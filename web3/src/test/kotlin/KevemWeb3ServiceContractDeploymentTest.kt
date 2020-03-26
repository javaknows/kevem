package org.kevem.web3

import org.kevem.compat.generated.NumberFunctions
import org.kevem.evm.model.*
import org.kevem.evm.model.Byte
import org.kevem.evm.toByteList
import org.kevem.rpc.AppConfig
import org.kevem.rpc.LocalAccount
import org.kevem.rpc.LocalAccounts
import org.kevem.rpc.toBigInteger
import org.kevem.web3.test.Web3TestContext
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

class KevemWeb3ServiceContractDeploymentTest {

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