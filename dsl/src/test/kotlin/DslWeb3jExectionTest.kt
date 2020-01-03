package org.kevm.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevm.compat.generated.NumberFunctions
import org.kevm.web3.test.Web3TestContext
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

class DslWeb3jExectionTest {

    val web3j = kevm {
        account {
            balance = eth(1)
            privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
        }
    }.toWeb3j()

    val txManager = Web3TestContext.txManager(web3j)

    val gasProvider = Web3TestContext.gasProvider

    @Test
    fun `can get balance of account`() {
        val result =
            web3j.ethGetBalance("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e", DefaultBlockParameterName.LATEST).send()

        assertThat(result.balance).isEqualTo(BigInteger("1000000000000000000"))
    }

    @Test
    fun `can deploy NumberFunctions contract and execute not function`() {
        val numberFunctions = NumberFunctions.deploy(web3j, txManager, gasProvider).send()

        val arg = Numeric.hexStringToByteArray("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe")
        val txHash = numberFunctions.callNot(arg).send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
        val result = receipt.logs[0].data

        assertThat(result).isEqualTo("0x0000000000000000000000000000000000000000000000000000000000000001")
    }

}