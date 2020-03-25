package org.kevm.rpc

import org.kevm.rpc.module.EvmContext
import org.kevm.rpc.module.RpcModule
import org.kevm.rpc.module.RpcRequest
import org.kevm.rpc.module.RpcResponse

class KevmRpcService(private val modules: List<RpcModule>, private val evmContext: EvmContext) {

    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? =
        modules.asSequence()
            .map { it.process(request, evmContext) }
            .filterNotNull()
            .firstOrNull()

}
