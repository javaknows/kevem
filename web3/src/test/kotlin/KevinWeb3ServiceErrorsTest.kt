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

class KevinWeb3ServiceErrorsTest {

    var web3 = Web3TestContext.createTestWeb3()

    @Test
    fun `can receive an error`() {
        val error = web3.shhNewGroup().send().error

        assertThat(error.message).isEqualTo("not implemented")
    }
}