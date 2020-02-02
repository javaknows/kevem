package org.kevm.web

import org.kevm.web.module.EvmContext
import org.kevm.web.module.RpcModule
import org.kevm.web.module.RpcRequest
import org.kevm.web.module.RpcResponse
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class KevmWebRpcService(private val modules: List<RpcModule>, private val evmContext: EvmContext) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? =
        modules.asSequence()
            .map { it.process(request, evmContext) }
            .filterNotNull()
            .firstOrNull()

}