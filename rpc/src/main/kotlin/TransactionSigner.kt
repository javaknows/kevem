package com.gammadex.kevin.rpc

import com.gammadex.kevin.evm.bytesToString
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import com.gammadex.kevin.evm.model.Byte

// TODO - remove this use of web3 when possible
class TransactionSigner {

    fun sign(transaction: SendTransactionParamDTO, account: LocalAccount): List<Byte> {
        val rawTransaction = RawTransaction.createTransaction(
            toBigInteger(transaction.nonce ?: "0"),
            toBigInteger(transaction.gasPrice),
            toBigInteger(transaction.gas),
            transaction.to,
            toBigInteger(transaction.value ?: "0"),
            transaction.data
        )

        val signedMessage = TransactionEncoder.signMessage(
            rawTransaction,
            Credentials.create(bytesToString(account.privateKey))
        )

        return signedMessage.map { Byte(it.toInt() and 0xFF) }
    }
}