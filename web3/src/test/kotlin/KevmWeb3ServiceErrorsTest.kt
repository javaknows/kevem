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

class KevmWeb3ServiceErrorsTest {

    var web3 = Web3TestContext.createTestWeb3()

    @Test
    fun `can receive an error`() {
        val error = web3.shhNewGroup().send().error

        assertThat(error.message).isEqualTo("unknown RPC request method 'shh_newGroup'")
    }
}