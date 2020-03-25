package org.kevm.web

import org.kevm.rpc.KevmRpcService
import org.kevm.rpc.module.RpcRequest
import org.kevm.rpc.module.RpcResponse
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class KevmWebRpcService(private val kevmRpcService: KevmRpcService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? = kevmRpcService.processRequest(request)

}