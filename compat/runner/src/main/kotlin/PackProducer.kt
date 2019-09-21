package com.gammadex.kevin.compat.runner

import com.gammadex.kevin.compat.generated.Kevin
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Numeric
import java.math.BigInteger

data class ConstantGasProvider(val limit: BigInteger, val price: BigInteger) : ContractGasProvider {
    override fun getGasLimit(contractFunc: String?): BigInteger = limit
    override fun getGasLimit(): BigInteger = limit
    override fun getGasPrice(contractFunc: String?): BigInteger = price
    override fun getGasPrice(): BigInteger = price
}

tailrec fun toTypes(list: List<String>, acc: Array<Class<*>> = emptyArray()): Array<Class<*>> =
    if (list.size <= 1) acc
    else {
        val t = when (list[0]) {
            "byteArray" -> ByteArray::class.java
            else -> TODO("not implemented")
        }

        toTypes(list.drop(2), acc + t)
    }

tailrec fun toArguments(list: List<String>, acc: Array<Any> = emptyArray()): Array<Any> =
    if (list.size <= 1) acc
    else {
        val v = when (list[0]) {
            "byteArray" -> Numeric.hexStringToByteArray(list[1])
            else -> TODO("not implemented")
        }

        toArguments(list.drop(2), acc + v)
    }

fun main(args: Array<String>) {

    val (web3j, contract) = init()

    val fileContent =
        ConstantGasProvider::class.java.getResource("/com/gammadex/kevin/compat/runner/pack.tsv").readText()

    val out = fileContent.split("\n").filterNot { it.isEmpty() }.map {
        val parts = it.split("\t")
        val function = cleanFunctionName(parts[0])
        val argsWithTypes = parts.drop(1)

        val types = toTypes(argsWithTypes)
        val method = contract.javaClass.getMethod(function, *types)

        val functionArgs = toArguments(argsWithTypes)
        val txHash = (method.invoke(contract, *functionArgs) as RemoteCall<TransactionReceipt>).send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()

        val result = receipt.logs[0].data

        (parts + result).joinToString("\t")
    }.joinToString("\n")

    println(out)
}

fun cleanFunctionName(name: String) = when {
    name.startsWith("call") -> {
        val n = name.replace("call", "")
        n[0].toLowerCase() + n.drop(1)
    }
    else -> name
}

private fun init(): Pair<Web3j, Kevin> {
    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(1000000), BigInteger.valueOf(400))
    val contract = Kevin.deploy(web3j, txManager, gasProvider).send()
    return Pair(web3j, contract)
}

