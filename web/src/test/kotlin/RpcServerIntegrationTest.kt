package org.kevm.web

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

data class RequestAndResponse(val name: String, val request: String, val response: String)

class RpcServerIntegrationTest {

    private val server = Server()
    private val client = HttpClients.createDefault()

    @BeforeEach
    internal fun setUp() {
        server.start(9002)
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
}