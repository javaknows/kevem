package org.kevem.rpc.jackson

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import org.kevem.rpc.module.RpcRequest

class TestClientVersionRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<String>>(jsonrpc, method, id, emptyList())

class RequestObjectMapperTest {

    private val underTest = RequestObjectMapper().create(
        mapOf(
            Pair("web3_clientVersion", TestClientVersionRequest::class)
        )
    )

    @Test
    internal fun `check can map JSON to TestClientVersionRequest`() {
        val json = """{"jsonrpc":"2.0","method":"web3_clientVersion","params":[],"id":67}"""

        val result = underTest.readValue(json, RpcRequest::class)

        assertThat(result is TestClientVersionRequest).isTrue()
        assertThat(result.id).isEqualTo(67L)
        assertThat(result.method).isEqualTo("web3_clientVersion")
        assertThat(result.jsonrpc).isEqualTo("2.0")
    }

    @Test
    internal fun `check unknown method type throws illegal argument exception`() {
        val json = """{"jsonrpc":"2.0","method":"web3_some_unknown_method_name","params":[],"id":67}"""

        val exception: RequestDeserializationException = assertThrows {
            underTest.readValue(json, RpcRequest::class)
        }

        assertThat(exception.localizedMessage).contains("unknown RPC request method")
    }

    @ParameterizedTest
    @MethodSource(value = ["missingFieldExamples"])
    internal fun `check missing fields throw illegal argument exception`(example: Pair<String, String>) {
        val (field, json) = example

        val exception: RequestDeserializationException = assertThrows {
            underTest.readValue(json, RpcRequest::class)
        }

        assertThat(exception.localizedMessage).contains("$field parameter is required")
    }

    companion object {
        @JvmStatic
        fun missingFieldExamples() = listOf(
            Pair("jsonrpc", """{"method":"web3_clientVersion","params":[],"id":67}"""),
            Pair("method",  """{"jsonrpc":"2.0","params":[],"id":67}"""),
            Pair("params",  """{"jsonrpc":"2.0","method":"web3_clientVersion","id":67}"""),
            Pair("id",      """{"jsonrpc":"2.0","method":"web3_clientVersion","params":[]}""")
        )
    }
}
