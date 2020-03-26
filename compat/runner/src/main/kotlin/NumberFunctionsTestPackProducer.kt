package org.kevem.compat.runner

import org.kevem.compat.generated.NumberFunctions
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.utils.Numeric
import java.math.BigInteger

fun main(args: Array<String>) {

    val (web3j, contract) = init()

    val single = listOf(
        "0x0000000000000000000000000000000000000000000000000000000000000000",
        "0x0000000000000000000000000000000000000000000000000000000000000001",
        "0x0000000000000000000000000000000000000000000000000000000000000002",
        "0x0000000000000000000000000000000000000000000000000000000000000009",
        "0xffffffffffffffffffffffffffffff0000000000000000000000000000000000",
        "0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe",
        "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
    )
    val pairs = single.flatMap { i1 ->
        single.map { i2 ->
            listOf(i1, i2)
        }
    }
    val triples = pairs.flatMap {
        val (i1, i2) = it
        single.map { i3 -> listOf(i1, i2, i3) }
    }
    val oneArgMethods = listOf("not", "iszero")
    val twoArgMethods = listOf(
        "add",
        "and",
        "byte",
        "div",
        "eq",
        "exp",
        "gt",
        "lt",
        "mod",
        "mul",
        "or",
        "sdiv",
        "sgt",
        "signextend",
        "slt",
        "smod",
        "sub",
        "xor",
        "sar"
    )
    val threeArgMethods = listOf("mulmod", "addmod")

    val out = (oneArgMethods + twoArgMethods + threeArgMethods).flatMap { function ->
        val argTypes = when (function) {
            in oneArgMethods -> arrayOf(ByteArray::class.java)
            in twoArgMethods -> arrayOf(ByteArray::class.java, ByteArray::class.java)
            else -> arrayOf(ByteArray::class.java, ByteArray::class.java, ByteArray::class.java)
        }

        val argsList: List<List<String>> = when (function) {
            in oneArgMethods -> single.map { listOf(it) }
            in twoArgMethods -> pairs
            else -> triples
        }

        val method = contract.javaClass.getMethod(contractFunctionName(function), *argTypes)

        argsList.map { args ->
            val argsX = args.map { Numeric.hexStringToByteArray(it) }.toTypedArray()

            val txHash = (method.invoke(contract, *argsX) as RemoteCall<TransactionReceipt>).send().transactionHash
            val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
            val result = receipt.logs[0].data

            (listOf(function) + listOf(result) + args + listOf("", "", "", "")).take(6).joinToString("\t")
        }
    }.joinToString("\n")

    println(out)
}

private fun init(): Pair<Web3j, NumberFunctions> {
    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(1000000), BigInteger.valueOf(400))
    val contract = NumberFunctions.deploy(web3j, txManager, gasProvider).send()
    return Pair(web3j, contract)
}

private fun contractFunctionName(name: String) = "call" + name[0].toUpperCase() + name.drop(1)
