package org.kevem.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.evm.model.*
import org.kevem.common.conversions.toByteList
import org.kevem.rpc.*
import java.math.BigInteger
import java.time.Instant
import org.kevem.common.conversions.*

data class RequestAndResponse(val name: String, val request: String, val response: String)

class RpcServerIntegrationTest {

    private val server = Server()
    private val client = HttpClients.createDefault()

    @BeforeEach
    internal fun setUp() {
        server.start(
            "localhost", 9002, false, createTestEvmContext()
        )
    }

    @AfterEach
    internal fun tearDown() {
        server.stop()
        client.close()
    }

    @ParameterizedTest
    @MethodSource(value = ["requestsAndResponses"])
    internal fun `check example RPC requests and responses end-to-end`(example: RequestAndResponse) {
        val response = executeRequest(example)

        val actualBody = mapper.readValue(response, Map::class.java)
        val expectedBody = mapper.readValue(example.response, Map::class.java)

        assertSameEntries(actualBody, expectedBody)
    }

    private fun assertSameEntries(actualBody: Map<*, *>, expectedBody: Map<*, *>) {
        assertThat(actualBody.keys).isEqualTo(expectedBody.keys)
        expectedBody.forEach { entry ->
            val (key, value) = entry
            assertThat(actualBody[key]).isEqualTo(value)
        }
    }

    private fun executeRequest(example: RequestAndResponse): String? {
        val httppost = HttpPost("http://localhost:9002/").apply {
            entity = StringEntity(example.request, ContentType.APPLICATION_JSON)
        }

        val response = client.execute(httppost)
        val responseBody = EntityUtils.toString(response.entity)
        return responseBody
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        @JvmStatic
        fun requestsAndResponses(): List<RequestAndResponse> {
            val testNames = System.getProperty("method")?.let {
                listOf(it)
            } ?: load("RpcServerIntegrationTest/testList.txt")
                .split("\n")
                .map { it.trim() }
                .filterNot { it.startsWith("#") }

            return testNames.map { loadData(it) }
        }

        private fun loadData(name: String): RequestAndResponse {
            val request = load("RpcServerIntegrationTest/$name/request.json")
            val response = load("RpcServerIntegrationTest/$name/response.json")

            return RequestAndResponse(name, request, response)
        }

        private fun load(requestPath: String) =
            RpcServerIntegrationTest::class.java.classLoader.getResource(requestPath)
                ?.readText()
                ?: throw IllegalStateException("not found on classpath - $requestPath")
    }

    private fun createTestEvmContext() = EvmContextBuilder.build(
        config = AppConfig(
            coinbase = "0xc94770007dda54cF92009BFF0dE90c06F603a09f",
            hashRate = toBigInteger("0x123")
        ),
        localAccounts = LocalAccounts(
            listOf(
                LocalAccount(Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), emptyList(), false),
                LocalAccount(
                    Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"),
                    toByteList("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"),
                    false
                )
            )
        ),
        accounts = Accounts(
            mapOf(
                Pair(
                    Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"),
                    Account(
                        Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"),
                        toBigInteger("0x234c8a3397aab58")
                    )
                ),
                Pair(
                    Address("0x295a70b2de5e3953354a6a8344e616ed314d7251"),
                    Account(
                        address = Address("0x295a70b2de5e3953354a6a8344e616ed314d7251"),
                        contract = Contract(
                            storage = Storage(
                                mapOf(
                                    Pair(BigInteger.ZERO, Word.coerceFrom("0x4d2"))
                                )
                            ),
                            code = BigIntegerIndexedList.fromByteString("0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056")
                        )
                    )
                )
            )
        ),
        evmConfig = EvmConfig(
            chainId = BigInteger.TWO,
            coinbase = Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f")
        ),
        genesisBlock = MinedBlock(
            block = Block(
                number = BigInteger.ONE,
                difficulty =  BigInteger.ZERO,
                gasLimit = BigInteger("1000000000000000000000000000000"),
                timestamp = Instant.parse("2015-06-30T03:26:28.00Z")
            ),
            gasUsed = BigInteger.ZERO,
            hash = toByteList("0x88e96d4537bea4d9c05d12549907b32561d3bf31f45aae734cdc119f13406cb6"),
            transactions = listOf(
                MinedTransaction(
                    TransactionMessage(
                        from = Address("0xc94770007dda54cf92009bff0de90c06f603a09f"),
                        to = Address("0x0"),
                        value = BigInteger.ZERO,
                        gasPrice = BigInteger.ZERO,
                        gasLimit = BigInteger.ZERO,
                        nonce = BigInteger.ZERO,
                        hash = toByteList("0xb903239f8543d04b5dc1ba6579132b143087c68db1b2168786408fcbce568238")
                    ),
                    TransactionResult(
                        status = ResultStatus.COMPLETE,
                        gasUsed = BigInteger.ONE,
                        logs = listOf(
                            Log(
                                emptyList(),
                                listOf(Word.coerceFrom("0x000000000000000000000000a94f5374fce5edbc8e2a8697c15331677e6ebf0b"))
                            )
                        )
                    )
                )
            )
        )
    )
}