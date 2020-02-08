package org.kevm.evm.gas

import org.kevm.evm.model.Byte
import org.kevm.evm.model.ExecutionContext
import org.kevm.evm.model.TransactionMessage
import java.math.BigInteger

class TransactionGasCalculator {

    fun upFrontCost(transaction: TransactionMessage) =
        transaction.value + (transaction.gasLimit * transaction.gasPrice)

    // gas cost required to consider transaction valid
    fun intrinsicGas(transaction: TransactionMessage): BigInteger {
        val createCost =
            if (isContractCreation(transaction)) GasCost.TxCreate.costBigInt
            else BigInteger.ZERO

        val dataCost = transaction.data
            .map { if (it == Byte.Zero) GasCost.TxDataZero.costBigInt else GasCost.TxDataNonZero.costBigInt }
            .fold(BigInteger.ZERO) { acc, n -> acc + n }

        return GasCost.Transaction.costBigInt + createCost + dataCost
    }

    fun contractCreationCost(transaction: TransactionMessage, executionResult: ExecutionContext): BigInteger =
        if (transaction.to == null)
            GasCost.CodeDeposit.costBigInt * executionResult.lastReturnData.size.toBigInteger() * transaction.gasPrice
        else
            BigInteger.ZERO

    private fun isContractCreation(transaction: TransactionMessage) = transaction.to == null

}