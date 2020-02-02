package org.kevm.web

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.endpoint.Server
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider
import org.kevm.evm.locking.locked
import org.kevm.web.jackson.RequestObjectMapper
import org.kevm.web.module.WebModule
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.exitProcess

class Server {
    private val runningLock = ReentrantLock()

    private val stopped = runningLock.newCondition()

    private var running = false; // guarded by runningLock

    private var jaxRsServer: org.apache.cxf.endpoint.Server? = null

    fun start(port: Int, keepAlive: Boolean = false) {
        locked(runningLock) {
            jaxRsServer = createServer(port)
            jaxRsServer?.start()
            running = true

            if (keepAlive) {
                while (running) {
                    stopped.await()
                }
            }
        }
    }

    fun stop() {
        locked(runningLock) {
            jaxRsServer?.stop()
            jaxRsServer?.destroy()
            running = false
            stopped.signal()
        }
    }

    private fun createServer(port: Int): Server {
        return JAXRSServerFactoryBean().apply {
            providers = listOf<Any>(JacksonJsonProvider(RequestObjectMapper().create(WebModule.supported())))
            address = "http://localhost:$port/"

            setResourceClasses(KevmWebRpcService::class.java)
            setResourceProvider(
                KevmWebRpcService::class.java, SingletonResourceProvider(
                    KevmWebRpcService(listOf(WebModule), EvmContextCreator.create())
                )
            )
        }.create()
    }
}
