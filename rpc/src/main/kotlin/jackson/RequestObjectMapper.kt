package org.kevem.rpc.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.kevem.rpc.CategorisedKevemRpcException
import org.kevem.rpc.module.RpcRequest
import kotlin.reflect.KClass

fun <T : Any> ObjectMapper.readValue(content: String, valueType: KClass<T>): T = readValue(content, valueType.java)

class RequestDeserializationException(message: String, code: Long = -1, id: Long? = null, jsonrpc: String? = null) :
    CategorisedKevemRpcException(message, code, id = id, jsonrpc = jsonrpc)

class RequestObjectMapper {
    fun create(methodTypes: Map<String, KClass<out RpcRequest<*>>>): ObjectMapper =
        jacksonObjectMapper().apply {
            registerModule(SimpleModule().apply {
                addDeserializer(RpcRequest::class.java, RequestDeserializer(methodTypes))
            })
        }
}

private class RequestDeserializer(private val methodTypes: Map<String, KClass<out RpcRequest<*>>>) :
    StdDeserializer<RpcRequest<*>>(RpcRequest::class.java) {

    private val requestFields = setOf("jsonrpc", "method", "params", "id")

    private val simpleMapper = jacksonObjectMapper()

    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): RpcRequest<*> {
        val node: JsonNode = parser.codec.readTree(parser)
        validateRequestFields(node)

        return try {
            simpleMapper.readValue(node.toString(), getRequestClass(node))
        } catch (e: JsonProcessingException) {
            val (id, jsonrpc) = extractIdJsonrpc(node)
            throw RequestDeserializationException(e.originalMessage, -1, id, jsonrpc)
        }
    }

    private fun extractIdJsonrpc(node: JsonNode): Pair<Long?, String?> {
        val jsonrpc = node.get("jsonrpc")?.asText()
        val id = node.get("id")?.asLong()

        return Pair(id, jsonrpc)
    }

    private fun getRequestClass(node: JsonNode): KClass<out RpcRequest<*>> {
        val method = node.get("method").asText()

        return methodTypes.getOrElse(method) {
            val (id, jsonrpc) = extractIdJsonrpc(node)
            throw RequestDeserializationException("unknown RPC request method '$method'", -32601, id, jsonrpc)
        }
    }

    private fun validateRequestFields(node: JsonNode) = requestFields.forEach {
        val (id, jsonrpc) = extractIdJsonrpc(node)
        if (node.get(it) == null) throw RequestDeserializationException("$it parameter is required", -1, id, jsonrpc)
    }
}

