package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.Opcode.*

class GasCostCalculator {

    fun calculate(opcode: Opcode, executionContext: ExecutionContext) =
        if (opcode.priceType == GasPriceType.Formula) {
            when (opcode) {
                EXP -> calculateExpCost(executionContext)
                SHA3 -> TODO()
                CALLDATACOPY -> TODO()
                CODECOPY -> TODO()
                EXTCODECOPY -> TODO()
                SSTORE -> TODO()
                LOG0 -> TODO()
                LOG1 -> TODO()
                LOG2 -> TODO()
                LOG3 -> TODO()
                LOG4 -> TODO()
                CALL -> TODO()
                CALLCODE -> TODO()
                DELEGATECALL -> TODO()
                STATICCALL -> TODO()
                SUICIDE -> TODO()
                else -> TODO()
            }
        } else opcode.priceType.cost

    private fun calculateExpCost(executionContext: ExecutionContext): Int {
        val elements = executionContext.currentCallContext.stack.peekWords(2)
        val (n, exp) = elements.map { it.toBigInt() }

        

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}