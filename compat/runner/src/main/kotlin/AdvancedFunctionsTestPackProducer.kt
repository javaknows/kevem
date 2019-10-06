package com.gammadex.kevin.compat.runner

import com.gammadex.kevin.compat.generated.AdvancedFunctions
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.utils.Numeric
import java.math.BigInteger

fun main(args: Array<String>) {

    val (web3j, contract) = init()

    val input = listOf(
        "0x0000000000000000000000000000000000000000000000000000000000000000",
        "0x0000000000000000000000000000000000000000000000000000000000000001",
        "0x0000000000000000000000000000000000000000000000000000000000000002",
        "0x0000000000000000000000000000000000000000000000000000000000000009",
        "0xffffffffffffffffffffffffffffff0000000000000000000000000000000000",
        "0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe",
        "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
    )

    val out = input.map {
        val txHash = contract.callKeccak256(Numeric.hexStringToByteArray(it)).send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()

        listOf(it, receipt.logs[0].data).joinToString("\t")
    }.joinToString("\n")

    println(out)
}

private fun init(): Pair<Web3j, AdvancedFunctions> {
    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(1000000), BigInteger.valueOf(400))
    val contract = AdvancedFunctions.deploy(web3j, txManager, gasProvider).send()
    return Pair(web3j, contract)
}
