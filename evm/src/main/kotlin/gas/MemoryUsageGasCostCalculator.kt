package org.kevm.evm.gas

import org.kevm.evm.Opcode
import org.kevm.evm.Opcode.*
import org.kevm.evm.model.ExecutionContext
import org.kevm.evm.model.Stack
import org.kevm.evm.ops.CallOps
import java.lang.Integer.max
import java.math.BigInteger

class MemoryUsageGasCostCalculator(private val memoryUseGasCalc: MemoryUsageGasCalc) {

    fun memoryUsageCost(opcode: Opcode, executionCtx: ExecutionContext): BigInteger = when (opcode) {
        CALL -> callCost(executionCtx, true)
        CALLCODE -> callCost(executionCtx, true)
        DELEGATECALL -> callCost(executionCtx, false)
        STATICCALL -> callCost(executionCtx, false)
        SHA3 -> calculate(executionCtx) {
            val (from, length) = it.peekWords(2).map { w -> w.toInt() }
            highestByteOrNull(from, length)
        }
        CALLDATACOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3).map { w -> w.toInt() }
            highestByteOrNull(to, size)
        }
        EXTCODECOPY -> calculate(executionCtx) {
            val (_, to, _, size) = it.peekWords(4).map { w -> w.toInt() }
            highestByteOrNull(to, size)
        }
        RETURNDATACOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3).map { w -> w.toInt() }
            highestByteOrNull(to, size)
        }
        CODECOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3).map { w -> w.toInt() }
            highestByteOrNull(to, size)
        }
        MLOAD -> calculate(executionCtx) {
            val (loc) = it.peekWords(1).map { w -> w.toInt() }
            loc + 32
        }
        MSTORE -> calculate(executionCtx) {
            val (loc, _) = it.peekWords(2).map { w -> w.toInt() }
            loc + 32
        }
        MSTORE8 -> calculate(executionCtx) {
            val (loc, _) = it.peekWords(2).map { w -> w.toInt() }
            loc + 1
        }
        LOG0 -> logCost(executionCtx)
        LOG1 -> logCost(executionCtx)
        LOG2 -> logCost(executionCtx)
        LOG3 -> logCost(executionCtx)
        LOG4 -> logCost(executionCtx)
        CREATE -> calculate(executionCtx) {
            val (_, loc, size) = it.peekWords(3).map { w -> w.toInt() }
            highestByteOrNull(loc, size)
        }
        RETURN -> calculate(executionCtx) {
            val (loc, size) = it.peekWords(2).map { w -> w.toInt() }
            highestByteOrNull(loc, size)
        }
        CREATE2 -> calculate(executionCtx) {
            val (_, _, loc, size) = it.peekWords(4).map { w -> w.toInt() }
            highestByteOrNull(loc, size)
        }
        REVERT -> calculate(executionCtx) {
            val (loc, size) = it.peekWords(2).map { w -> w.toInt() }
            highestByteOrNull(loc, size)
        }
        else -> BigInteger.ZERO
    }

    private fun logCost(executionContext: ExecutionContext): BigInteger = calculate(executionContext) {
        val (loc, size) = it.peekWords(2).map { w -> w.toInt() }
        loc + size
    }

    private fun callCost(executionContext: ExecutionContext, withValue: Boolean): BigInteger =
        calculate(executionContext) {
            val callArgs = CallOps.peekCallArgsFromStack(executionContext.stack, withValue)

            val readMaxByte = highestByteOrNull(callArgs.inSize, callArgs.inLocation)
            val writeMaxByte = highestByteOrNull(callArgs.outSize, callArgs.outLocation)

            nullSafeMax(readMaxByte, writeMaxByte)
        }

    private fun calculate(executionContext: ExecutionContext, maxReferenced: (Stack) -> Int?): BigInteger {
        val maxByteReferenced = maxReferenced(executionContext.stack)

        val memCost =
            if (maxByteReferenced != null) {
                val currentMax = executionContext.currentCallCtx.memory.maxIndex

                if (currentMax == null || maxByteReferenced > currentMax) {
                    memoryUseGasCalc.memoryCost(maxByteReferenced) - memoryUseGasCalc.memoryCost(currentMax ?: 0)
                } else 0
            } else 0

        return memCost.toBigInteger()
    }

    private fun nullSafeMax(a: Int?, b: Int?) = when {
        a == null -> b
        b == null -> a
        else -> max(a, b)
    }

    private fun highestByteOrNull(from: Int, length: Int) =
        if (length > 0) from + length
        else null
}

