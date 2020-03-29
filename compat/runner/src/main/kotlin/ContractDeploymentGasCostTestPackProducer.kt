package org.kevem.compat.runner

import org.kevem.compat.generated.NumberFunctions
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Numeric
import java.math.BigInteger

fun main(args: Array<String>) {

    val (web3j, txManager, gasProvider) = init()

    val version = (web3j.web3ClientVersion().send().web3ClientVersion ?: "").replace("/.*".toRegex(), "")

    val contract = NumberFunctions.deploy(web3j, txManager, gasProvider).send()

    val receipt = txManager.lastReceipt()
    if (receipt != null) {
        val accountBalance =
            web3j.ethGetBalance("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e", DefaultBlockParameterName.LATEST)
                .send().balance

        println("$version\tNumberFunctions\t" + receipt.gasUsed + "\t" + accountBalance)
    }
}

private fun init(): Triple<Web3j, CapturingClientTransactionManager, ContractGasProvider> {
    val web3j = Web3j.build(HttpService("http://127.0.0.1:8545"))
    val account = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")
    val txManager = CapturingClientTransactionManager(web3j, "0x4E7D932c0f12Cfe14295B86824B37bB1062bc29E")
    val gasProvider = ConstantGasProvider(BigInteger.valueOf(1000000), BigInteger.valueOf(400))

    return Triple(web3j, txManager, gasProvider)
}
