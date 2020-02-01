package org.kevm.web

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider
import org.kevm.web.jackson.RequestObjectMapper
import kotlin.reflect.KClass
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Server().start(mapOf())
    Thread.sleep(10 * 6000 * 1000.toLong())
    exitProcess(0)
}

class Server {
    fun start(map: Map<String, KClass<out RpcRequest<*>>>) {
        JAXRSServerFactoryBean().apply {
            providers = listOf<Any>(JacksonJsonProvider(RequestObjectMapper().create(map)))
            address = "http://localhost:9001/"

            setResourceClasses(KevmWebRpcService::class.java)

            setResourceProvider(
                KevmWebRpcService::class.java,
                SingletonResourceProvider(KevmWebRpcService())
            )
        }.create()
    }
}
