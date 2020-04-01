package org.kevem.web

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.endpoint.Server
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider
import org.kevem.evm.locking.locked
import org.kevem.rpc.KevemRpcService
import org.kevem.rpc.jackson.RequestObjectMapper
import org.kevem.rpc.module.*
import java.util.concurrent.locks.ReentrantLock

class Server {
    private val runningLock = ReentrantLock()

    private val stopped = runningLock.newCondition()

    private var running = false; // guarded by runningLock

    private var jaxRsServer: org.apache.cxf.endpoint.Server? = null // guarded by runningLock

    fun start(host: String, port: Int, keepAlive: Boolean = false, evmContext: EvmContext, onStart: () -> Unit = {}) = locked(runningLock) {
        jaxRsServer = createServer(host, port, evmContext)
        jaxRsServer?.start()
        running = true

        onStart()

        if (keepAlive) {
            while (running) {
                stopped.await()
            }
        }
    }

    fun stop() = locked(runningLock) {
        jaxRsServer?.stop()
        jaxRsServer?.destroy()
        running = false
        stopped.signal()
    }

    fun isRunning() = running

    private fun createServer(host: String, port: Int, evmContext: EvmContext): Server {
        val modules = listOf(WebModule, NetModule, EthModule, TestModule)

        return JAXRSServerFactoryBean().apply {
            val jacksonProvider = JacksonJsonProvider(RequestObjectMapper().create(
                modules.fold(emptyMap()) { a, m -> a + m.supported() }
            ))
            providers = listOf<Any>(jacksonProvider, ErrorProvider())
            address = "http://$host:$port/"

            setResourceClasses(KevemWebRpcService::class.java)
            setResourceProvider(
                KevemWebRpcService::class.java, SingletonResourceProvider(
                    KevemWebRpcService(KevemRpcService(modules, evmContext))
                )
            )
        }.create()
    }
}
