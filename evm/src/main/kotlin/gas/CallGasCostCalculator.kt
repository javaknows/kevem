package com.gammadex.kevin.evm.gas

import com.gammadex.kevin.evm.model.Address
import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.numbers.BigIntMath
import java.math.BigInteger
import java.math.BigInteger.ZERO

class CallGasCostCalculator {

    fun cost(gas: BigInteger, to: Address, value: BigInteger, executionCtx: ExecutionContext): BigInteger {
        val (gasCap, extraFee) = calculateGasCapAndExtraFee(value, to, gas, executionCtx)

        return gasCap + extraFee
    }

    private fun calculateGasCapAndExtraFee(
        value: BigInteger,
        to: Address,
        gas: BigInteger,
        executionCtx: ExecutionContext
    ): Pair<BigInteger, BigInteger> {
        val newAccountFee = if (value > ZERO && !executionCtx.evmState.accountExists(to)) GasCost.NewAccount.cost else 0
        val transferFee = if (value > ZERO) GasCost.CallValue.cost else 0
        val extraFee = (GasCost.Call.cost + newAccountFee + transferFee).toBigInteger()

        val callerGas = executionCtx.currentCallContext.gas
        val gasCap = if (callerGas > extraFee) BigIntMath.min(callerGas - extraFee, gas) else gas

        return Pair(gasCap, extraFee)
    }

}