package org.kevem.rpc

import org.kevem.rpc.module.EvmContext
import org.kevem.rpc.module.RpcModule
import org.kevem.rpc.module.RpcRequest
import org.kevem.rpc.module.RpcResponse

class KevemRpcService(private val modules: List<RpcModule>, private val evmContext: EvmContext) {

    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? =
        modules.asSequence()
            .map { it.process(request, evmContext) }
            .filterNotNull()
            .firstOrNull()

}
