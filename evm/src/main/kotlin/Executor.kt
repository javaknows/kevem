package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.gas.GasCostCalculator
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.ops.*
import java.math.BigInteger

// TODO - ensure max stack depth won't be reached
// TODO - don't accept certain operations depending on fork version configured

class Executor(private val gasCostCalculator: GasCostCalculator) {
    tailrec fun executeAll(executionCtx: ExecutionContext): ExecutionContext =
        if (executionCtx.completed) executionCtx
        else executeAll(executeNextOpcode(executionCtx))

    fun executeNextOpcode(executionCtx: ExecutionContext): ExecutionContext = with(executionCtx) {
        if (isEndOfContract(currentCallContextOrNull))
            HaltOps.stop(executionCtx)
        else {
            val byteCode = currentCallCtx.code[currentLocation]
            val opcode = Opcode.byCode[byteCode]

            when {
                opcode == null -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.INVALID_INSTRUCTION, "Invalid instruction: $byteCode")
                )
                isStackUnderflow(opcode, currentCallCtx) -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.STACK_UNDERFLOW, "Stack not deep enough for $opcode")
                )
                isModifyInStaticCall(opcode, executionCtx) -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.STATE_CHANGE_STATIC_CALL, "$opcode not allowed in static call")
                )
                else -> consumeGasAndProcessOpcode(opcode, executionCtx)
            }
        }
    }

    private fun consumeGasAndProcessOpcode(opcode: Opcode, executionCtx: ExecutionContext): ExecutionContext {
        val (isOutOfGas, updatedExecutionCtx) = consumeGas(opcode, executionCtx)
        return if (isOutOfGas) HaltOps.fail(
            updatedExecutionCtx, EvmError(ErrorCode.OUT_OF_GAS, "Out of gas")
        ) else processOpcode(updatedExecutionCtx, opcode)
    }

    private fun consumeGas(opcode: Opcode, executionCtx: ExecutionContext): Pair<Boolean, ExecutionContext> {
        val cost = gasCostCalculator.calculateCost(opcode, executionCtx)
        val isOutOfGas = cost > executionCtx.currentCallCtx.gasRemaining

        val newCtx = executionCtx.updateCurrentCallCtx(
            gasUsed = executionCtx.currentCallCtx.gasUsed + cost
        )

        return Pair(isOutOfGas, newCtx)
    }

    private fun isModifyInStaticCall(opcode: Opcode?, executionCtx: ExecutionContext): Boolean = when {
        executionCtx.currentCallCtx.type != CallType.STATICCALL -> false
        opcode == Opcode.CALL && executionCtx.currentCallCtx.stack.peekWord(2).toBigInt() > BigInteger.ZERO -> true
        else -> !Opcode.isAllowedInStatic(opcode)
    }

    private fun isStackUnderflow(opcode: Opcode?, callCtx: CallContext) =
        Opcode.numArgs(opcode) > callCtx.stack.size()

    private fun isEndOfContract(callCtx: CallContext?) =
        callCtx != null && callCtx.currentLocation !in callCtx.code.indices

    private fun processOpcode(currentContext: ExecutionContext, opcode: Opcode): ExecutionContext {
        val updatedContext = with(currentContext) {
            when (opcode) {
                Opcode.STOP -> HaltOps.stop(currentContext)
                Opcode.ADD -> biStackOp(currentContext, VmMath::add)
                Opcode.MUL -> biStackOp(currentContext, VmMath::mul)
                Opcode.SUB -> biStackOp(currentContext, VmMath::sub)
                Opcode.DIV -> biStackOp(currentContext, VmMath::div)
                Opcode.SDIV -> biStackOp(currentContext, VmMath::sdiv)
                Opcode.MOD -> biStackOp(currentContext, VmMath::mod)
                Opcode.SMOD -> biStackOp(currentContext, VmMath::smod)
                Opcode.ADDMOD -> triStackOp(currentContext, VmMath::addMod)
                Opcode.MULMOD -> triStackOp(currentContext, VmMath::mulMod)
                Opcode.EXP -> biStackOp(currentContext, VmMath::exp)
                Opcode.SIGNEXTEND -> biStackOp(currentContext, VmMath::signExtend)
                Opcode.LT -> biStackOp(currentContext, VmMath::lt)
                Opcode.GT -> biStackOp(currentContext, VmMath::gt)
                Opcode.SLT -> biStackOp(currentContext, VmMath::slt)
                Opcode.SGT -> biStackOp(currentContext, VmMath::sgt)
                Opcode.EQ -> biStackOp(currentContext, VmMath::eq)
                Opcode.ISZERO -> uniStackOp(currentContext, VmMath::isZero)
                Opcode.AND -> biStackOp(currentContext, VmMath::and)
                Opcode.OR -> biStackOp(currentContext, VmMath::or)
                Opcode.XOR -> biStackOp(currentContext, VmMath::xor)
                Opcode.NOT -> uniStackOp(currentContext, VmMath::not)
                Opcode.BYTE -> biStackOp(currentContext, VmMath::byte)
                Opcode.SHL -> biStackOp(currentContext, VmMath::shl)
                Opcode.SHR -> biStackOp(currentContext, VmMath::shr)
                Opcode.SAR -> biStackOp(currentContext, VmMath::sar)
                Opcode.SHA3 -> {
                    val (elements, newStack) = stack.popWords(2)
                    val (a, b) = elements.map { it.toInt() }
                    val (bytes, newMemory) = memory.read(a, b)
                    val finalStack = newStack.pushWord(keccak256(bytes))

                    currentContext.updateCurrentCallCtx(stack = finalStack, memory = newMemory)
                }
                Opcode.ADDRESS -> {
                    val call = callStack.last()
                    val contractAddress =
                        call.contractAddress ?: throw RuntimeException("can't determine contract address")
                    val newStack = stack.pushWord(Word.coerceFrom(contractAddress.value))

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.BALANCE -> {
                    val (popped, newStack) = stack.popWord()
                    val balance = evmState.balanceOf(popped.toAddress())
                    val finalStack = newStack.pushWord(Word.coerceFrom(balance))

                    currentContext.updateCurrentCallCtx(stack = finalStack)
                }
                Opcode.ORIGIN -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.origin.value))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLER -> { // TODO - just use caller
                    val call = callStack.last { it.type != CallType.DELEGATECALL }
                    val newStack = stack.pushWord(call.caller.toWord())

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLVALUE -> { // TODO - just use callvalue
                    val call = callStack.last { it.type != CallType.DELEGATECALL }
                    val newStack = stack.pushWord(Word.coerceFrom(call.value))

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLDATALOAD -> {
                    val call = callStack.last()

                    val (position, newStack) = stack.popWord()
                    val start = position.toInt().coerceIn(0, call.callData.size)
                    val end = (position.toInt() + 32).coerceIn(start, call.callData.size)
                    val append = Byte.Zero.repeat(32 - end + start)

                    val data = call.callData.subList(start, end) + append
                    val finalStack = newStack.pushWord(Word.coerceFrom(data))

                    currentContext.updateCurrentCallCtx(stack = finalStack)
                }
                Opcode.CALLDATASIZE -> {
                    val size = callStack.last().callData.size
                    val newStack = stack.pushWord(Word.coerceFrom(size))

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLDATACOPY -> {
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val call = callStack.last()
                    val data = call.callData.drop(from).take(size)
                    val paddedData = data + Byte.Zero.repeat(size - data.size)
                    val newMemory = memory.write(to, paddedData)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.CODESIZE -> {
                    val call = callStack.last()
                    val newStack = stack.pushWord(Word.coerceFrom(call.code.size))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CODECOPY -> {
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val call = callStack.last()
                    val data = call.code.drop(from).take(size)
                    val paddedData = data + Byte.Zero.repeat(size - data.size)
                    val newMemory = memory.write(to, paddedData)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.GASPRICE -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.gasPrice))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.EXTCODESIZE -> {
                    val (popped, newStack) = stack.popWord()
                    val code = evmState.codeAt(popped.toAddress())
                    val finalStack = newStack.pushWord(Word.coerceFrom(code.size))

                    currentContext.updateCurrentCallCtx(stack = finalStack)
                }
                Opcode.EXTCODECOPY -> {
                    val (elements, newStack) = stack.popWords(4)
                    val (address, to, from, size) = elements

                    val code = evmState.codeAt(address.toAddress())
                    val data = code.drop(from.toInt()).take(size.toInt())
                    val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size)
                    val newMemory = memory.write(to.toInt(), paddedData)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.RETURNDATASIZE -> {
                    val newStack = stack.pushWord(Word.coerceFrom(lastReturnData.size))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.RETURNDATACOPY -> {
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val data = lastReturnData.drop(from).take(size)
                    val paddedData = data + Byte.Zero.repeat(size - data.size)
                    val newMemory = memory.write(to, paddedData)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.BLOCKHASH -> uniStackOp(currentContext) {
                    previousBlocks.getOrDefault(it.toBigInt(), Word.Zero)
                }
                Opcode.COINBASE -> {
                    val newStack = stack.pushWord(coinBase.toWord())
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.TIMESTAMP -> {
                    val epoch = clock.instant().epochSecond
                    val newStack = stack.pushWord(Word.coerceFrom(epoch))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.NUMBER -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentBlock.number))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.DIFFICULTY -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentBlock.difficulty))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.GASLIMIT -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentBlock.gasLimit))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.POP -> pop(currentContext)
                Opcode.MLOAD -> MemoryOps.mload(currentContext)
                Opcode.MSTORE -> MemoryOps.mstore(currentContext)
                Opcode.MSTORE8 -> MemoryOps.mstore8(currentContext)
                Opcode.SLOAD -> StorageOps.sLoad(currentContext)
                Opcode.SSTORE -> StorageOps.sStore(currentContext)
                Opcode.JUMP -> JumpOps.jump(currentContext)
                Opcode.JUMPI -> JumpOps.jumpi(currentContext)
                Opcode.PC -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentLocation))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.MSIZE -> MemoryOps.msize(currentContext)
                Opcode.GAS -> {
                    val newStack = stack.pushWord(Word.coerceFrom(currentCallCtx.gasRemaining))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.JUMPDEST -> JumpOps.jumpDest(currentContext)
                Opcode.PUSH1 -> push(currentContext, 1)
                Opcode.PUSH2 -> push(currentContext, 2)
                Opcode.PUSH3 -> push(currentContext, 3)
                Opcode.PUSH4 -> push(currentContext, 4)
                Opcode.PUSH5 -> push(currentContext, 5)
                Opcode.PUSH6 -> push(currentContext, 6)
                Opcode.PUSH7 -> push(currentContext, 7)
                Opcode.PUSH8 -> push(currentContext, 8)
                Opcode.PUSH9 -> push(currentContext, 9)
                Opcode.PUSH10 -> push(currentContext, 10)
                Opcode.PUSH11 -> push(currentContext, 11)
                Opcode.PUSH12 -> push(currentContext, 12)
                Opcode.PUSH13 -> push(currentContext, 13)
                Opcode.PUSH14 -> push(currentContext, 14)
                Opcode.PUSH15 -> push(currentContext, 15)
                Opcode.PUSH16 -> push(currentContext, 16)
                Opcode.PUSH17 -> push(currentContext, 17)
                Opcode.PUSH18 -> push(currentContext, 18)
                Opcode.PUSH19 -> push(currentContext, 19)
                Opcode.PUSH20 -> push(currentContext, 20)
                Opcode.PUSH21 -> push(currentContext, 21)
                Opcode.PUSH22 -> push(currentContext, 22)
                Opcode.PUSH23 -> push(currentContext, 23)
                Opcode.PUSH24 -> push(currentContext, 24)
                Opcode.PUSH25 -> push(currentContext, 25)
                Opcode.PUSH26 -> push(currentContext, 26)
                Opcode.PUSH27 -> push(currentContext, 27)
                Opcode.PUSH28 -> push(currentContext, 28)
                Opcode.PUSH29 -> push(currentContext, 29)
                Opcode.PUSH30 -> push(currentContext, 30)
                Opcode.PUSH31 -> push(currentContext, 31)
                Opcode.PUSH32 -> push(currentContext, 32)
                Opcode.DUP1 -> dup(currentContext, 0)
                Opcode.DUP2 -> dup(currentContext, 1)
                Opcode.DUP3 -> dup(currentContext, 2)
                Opcode.DUP4 -> dup(currentContext, 3)
                Opcode.DUP5 -> dup(currentContext, 4)
                Opcode.DUP6 -> dup(currentContext, 5)
                Opcode.DUP7 -> dup(currentContext, 6)
                Opcode.DUP8 -> dup(currentContext, 7)
                Opcode.DUP9 -> dup(currentContext, 8)
                Opcode.DUP10 -> dup(currentContext, 9)
                Opcode.DUP11 -> dup(currentContext, 10)
                Opcode.DUP12 -> dup(currentContext, 11)
                Opcode.DUP13 -> dup(currentContext, 12)
                Opcode.DUP14 -> dup(currentContext, 13)
                Opcode.DUP15 -> dup(currentContext, 14)
                Opcode.DUP16 -> dup(currentContext, 15)
                Opcode.SWAP1 -> swap(currentContext, 0)
                Opcode.SWAP2 -> swap(currentContext, 1)
                Opcode.SWAP3 -> swap(currentContext, 2)
                Opcode.SWAP4 -> swap(currentContext, 3)
                Opcode.SWAP5 -> swap(currentContext, 4)
                Opcode.SWAP6 -> swap(currentContext, 5)
                Opcode.SWAP7 -> swap(currentContext, 6)
                Opcode.SWAP8 -> swap(currentContext, 7)
                Opcode.SWAP9 -> swap(currentContext, 8)
                Opcode.SWAP10 -> swap(currentContext, 9)
                Opcode.SWAP11 -> swap(currentContext, 10)
                Opcode.SWAP12 -> swap(currentContext, 11)
                Opcode.SWAP13 -> swap(currentContext, 12)
                Opcode.SWAP14 -> swap(currentContext, 13)
                Opcode.SWAP15 -> swap(currentContext, 14)
                Opcode.SWAP16 -> swap(currentContext, 15)
                Opcode.LOG0 -> log(currentContext, 0)
                Opcode.LOG1 -> log(currentContext, 1)
                Opcode.LOG2 -> log(currentContext, 2)
                Opcode.LOG3 -> log(currentContext, 3)
                Opcode.LOG4 -> log(currentContext, 4)
                Opcode.CREATE -> CreateContractOps.create(currentContext)
                Opcode.CALL -> CallOps.call(currentContext)
                Opcode.CALLCODE -> CallOps.callCode(currentContext)
                Opcode.RETURN -> HaltOps.doReturn(currentContext)
                Opcode.DELEGATECALL -> CallOps.delegateCall(currentContext)
                Opcode.STATICCALL -> CallOps.staticCall(currentContext)
                Opcode.CREATE2 -> CreateContractOps.create2(currentContext)
                Opcode.REVERT -> HaltOps.revert(currentContext)
                Opcode.INVALID -> HaltOps.invalid(currentContext)
                Opcode.SUICIDE -> HaltOps.suicide(currentContext)
            }
        }

        return if (isStackOverflow(updatedContext))
            HaltOps.fail(updatedContext, EvmError(ErrorCode.STACK_OVERFLOW, "Stack overflow"))
        else
            incrementLocation(updatedContext, opcode)
    }

    private fun isStackOverflow(executionCtx: ExecutionContext) =
        executionCtx.callStack.isNotEmpty() && executionCtx.currentCallCtx.stack.size() > 1024

    private fun incrementLocation(executionCtx: ExecutionContext, opcode: Opcode?): ExecutionContext =
        when {
            Opcode.isHaltingOpcode(opcode) || Opcode.isJumpOpcode(opcode) || executionCtx.completed -> executionCtx
            Opcode.isCallOpcode(opcode) -> executionCtx.updatePreviousCallCtxIfPresent { ctx ->
                ctx.copy(currentLocation = ctx.currentLocation + Opcode.numBytes(opcode))
            }
            else -> executionCtx.updateCurrentCallCtxIfPresent { ctx ->
                ctx.copy(currentLocation = ctx.currentLocation + Opcode.numBytes(opcode))
            }
        }

    private fun uniStackOp(executionContext: ExecutionContext, op: (w1: Word) -> Word): ExecutionContext {
        val (a, newStack) = executionContext.stack.popWord()
        val finalStack = newStack.pushWord(op(a))

        return executionContext.updateCurrentCallCtx(stack = finalStack)
    }

    private fun biStackOp(executionContext: ExecutionContext, op: (w1: Word, w2: Word) -> Word): ExecutionContext {
        val (elements, newStack) = executionContext.stack.popWords(2)
        val (a, b) = elements
        val finalStack = newStack.pushWord(op(a, b))

        return executionContext.updateCurrentCallCtx(stack = finalStack)
    }

    private fun triStackOp(execCtx: ExecutionContext, op: (w1: Word, w2: Word, w3: Word) -> Word): ExecutionContext {
        val (elements, newStack) = execCtx.stack.popWords(3)
        val (a, b, c) = elements
        val finalStack = newStack.pushWord(op(a, b, c))

        return execCtx.updateCurrentCallCtx(stack = finalStack)
    }
}