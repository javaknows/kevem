package org.kevem.ethereumtests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.kevem.evm.*
import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.common.conversions.bytesToString
import org.kevem.common.conversions.toByteList
import org.kevem.evm.gas.*
import org.kevem.evm.model.*
import org.kevem.rpc.*
import org.kevem.web.Server
import org.kevem.rpc.module.EthSendTransactionRequest
import org.kevem.rpc.module.EvmContext
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeneralStateTestCaseRunnerTest {

    private val server = Server()
    private val client = HttpClients.createDefault()
    private val mapper = jacksonObjectMapper()

    //@BeforeAll
    //fun setUp() {

    //}

    @AfterEach
    fun tearDown() {
        server.stop()
        client.close()
    }

    private val executor = createExecutor()

    @DisplayName("ethereum-test GeneralStateTest pack")
    @ParameterizedTest(name = "{0}")
    @MethodSource(value = ["testCases"])
    internal fun `ethereum-test GeneralStateTest pack`(testCase: GeneralStateTestExplodedCase): Unit = with(testCase) {
        println(testCase.name)

        val keyPair = ECKeyPair.create(toByteList(transaction.secretKey).map { it.javaByte() }.toByteArray())

        val gasPrice = toBigInteger(transaction.gasPrice)

        val features = Features(emptyList())

        val evmConfig = EvmConfig(
            coinbase = Address(env.currentCoinbase),
            features = features
        )

        val instant = Instant.ofEpochSecond(toBigInteger(env.currentTimestamp).toLong())

        val difficulty = toBigInteger(env.currentDifficulty)

        val blockGasLimit = toBigInteger(env.currentGasLimit)

        val appConfig = AppConfig(
            coinbase = env.currentCoinbase,
            difficulty = difficulty,
            gasPrice = gasPrice,
            blockGasLimit = blockGasLimit,
            genesisBlockTimestamp = instant
        )

        val tp = TransactionProcessor(
            executor = createExecutor(),
            config = evmConfig
        )

        val accounts = parseAccounts(pre)

        val clock = Clock.fixed(instant, ZoneId.of("UTC"))

        val genesisBlock = MinedBlock(
            block = Block(
                number = BigInteger.ZERO,
                difficulty = difficulty,
                gasLimit = blockGasLimit,
                timestamp = Instant.MIN
            ),
            gasUsed = BigInteger.ZERO,
            hash = toByteList(env.previousHash),
            transactions = emptyList()
        )

        val statefulTransactionProcessor = StatefulTransactionProcessor(
            tp, clock, WorldState(listOf(genesisBlock), accounts)
        )

        val localAccount = LocalAccount(Keys.getAddress(keyPair), transaction.secretKey)

        val standardRpc = StandardRPC(
            StandardEvmOperations(
                statefulTransactionProcessor,
                evmConfig
            ),
            appConfig,
            LocalAccounts(listOf(localAccount))
        )

        val testRpc = TestRPC(
            statefulTransactionProcessor
        )

        val evmContext = EvmContext(standardRpc, testRpc)

        server.start("localhost", 9002, false, evmContext)

        val txRequest = EthSendTransactionRequest(
            "2.0", "eth_sendTransaction", 1L, listOf(
                SendTransactionParamDTO(
                    from = localAccount.address.toString(),
                    to = transaction.to,
                    value = transaction.value,
                    gasPrice = transaction.gasPrice,
                    gas = transaction.gasLimit,
                    data = transaction.data,
                    nonce = transaction.nonce
                )
            )
        )

        val request = mapper.writeValueAsString(txRequest)

        val httppost = HttpPost("http://localhost:9002/").apply {
            entity = StringEntity(request, ContentType.APPLICATION_JSON)
        }

        val response = client.execute(httppost)
        val responseBody = EntityUtils.toString(response.entity)

        println(responseBody)

        val wsResult = statefulTransactionProcessor.getWorldState()

        results.forEach {
            val (a, expectedResult) = it
            val address = Address(a)

            if (expectedResult.shouldnotexist == "1")
                assertThat(wsResult.accounts.accountExists(address)).isFalse()
            //else
            //assertThat(wsResult.accounts.accountExists(address)).isTrue()

            expectedResult.balance?.also { expectedBalance ->
                val balance = wsResult.accounts.balanceOf(address)
                assertThat(balance).isEqualTo(toBigInteger0xTo0(expectedBalance))
            }
            expectedResult.nonce?.also { expectedNonce ->
                val nonce = wsResult.accounts.nonceOf(address)
                assertThat(nonce).isEqualTo(toBigInteger0xTo0(expectedNonce))
            }
            expectedResult.code?.also { expectedCode ->
                val code = wsResult.accounts.codeAt(address)
                val codeString = bytesToString(code.toList())
                assertThat(codeString).isEqualTo(expectedCode)
            }
            expectedResult.storage?.also { expectedStorage ->
                expectedStorage.forEach {
                    val (index, expectedValue) = it

                    val value = wsResult.accounts.storageAt(address, toBigInteger(index))

                    //assertThat(value).isEqualTo(Word.coerceFrom(expectedValue))
                }
            }
        }
    }


    companion object {
        private val loader = ExplodedGeneralTestCaseLoader()

        @JvmStatic
        fun testCases(): List<GeneralStateTestExplodedCase> = loader.loadTestCases()
    }

    private fun assertOutDataMatches(out: String?, executed: ExecutionContext) =
        assertThat(executed.lastReturnData).isEqualTo(toByteList(out))

    private fun assertPostAccountsMatch(accounts: Accounts, executed: ExecutionContext) =
        accounts.list().forEach { a ->
            val account: Account = executed.accounts.list().find { it.address == a.address }
                ?: fail("no account with address ${a.address}")

            assertThat(a).isEqualTo(account)
        }

    private fun createExecutor(): Executor =
        Executor(
            GasCostCalculator(
                BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
                MemoryUsageGasCostCalculator(
                    MemoryUsageGasCalc()
                )
            )
        )

    private fun parseAccounts(post: Map<String, GeneralStateTestCasePre>?): Accounts {
        val accountList = post?.map { entry ->
            val (a, d) = entry

            val contract =
                if (d.code != "0x") Contract(
                    code = BigIntegerIndexedList.fromByteString(d.code),
                    storage = Storage(
                        d.storage.map { e ->
                            val (k, v) = e
                            Pair(toBigInteger(k), Word.coerceFrom(v))
                        }.toMap()
                    )
                ) else null

            Account(
                Address(a),
                toBigInteger(d.balance),
                contract,
                nonce = toBigInteger(d.nonce)
            )
        } ?: emptyList()

        return Accounts(accountList)
    }

    /*
     use from general location
     */

    private fun toBigInteger0xTo0(num: String): BigInteger = toBigInteger(num.replace("^0x$".toRegex(), "0"))

    private fun toBigInteger(number: String) =
        if (number.startsWith("0x")) BigInteger(cleanHexNumber(number), 16)
        else BigInteger(number)

    private fun cleanHexNumber(number: String) = number.replaceFirst("0x0+", "0x0").replaceFirst("0x", "")

    fun toBigIntegerOrNull(number: String?) =
        if (number == null) null
        else if (number.startsWith("0x")) BigInteger(cleanHexNumber(number), 16)
        else BigInteger(number)
}