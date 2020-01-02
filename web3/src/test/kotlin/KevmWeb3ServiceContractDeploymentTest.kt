package org.kevm.web3

import org.kevm.compat.generated.NumberFunctions
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.rpc.AppConfig
import org.kevm.rpc.LocalAccount
import org.kevm.rpc.LocalAccounts
import org.kevm.rpc.toBigInteger
import org.kevm.web3.test.Web3TestContext
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

class KevmWeb3ServiceContractDeploymentTest {

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