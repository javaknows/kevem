package org.kevm.compat.runner

import org.kevm.compat.generated.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import java.math.BigInteger

enum class CallType(val callTypeName: String, val num: Int) {
    Call("call", 1),
    CallCode("callcode", 2),
    DelegateCall("delegatecall", 3),
    StaticCall("staticcall", 4);

    fun hex() = "0x$num"
}

data class Result(val callType: String, val code: ResultCode, val value: String)

enum class ResultCode(val code: Int) {
    MemPointer(1),
    Storage(2),
    Address(3),
    Caller(4),
    Origin(5),
    Result(9);

    companion object {
        fun from(c: Int): ResultCode = values().find { it.code == c }!!
    }
}

fun main() {

    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))

    CallType.values().forEach { ct ->
        val (mainContract, childContract) = init(web3j)
        val callTypeNum = ct.num

        mainContract.setCallType(callTypeNum.toBigInteger()).send()
        mainContract.setGasToUse(BigInteger.valueOf(1000000)).send()

        val txHash = mainContract.callCreateContextLogs().send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()

        val r = receipt.logs.map { log ->
            val (type, code, res) = log.topics

            val intCode = BigInteger(code.replace("0x", ""), 16).toInt()
            Result(stripZeros(type), ResultCode.from(intCode), stripZeros(res))
        }

        val resultLine = r.groupBy { cr -> cr.callType }
            .flatMap { g: Map.Entry<String, List<Result>> ->
                val type = CallType.values().find {it.hex() == g.key}?.callTypeName ?: ""
                val typeResults = g.value

                ResultCode.values().sorted().map { v ->
                    typeResults.find { it.code.code == v.code }?.value ?: "NOT FOUND"
                }
            }

        println("account: 0x4e7d932c0f12cfe14295b86824b37bb1062bc29e")
        println("main contract: ${mainContract.contractAddress}")
        println("child contract: ${childContract.contractAddress}")
        println("call type: ${ct.callTypeName}")
        println("${ResultCode.values().sorted().map { it.name }.joinToString("|")}")
        println(resultLine.joinToString(" | "))
        println("------------------------------------------")
    }
}

private fun stripZeros(s: String) = s.replace("0x0*".toRegex(), "0x")

private fun init(web3j: Web3j): Pair<DelegateToLogContext, LogContext> {
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = ClientTransactionManager(web3j, "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(10000000), BigInteger.valueOf(400))

    val logContext = LogContext.deploy(web3j, txManager, gasProvider).send()

    val contract = DelegateToLogContext.deploy(web3j, txManager, gasProvider).send()
    contract.setChildAddress(logContext.contractAddress).send()

    return Pair(contract, logContext)
}