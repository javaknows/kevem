package org.kevem.evm.gas

import org.kevem.evm.EIP
import org.kevem.evm.model.Byte
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Features
import org.kevem.evm.model.TransactionMessage
import java.math.BigInteger

class TransactionGasCalculator {

    fun upFrontCost(transaction: TransactionMessage) =
        transaction.value + (transaction.gasLimit * transaction.gasPrice)

    // gas cost required to consider transaction valid
    fun intrinsicGas(transaction: TransactionMessage, features: Features): BigInteger {
        val createCost =
            if (isContractCreation(transaction)) GasCost.TxCreate.costBigInt
            else BigInteger.ZERO

        val singleDataByteGasCost =
            if (features.isEnabled(EIP.EIP2028)) GasCost.TxDataNonZeroEip2028
            else GasCost.TxDataNonZeroHomestead

        val dataCost = transaction.data
            .map { if (it == Byte.Zero) GasCost.TxDataZero.costBigInt else singleDataByteGasCost.costBigInt }
            .fold(BigInteger.ZERO) { acc, n -> acc + n }

        return GasCost.Transaction.costBigInt + createCost + dataCost
    }

    fun contractCreationCost(transaction: TransactionMessage, executionResult: ExecutionContext): BigInteger =
        if (transaction.to == null)
            GasCost.CodeDeposit.costBigInt * executionResult.lastReturnData.size() * transaction.gasPrice
        else
            BigInteger.ZERO

    private fun isContractCreation(transaction: TransactionMessage) = transaction.to == null

}