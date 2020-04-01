package org.kevem.web3

import org.kevem.compat.generated.NumberFunctions
import org.kevem.web3.test.Web3TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.web3j.utils.Numeric

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