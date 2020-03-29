package org.kevem.compat.runner

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.TransactionManager
import java.math.BigInteger
import java.util.concurrent.CopyOnWriteArrayList

class CapturingClientTransactionManager(web3j: Web3j, fromAddress: String) :
    ClientTransactionManager(web3j, fromAddress) {

    private val list = CopyOnWriteArrayList<TransactionReceipt>()

    override fun executeTransaction(
        gasPrice: BigInteger?,
        gasLimit: BigInteger?,
        to: String?,
        data: String?,
        value: BigInteger?,
        constructor: Boolean
    ): TransactionReceipt =
        super.executeTransaction(gasPrice, gasLimit, to, data, value, constructor).also {
            list.add(it)
        }

    fun lastReceipt(): TransactionReceipt? = list.lastOrNull()
}