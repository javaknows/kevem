package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.ops.*

// TODO - deduct gas. Deduct opcode base gas here and computed gas in the opcode handling
// TODO - fail if out of gas
// TODO - ensure max stack depth won't be reached
// TODO - in HaltOps when halting in DELEGATECALL or CALLCODE check if context values need to be copied down the stack
// TODO - don't accept certain operations depending on fork version configured

class Executor {
    tailrec fun executeAll(executionCtx: ExecutionContext): ExecutionContext =
        if (executionCtx.completed) executionCtx
        else executeAll(executeNextOpcode(executionCtx))

    fun executeNextOpcode(executionCtx: ExecutionContext): ExecutionContext = with(executionCtx) {
        if (isEndOfContract(currentCallContextOrNull))
            HaltOps.stop(executionCtx)
        else {
            val byteCode = currentCallContext.code[currentLocation]
            val opcode = Opcode.byCode[byteCode]

            when {
                isStackUnderflow(opcode, currentCallContext) -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.STACK_DEPTH, "Stack not deep enough for $opcode")
                )
                isModifyInStaticCall(opcode, currentCallContext) -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.STATE_CHANGE_STATIC_CALL, "$opcode not allowed in static call")
                )
                else -> processOpcode(executionCtx, opcode, byteCode)
            }
        }
    }

    private fun isModifyInStaticCall(opcode: Opcode?, currentCallCtx: CallContext) =
        currentCallCtx.type == CallType.STATICCALL && !Opcode.isAllowedInStatic(opcode)

    private fun isStackUnderflow(opcode: Opcode?, callCtx: CallContext) =
        Opcode.numArgs(opcode) > callCtx.stack.size()

    private fun isEndOfContract(callCtx: CallContext?) =
        callCtx != null && callCtx.currentLocation !in callCtx.code.indices

    private fun processOpcode(currentContext: ExecutionContext, opcode: Opcode?, byteCode: Byte): ExecutionContext {
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
                Opcode.SHA3 -> biStackOp(currentContext) { a, b ->
                    val bytes = memory.get(a.toInt(), b.toInt())
                    keccak256(bytes)
                }
                Opcode.ADDRESS -> {
                    val call = callStack.last()
                    val newStack = stack.pushWord(Word.coerceFrom(call.contract.address.value))

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
                Opcode.CALLER -> {
                    val call = callStack.last { it.type != CallType.DELEGATECALL }
                    val newStack = stack.pushWord(call.caller.toWord())

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLVALUE -> {
                    val call = callStack.last { it.type != CallType.DELEGATECALL }
                    val newStack = stack.pushWord(Word.coerceFrom(call.value))

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLDATALOAD -> {
                    val call = callStack.last()

                    val (position, newStack) = stack.popWord()
                    val start = position.toInt().coerceIn(0, call.callData.size)
                    val end = (position.toInt() + 32).coerceIn(call.callData.size, 32)

                    val data = call.callData.subList(start, end)
                    val finalStack = newStack.pushWord(Word.coerceFrom(data))

                    currentContext.updateCurrentCallCtx(stack = finalStack)
                }
                Opcode.CALLDATASIZE -> {
                    val size = callStack.last().callData.size
                    val newStack = stack.pushWord(Word.coerceFrom(size))

                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CALLDATACOPY -> {
                    // TODO - how should it handle out of range ?
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val call = callStack.last()
                    val data = call.callData.subList(from, from + size)
                    val newMemory = memory.set(to, data)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.CODESIZE -> {
                    // TODO - what should hapen if it is a DELEGATECALL?
                    val call = callStack.last()
                    val newStack = stack.pushWord(Word.coerceFrom(call.contract.code.size))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.CODECOPY -> {
                    // TODO - how should it handle out of range ?
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val call = callStack.last()
                    val data = call.contract.code.subList(from, from + size)
                    val newMemory = memory.set(to, data)

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
                    // TODO - how should it handle out of range ?
                    val (elements, newStack) = stack.popWords(4)
                    val (address, to, from, size) = elements

                    val code = evmState.codeAt(address.toAddress())
                    val data = code.subList(from.toInt(), from.toInt() + size.toInt())
                    val newMemory = memory.set(to.toInt(), data)

                    currentContext.updateCurrentCallCtx(stack = newStack, memory = newMemory)
                }
                Opcode.RETURNDATASIZE -> {
                    val newStack = stack.pushWord(Word.coerceFrom(lastReturnData.size))
                    currentContext.updateCurrentCallCtx(stack = newStack)
                }
                Opcode.RETURNDATACOPY -> {
                    // TODO - how should it handle out of range ?
                    val (elements, newStack) = stack.popWords(3)
                    val (to, from, size) = elements.map { it.toInt() }
                    val data = lastReturnData.subList(from, from + size)
                    val newMemory = memory.set(to, data)

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
                    val newStack = stack.pushWord(Word.coerceFrom(currentCallContext.gasRemaining))
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
                else -> HaltOps.invalid(currentContext, "Invalid instruction - unknown opcode $byteCode")
            }
        }

        return incrementLocation(updatedContext, opcode)
    }

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