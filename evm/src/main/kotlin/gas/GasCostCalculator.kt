package com.gammadex.kevin.evm.gas

import com.gammadex.kevin.evm.Opcode
import com.gammadex.kevin.evm.model.Address
import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.numbers.BigIntMath
import java.math.BigInteger

class GasCostCalculator(
    private val baseGasCostCalculator: BaseGasCostCalculator,
    private val memoryUsageCostCalculator: MemoryUsageGasCostCalculator
) {
    fun calculateCost(opcode: Opcode, executionCtx: ExecutionContext): BigInteger {
        val baseCost = baseGasCostCalculator.baseCost(opcode, executionCtx)
        val memCost = memoryUsageCostCalculator.memoryUsageCost(opcode, executionCtx)

        return baseCost + memCost
    }
}

class MemoryUsageGasCalc {

    /**
     * C_mem in the yellow paper
     */
    fun memoryCost(highestByteIndex: Int): Int {
        val numWords = Math.ceil(highestByteIndex / 32.toDouble()).toInt()

        return (GasCost.Memory.cost * numWords) + ((numWords * numWords) / 512)
    }
}

class CallGasCostCalc {
    fun calcCallCostAndCallGas(
        value: BigInteger,
        to: Address,
        gas: BigInteger,
        executionCtx: ExecutionContext
    ): Pair<BigInteger, BigInteger> {
        val newAccountFee =
            if (value > BigInteger.ZERO && !executionCtx.evmState.accountExists(to)) GasCost.NewAccount.cost
            else 0

        val transferFee =
            if (value > BigInteger.ZERO) GasCost.CallValue.cost
            else 0

        val extraFee = (GasCost.Call.cost + newAccountFee + transferFee).toBigInteger()

        // TODO - gas cap is in the yellow paper but not in parity or ganache - look into this maybe an EIP defines different behvaiour
        /*
        val callerGas = executionCtx.currentCallCtx.gas
        val gasCap =
            if (callerGas > extraFee) BigIntMath.min(allButOne64th(callerGas - extraFee), gas)
            else gas
            */
        val gasCap = gas

        val callGas =
            if (value > BigInteger.ZERO) gasCap + GasCost.CallStipend.cost.toBigInteger()
            else gasCap

        val callCost = gasCap + extraFee

        return Pair(callCost, callGas)
    }

    /**
     * L(n) in the yellow papaer (298)
     */
    private fun allButOne64th(num: BigInteger) = num - num / BigInteger("64")
}