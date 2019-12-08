package com.gammadex.kevin.web3

import com.gammadex.kevin.compat.generated.NumberFunctions
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.toByteList
import com.gammadex.kevin.rpc.AppConfig
import com.gammadex.kevin.rpc.LocalAccount
import com.gammadex.kevin.rpc.LocalAccounts
import com.gammadex.kevin.rpc.toBigInteger
import com.gammadex.kevin.web3.test.Web3TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.Instant
import org.web3j.protocol.core.methods.request.Transaction as Web3RequestTransaction
import org.web3j.protocol.core.methods.response.Transaction as Web3ResponseTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt as Web3TransactionReceipt

class KevinWeb3ServiceContractDeploymentTest {

    var web3 = Web3TestContext.createTestWeb3()
    var txManager = Web3TestContext.txManager(web3)

    @Test
    fun `can deploy NumberFunctions contract and call the OR function`() {
        val numberFunctions = NumberFunctions.deploy(web3, txManager, Web3TestContext.gasProvider).send()

        val arg = Numeric.hexStringToByteArray("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe")
        val txHash = numberFunctions.callNot(arg).send().transactionHash
        val receipt = web3.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
        val result = receipt.logs[0].data

        assertThat(result).isEqualTo("0x0000000000000000000000000000000000000000000000000000000000000001")
    }
}