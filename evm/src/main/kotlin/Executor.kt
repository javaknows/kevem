package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.gas.GasCostCalculator
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.ops.*
import java.math.BigInteger

// TODO - don't accept certain operations depending on fork version configured

class Executor(private val gasCostCalculator: GasCostCalculator) {
    tailrec fun executeAll(executionCtx: ExecutionContext): ExecutionContext =
        if (executionCtx.completed) executionCtx
        else executeAll(executeNextOpcode(executionCtx))

    internal fun executeNextOpcode(executionCtx: ExecutionContext): ExecutionContext = with(executionCtx) {
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
                isStackOverflow(opcode, currentCallCtx) -> HaltOps.fail(
                    executionCtx, EvmError(ErrorCode.STACK_OVERFLOW, "Stack overflow")
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

    private fun isStackOverflow(opcode: Opcode?, callCtx: CallContext) =
        (callCtx.stack.size() - Opcode.numArgs(opcode) + Opcode.numReturn(opcode)) > 1024

    private fun isEndOfContract(callCtx: CallContext?) =
        callCtx != null && callCtx.currentLocation !in callCtx.code.indices

    private fun processOpcode(currentContext: ExecutionContext, opcode: Opcode): ExecutionContext {
        val updatedContext = when (opcode) {
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
            Opcode.SHA3 -> CryptoOps.sha3(currentContext)
            Opcode.ADDRESS -> CallerOps.address(currentContext)
            Opcode.BALANCE -> CallerOps.balance(currentContext)
            Opcode.ORIGIN -> CallerOps.origin(currentContext)
            Opcode.CALLER -> CallerOps.caller(currentContext)
            Opcode.CALLVALUE -> CallerOps.callValue(currentContext)
            Opcode.CALLDATALOAD -> DataOps.callDataLoad(currentContext)
            Opcode.CALLDATASIZE -> DataOps.callDataSize(currentContext)
            Opcode.CALLDATACOPY -> DataOps.callDataCopy(currentContext)
            Opcode.CODESIZE -> DataOps.codeSize(currentContext)
            Opcode.CODECOPY -> DataOps.codeCopy(currentContext)
            Opcode.GASPRICE -> CallerOps.gasPrice(currentContext)
            Opcode.EXTCODESIZE -> DataOps.extCodeSize(currentContext)
            Opcode.EXTCODECOPY -> DataOps.extCodeCopy(currentContext)
            Opcode.RETURNDATASIZE -> DataOps.returnDataSize(currentContext)
            Opcode.RETURNDATACOPY -> DataOps.returnDataCopy(currentContext)
            Opcode.BLOCKHASH -> BlockOps.blockHash(currentContext)
            Opcode.COINBASE -> BlockOps.coinBase(currentContext)
            Opcode.TIMESTAMP -> BlockOps.timeStamp(currentContext)
            Opcode.NUMBER -> BlockOps.number(currentContext)
            Opcode.DIFFICULTY -> BlockOps.difficulty(currentContext)
            Opcode.GASLIMIT -> BlockOps.gasLimit(currentContext)
            Opcode.POP -> EvmOps.pop(currentContext)
            Opcode.MLOAD -> MemoryOps.mload(currentContext)
            Opcode.MSTORE -> MemoryOps.mstore(currentContext)
            Opcode.MSTORE8 -> MemoryOps.mstore8(currentContext)
            Opcode.SLOAD -> StorageOps.sLoad(currentContext)
            Opcode.SSTORE -> StorageOps.sStore(currentContext)
            Opcode.JUMP -> JumpOps.jump(currentContext)
            Opcode.JUMPI -> JumpOps.jumpi(currentContext)
            Opcode.PC -> CallerOps.programCounter(currentContext)
            Opcode.MSIZE -> MemoryOps.msize(currentContext)
            Opcode.GAS -> CallerOps.gas(currentContext)
            Opcode.JUMPDEST -> JumpOps.jumpDest(currentContext)
            Opcode.PUSH1 -> EvmOps.push(currentContext, 1)
            Opcode.PUSH2 -> EvmOps.push(currentContext, 2)
            Opcode.PUSH3 -> EvmOps.push(currentContext, 3)
            Opcode.PUSH4 -> EvmOps.push(currentContext, 4)
            Opcode.PUSH5 -> EvmOps.push(currentContext, 5)
            Opcode.PUSH6 -> EvmOps.push(currentContext, 6)
            Opcode.PUSH7 -> EvmOps.push(currentContext, 7)
            Opcode.PUSH8 -> EvmOps.push(currentContext, 8)
            Opcode.PUSH9 -> EvmOps.push(currentContext, 9)
            Opcode.PUSH10 -> EvmOps.push(currentContext, 10)
            Opcode.PUSH11 -> EvmOps.push(currentContext, 11)
            Opcode.PUSH12 -> EvmOps.push(currentContext, 12)
            Opcode.PUSH13 -> EvmOps.push(currentContext, 13)
            Opcode.PUSH14 -> EvmOps.push(currentContext, 14)
            Opcode.PUSH15 -> EvmOps.push(currentContext, 15)
            Opcode.PUSH16 -> EvmOps.push(currentContext, 16)
            Opcode.PUSH17 -> EvmOps.push(currentContext, 17)
            Opcode.PUSH18 -> EvmOps.push(currentContext, 18)
            Opcode.PUSH19 -> EvmOps.push(currentContext, 19)
            Opcode.PUSH20 -> EvmOps.push(currentContext, 20)
            Opcode.PUSH21 -> EvmOps.push(currentContext, 21)
            Opcode.PUSH22 -> EvmOps.push(currentContext, 22)
            Opcode.PUSH23 -> EvmOps.push(currentContext, 23)
            Opcode.PUSH24 -> EvmOps.push(currentContext, 24)
            Opcode.PUSH25 -> EvmOps.push(currentContext, 25)
            Opcode.PUSH26 -> EvmOps.push(currentContext, 26)
            Opcode.PUSH27 -> EvmOps.push(currentContext, 27)
            Opcode.PUSH28 -> EvmOps.push(currentContext, 28)
            Opcode.PUSH29 -> EvmOps.push(currentContext, 29)
            Opcode.PUSH30 -> EvmOps.push(currentContext, 30)
            Opcode.PUSH31 -> EvmOps.push(currentContext, 31)
            Opcode.PUSH32 -> EvmOps.push(currentContext, 32)
            Opcode.DUP1 -> EvmOps.dup(currentContext, 0)
            Opcode.DUP2 -> EvmOps.dup(currentContext, 1)
            Opcode.DUP3 -> EvmOps.dup(currentContext, 2)
            Opcode.DUP4 -> EvmOps.dup(currentContext, 3)
            Opcode.DUP5 -> EvmOps.dup(currentContext, 4)
            Opcode.DUP6 -> EvmOps.dup(currentContext, 5)
            Opcode.DUP7 -> EvmOps.dup(currentContext, 6)
            Opcode.DUP8 -> EvmOps.dup(currentContext, 7)
            Opcode.DUP9 -> EvmOps.dup(currentContext, 8)
            Opcode.DUP10 -> EvmOps.dup(currentContext, 9)
            Opcode.DUP11 -> EvmOps.dup(currentContext, 10)
            Opcode.DUP12 -> EvmOps.dup(currentContext, 11)
            Opcode.DUP13 -> EvmOps.dup(currentContext, 12)
            Opcode.DUP14 -> EvmOps.dup(currentContext, 13)
            Opcode.DUP15 -> EvmOps.dup(currentContext, 14)
            Opcode.DUP16 -> EvmOps.dup(currentContext, 15)
            Opcode.SWAP1 -> EvmOps.swap(currentContext, 0)
            Opcode.SWAP2 -> EvmOps.swap(currentContext, 1)
            Opcode.SWAP3 -> EvmOps.swap(currentContext, 2)
            Opcode.SWAP4 -> EvmOps.swap(currentContext, 3)
            Opcode.SWAP5 -> EvmOps.swap(currentContext, 4)
            Opcode.SWAP6 -> EvmOps.swap(currentContext, 5)
            Opcode.SWAP7 -> EvmOps.swap(currentContext, 6)
            Opcode.SWAP8 -> EvmOps.swap(currentContext, 7)
            Opcode.SWAP9 -> EvmOps.swap(currentContext, 8)
            Opcode.SWAP10 -> EvmOps.swap(currentContext, 9)
            Opcode.SWAP11 -> EvmOps.swap(currentContext, 10)
            Opcode.SWAP12 -> EvmOps.swap(currentContext, 11)
            Opcode.SWAP13 -> EvmOps.swap(currentContext, 12)
            Opcode.SWAP14 -> EvmOps.swap(currentContext, 13)
            Opcode.SWAP15 -> EvmOps.swap(currentContext, 14)
            Opcode.SWAP16 -> EvmOps.swap(currentContext, 15)
            Opcode.LOG0 -> EvmOps.log(currentContext, 0)
            Opcode.LOG1 -> EvmOps.log(currentContext, 1)
            Opcode.LOG2 -> EvmOps.log(currentContext, 2)
            Opcode.LOG3 -> EvmOps.log(currentContext, 3)
            Opcode.LOG4 -> EvmOps.log(currentContext, 4)
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