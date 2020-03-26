package org.kevem.web3

import org.kevem.web3.test.Web3TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KevemWeb3ServiceErrorsTest {

    var web3 = Web3TestContext.createTestWeb3()

    @Test
    fun `can receive an error`() {
        val error = web3.shhNewGroup().send().error

        assertThat(error.message).isEqualTo("unknown RPC request method 'shh_newGroup'")
    }
}