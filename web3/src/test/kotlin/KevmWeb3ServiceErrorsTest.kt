package org.kevm.web3

import org.kevm.web3.test.Web3TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KevmWeb3ServiceErrorsTest {

    var web3 = Web3TestContext.createTestWeb3()

    @Test
    fun `can receive an error`() {
        val error = web3.shhNewGroup().send().error

        assertThat(error.message).isEqualTo("unknown RPC request method 'shh_newGroup'")
    }
}