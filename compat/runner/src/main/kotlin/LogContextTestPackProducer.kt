package com.gammadex.kevin.compat.runner

import com.gammadex.kevin.compat.generated.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import java.math.BigInteger

fun main() {

    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))

    val calls = listOf(
        Pair("call", BigInteger("1")),
        Pair("callcode", BigInteger("2")),
        Pair("delegatecall", BigInteger("3")),
        Pair("staticcall", BigInteger("4"))
    )

    val contract = init(web3j)

    calls.forEach {
        val (name, callTypeNum) = it

        println("\t====================\n\t$name")

        contract.setCallType(callTypeNum).send()
        contract.setGasToUse(BigInteger.valueOf(1000000)).send()

        val txHash = contract.callCreateContextLogs().send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
        printLogs(receipt.logs)
    }

    fun printLogs(logs: List<Log>) {
        println("\t${logs.size} logs\n\t----------")
        logs.forEach { l ->
            println("\t\t" + l.data)
            l.topics.forEach { t ->
                println("\t\t" + t)
            }
            println("\t----------")
        }
    }
}

private fun init(web3j: Web3j): DelegateToLogContext {
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(10000000), BigInteger.valueOf(400))

    val logContext = LogContext.deploy(web3j, txManager, gasProvider).send()
    println("\thalt funcs on ${logContext.contractAddress}")

    val contract = DelegateToLogContext.deploy(web3j, txManager, gasProvider).send()
    contract.setChildAddress(logContext.contractAddress).send()
    println("\tparent on ${contract.contractAddress}")

    return contract
}
