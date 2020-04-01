package org.kevem.rpc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.kevem.common.CategorisedKevemException
import org.kevem.common.Logger
import org.kevem.rpc.module.EvmContext
import org.kevem.rpc.module.RpcModule
import org.kevem.rpc.module.RpcRequest
import org.kevem.rpc.module.RpcResponse

class KevemRpcService(
    private val modules: List<RpcModule>,
    private val evmContext: EvmContext,
    private val exceptionErrorResponse: ExceptionErrorResponse = ExceptionErrorResponse(),
    private val simpleMapper: ObjectMapper = jacksonObjectMapper()
) {

    private val log: Logger = Logger.createLogger(KevemRpcService::class)

    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? {
        log.info(simpleMapper.writeValueAsString(request))

        val response = try {
            modules.asSequence()
                .map { it.process(request, evmContext) }
                .filterNotNull()
                .firstOrNull()
        } catch (e: Exception) {
            exceptionErrorResponse.errorResponse(e, request.jsonrpc, request.id)
        }

        log.info(simpleMapper.writeValueAsString(response))

        return response
    }
}
