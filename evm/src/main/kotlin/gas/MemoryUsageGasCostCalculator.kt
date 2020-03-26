package org.kevem.evm.gas

import org.kevem.evm.Opcode
import org.kevem.evm.Opcode.*
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Stack
import org.kevem.evm.numbers.BigIntMath
import org.kevem.evm.ops.CallOps
import java.lang.Integer.max
import java.math.BigInteger

class MemoryUsageGasCostCalculator(private val memoryUseGasCalc: MemoryUsageGasCalc) {

    fun memoryUsageCost(opcode: Opcode, executionCtx: ExecutionContext): BigInteger = when (opcode) {
        CALL -> callCost(executionCtx, true)
        CALLCODE -> callCost(executionCtx, true)
        DELEGATECALL -> callCost(executionCtx, false)
        STATICCALL -> callCost(executionCtx, false)
        SHA3 -> calculate(executionCtx) {
            val (from, length) = it.peekWords(2)
            highestByteOrNull(from.toBigInt(), length.toInt())
        }
        CALLDATACOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3)
            highestByteOrNull(to.toBigInt(), size.toInt())
        }
        EXTCODECOPY -> calculate(executionCtx) {
            val (_, to, _, size) = it.peekWords(4)
            highestByteOrNull(to.toBigInt(), size.toInt())
        }
        RETURNDATACOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3)
            highestByteOrNull(to.toBigInt(), size.toInt())
        }
        CODECOPY -> calculate(executionCtx) {
            val (to, _, size) = it.peekWords(3)
            highestByteOrNull(to.toBigInt(), size.toInt())
        }
        MLOAD -> calculate(executionCtx) {
            val (loc) = it.peekWords(1).map { w -> w.toBigInt() }
            loc + 32.toBigInteger()
        }
        MSTORE -> calculate(executionCtx) {
            val (loc, _) = it.peekWords(2).map { w -> w.toBigInt() }
            loc + 32.toBigInteger()
        }
        MSTORE8 -> calculate(executionCtx) {
            val (loc, _) = it.peekWords(2).map { w -> w.toBigInt() }
            loc + 1.toBigInteger()
        }
        LOG0 -> logCost(executionCtx)
        LOG1 -> logCost(executionCtx)
        LOG2 -> logCost(executionCtx)
        LOG3 -> logCost(executionCtx)
        LOG4 -> logCost(executionCtx)
        CREATE -> calculate(executionCtx) {
            val (_, loc, size) = it.peekWords(3)
            highestByteOrNull(loc.toBigInt(), size.toInt())
        }
        RETURN -> calculate(executionCtx) {
            val (loc, size) = it.peekWords(2)
            highestByteOrNull(loc.toBigInt(), size.toInt())
        }
        CREATE2 -> calculate(executionCtx) {
            val (_, _, loc, size) = it.peekWords(4)
            highestByteOrNull(loc.toBigInt(), size.toInt())
        }
        REVERT -> calculate(executionCtx) {
            val (loc, size) = it.peekWords(2)
            highestByteOrNull(loc.toBigInt(), size.toInt())
        }
        else -> BigInteger.ZERO
    }

    private fun logCost(executionContext: ExecutionContext): BigInteger = calculate(executionContext) {
        val (loc, size) = it.peekWords(2).map { w -> w.toBigInt() }
        loc + size
    }

    private fun callCost(executionContext: ExecutionContext, withValue: Boolean): BigInteger =
        calculate(executionContext) {
            val callArgs = CallOps.peekCallArgsFromStack(executionContext.stack, withValue)

            val readMaxByte = highestByteOrNull(callArgs.inLocation, callArgs.inSize)
            val writeMaxByte = highestByteOrNull(callArgs.outLocation, callArgs.outSize)

            nullSafeMax(readMaxByte, writeMaxByte)
        }

    private fun calculate(executionContext: ExecutionContext, maxReferenced: (Stack) -> BigInteger?): BigInteger {
        val maxByteReferenced = maxReferenced(executionContext.stack)

        return if (maxByteReferenced != null) {
            val currentMax = executionContext.currentCallCtx.memory.maxIndex

            if (currentMax == null || maxByteReferenced > currentMax) {
                memoryUseGasCalc.memoryCost(maxByteReferenced) - memoryUseGasCalc.memoryCost(
                    currentMax ?: BigInteger.ZERO
                )
            } else BigInteger.ZERO
        } else BigInteger.ZERO
    }

    private fun nullSafeMax(a: BigInteger?, b: BigInteger?) = when {
        a == null -> b
        b == null -> a
        else -> BigIntMath.max(a, b)
    }

    private fun highestByteOrNull(from: BigInteger, length: Int): BigInteger? =
        if (length > 0) from + length.toBigInteger()
        else null
}

