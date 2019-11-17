package com.gammadex.kevin.evm.gas

import com.gammadex.kevin.evm.Opcode
import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.Opcode.*
import com.gammadex.kevin.evm.numbers.BigIntMath
import com.gammadex.kevin.evm.numbers.logn
import java.math.BigInteger
import com.gammadex.kevin.evm.ops.CallOps

class BaseGasCostCalculator(
    private val callGasCostCalc: CallGasCostCalc
) {

    fun baseCost(opcode: Opcode, executionContext: ExecutionContext): BigInteger =
        if (opcode.cost == GasCost.Formula) {
            when (opcode) {
                EXP -> expCost(executionContext)
                SHA3 -> sha3Cost(executionContext)
                CALLDATACOPY -> dataCopyCost(executionContext)
                CODECOPY -> dataCopyCost(executionContext)
                EXTCODECOPY -> extCodeCopyCost(executionContext)
                RETURNDATACOPY -> dataCopyCost(executionContext)
                SSTORE -> sStoreCost(executionContext)
                LOG0 -> logCost(executionContext, 0)
                LOG1 -> logCost(executionContext, 1)
                LOG2 -> logCost(executionContext, 2)
                LOG3 -> logCost(executionContext, 3)
                LOG4 -> logCost(executionContext, 4)
                CALL -> callCost(executionContext, true)
                CALLCODE -> callCost(executionContext, true)
                DELEGATECALL -> callCost(executionContext, false)
                STATICCALL -> callCost(executionContext, false)
                SUICIDE -> suicideCost(executionContext)
                else -> throw RuntimeException("Don't now how to compute gas cost for $opcode")
            }
        } else opcode.cost.cost.toBigInteger()

    private fun callCost(executionContext: ExecutionContext, withValue: Boolean): BigInteger {
        val callArgs = CallOps.peekCallArgsFromStack(executionContext.stack, withValue)

        return with(callArgs) {
            val (callCost, _) = callGasCostCalc.calcCallCostAndCallGas(value, address, gas, executionContext)

            callCost
        }
    }

    // TODO - use GasCost enum for all these magic numbers

    /**
     * 5000 + ((create_new_account) ? 25000 : 0)
     */
    private fun suicideCost(executionContext: ExecutionContext): BigInteger {
        val (address) = executionContext.currentCallCtx.stack.peekWords(1).map { it.toAddress() }

        val accountExists = executionContext.evmState.accountExists(address)

        val newAccountCharge = if (accountExists) BigInteger.ZERO else BigInteger("25000")
        return BigInteger("5000") + newAccountCharge
    }

    /**
     * 375 + 8 * (number of bytes in log data)
     */
    private fun logCost(executionContext: ExecutionContext, numTopics: Int): BigInteger {
        val elements = executionContext.currentCallCtx.stack.peekWords(2 + numTopics)
        val size = elements.take(2).last().toInt()

        return (GasCost.Log.cost +
                GasCost.LogTopic.cost * numTopics +
                GasCost.LogData.cost * size).toBigInteger()
    }

    /**
     * ((value != 0) && (storage_location == 0)) ? 20000 : 5000
     */
    private fun sStoreCost(executionContext: ExecutionContext): BigInteger {
        val (location, value) = executionContext.currentCallCtx.stack.peekWords(3).map { it.toBigInt() }

        val storageAddress = executionContext.currentCallCtx.storageAddress
            ?: throw RuntimeException("can't determine contract address")
        val oldValue = executionContext.evmState.storageAt(storageAddress, location.toInt())

        return if (value != BigInteger.ZERO && oldValue.toBigInt() == BigInteger.ZERO)
            GasCost.SSet.cost.toBigInteger()
        else
            GasCost.SReset.cost.toBigInteger()
    }

    /**
     * 700 + 3 * (number of words copied, rounded up)
     */
    private fun extCodeCopyCost(executionContext: ExecutionContext): BigInteger {
        val (_, _, _, size) = executionContext.currentCallCtx.stack.peekWords(4)

        return BigInteger("700") + BigInteger("3") * numWordsRoundedUp(size.toBigInt())
    }

    private fun dataCopyCost(executionContext: ExecutionContext): BigInteger {
        val (_, _, size) = executionContext.currentCallCtx.stack.peekWords(3)

        return GasCost.Base.cost.toBigInteger() + GasCost.Copy.cost.toBigInteger() * numWordsRoundedUp(size.toBigInt())
    }

    /**
     * 30 + 6 * (size of input in words)
     */
    private fun sha3Cost(executionContext: ExecutionContext): BigInteger {
        val (start, end) = executionContext.currentCallCtx.stack.peekWords(2)
        val numBytes = BigIntMath.max(end.toBigInt() - start.toBigInt(), BigInteger.ZERO)
        val numWords = numWordsRoundedUp(numBytes)

        return BigInteger("30") + BigInteger("6") * numWords
    }

    /**
     * (exp == 0) ? 10 : (10 + 10 * (1 + log256(exp)))
     */
    private fun expCost(executionContext: ExecutionContext): BigInteger {
        val elements = executionContext.currentCallCtx.stack.peekWords(2)
        val (_, exp) = elements.map { it.toBigInt() }

        return if (exp == BigInteger.ZERO) BigInteger.TEN
        else BigInteger.TEN + BigInteger.TEN * (BigInteger.ONE + logn(exp, BigInteger("256")))
    }

    private fun numWordsRoundedUp(numBytes: BigInteger) = BigIntMath.divRoundUp(numBytes, BigInteger("32"))
}