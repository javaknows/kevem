package org.kevem.rpc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.kevem.common.Logger
import org.kevem.evm.gas.TransactionValidator
import org.kevem.rpc.module.EvmContext
import org.kevem.rpc.module.RpcModule
import org.kevem.rpc.module.RpcRequest
import org.kevem.rpc.module.RpcResponse

class KevemRpcService(private val modules: List<RpcModule>, private val evmContext: EvmContext) {

    private val simpleMapper = jacksonObjectMapper()

    private val log: Logger = Logger.createLogger(KevemRpcService::class)

    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? {
        log.info(simpleMapper.writeValueAsString(request))

        val response = modules.asSequence()
            .map { it.process(request, evmContext) }
            .filterNotNull()
            .firstOrNull()

        log.info(simpleMapper.writeValueAsString(response))

        return response
    }

}
