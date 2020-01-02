package org.kevm.compat.runner

import org.kevm.compat.generated.AdvancedFunctions
import org.kevm.compat.generated.Delegating
import org.kevm.compat.generated.HaltFunctions
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.utils.Numeric
import java.math.BigInteger

fun main() {

    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))

    val calls = listOf(
        Pair("call", BigInteger("1")),
        Pair("callcode", BigInteger("2")),
        Pair("delegatecall", BigInteger("3")),
        Pair("staticcall", BigInteger("4"))
    )

    val funcs = listOf (
        Pair("doStop",  { c: Delegating -> c.doStop() }),
        Pair("doRevert", { c: Delegating -> c.doRevert() }),
        Pair("doReturn", { c: Delegating -> c.doReturn() }),
        Pair("doSelfDestruct", { c: Delegating -> c.doSelfDestruct() }),
        Pair("doInvalid", { c: Delegating -> c.doInvalid() })
    )

    funcs.forEach { f ->
        val (funcName, func) = f

        println(funcName)

        calls.forEach {
            val contract = init(web3j)

            val (name, callTypeNum) = it

            println("\t====================\n\t$name")

            contract.setCallType(callTypeNum).send()
            contract.setGasToUse(BigInteger.valueOf(1000000)).send()

            //val txHash = contract.doStop().send().transactionHash
            //val txHash = contract.doReturn().send().transactionHash
            //try {
            val txHash = func(contract).send().transactionHash
            val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
            printLogs(receipt.logs)
            //} catch (e: Exception) {
            //    println(e)
            //}
        }
    }
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

private fun init(web3j: Web3j): Delegating {

    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(10000000), BigInteger.valueOf(400))

    val haltFuncs = HaltFunctions.deploy(web3j, txManager, gasProvider).send()
    println("\thalt funcs on ${haltFuncs.contractAddress}")

    val contract = Delegating.deploy(web3j, txManager, gasProvider).send()
    contract.setChildAddress(haltFuncs.contractAddress).send()
    println("\tparent on ${contract.contractAddress}")


    return contract
}
