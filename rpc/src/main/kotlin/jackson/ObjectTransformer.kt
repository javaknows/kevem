package org.kevem.rpc.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlin.reflect.KClass

object ObjectTransformer {

    private val mapper = ObjectMapper().registerKotlinModule().also {
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun <T : Any> transform(source: Map<Any, Any>, type: KClass<T>): T {
        val json = mapper.writeValueAsString(source)
        return mapper.readValue(json, type.java)
    }

}