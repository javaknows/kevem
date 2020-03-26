package org.kevem.web

import org.kevem.rpc.KevemRpcService
import org.kevem.rpc.module.RpcRequest
import org.kevem.rpc.module.RpcResponse
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class KevemWebRpcService(private val kevemRpcService: KevemRpcService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun processRequest(request: RpcRequest<*>): RpcResponse<*>? = kevemRpcService.processRequest(request)

}