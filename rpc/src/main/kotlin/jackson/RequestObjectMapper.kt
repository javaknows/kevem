package org.kevem.rpc.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.kevem.rpc.module.RpcRequest
import kotlin.reflect.KClass

fun <T : Any> ObjectMapper.readValue(content: String, valueType: KClass<T>): T = readValue(content, valueType.java)

class RequestDeserializationException(message: String) : IllegalArgumentException(message)

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
            throw RequestDeserializationException(e.originalMessage)
        }
    }

    private fun getRequestClass(node: JsonNode): KClass<out RpcRequest<*>> {
        val method = node.get("method").asText()

        return methodTypes.getOrElse(method) {
            throw RequestDeserializationException("unknown RPC request method '$method'")
        }
    }

    private fun validateRequestFields(node: JsonNode) = requestFields.forEach {
        if (node.get(it) == null) throw RequestDeserializationException("$it parameter is required")
    }
}

