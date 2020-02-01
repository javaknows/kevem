package org.kevm.web

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

open class RpcRequest<T>(
    val jsonrpc: String,
    val method: String,
    val id: Long,
    val params: T
)

open class RpcResponse<T>(
    val jsonrpc: String,
    val id: Long,
    val result: T
)

class ClientVersionResponse(jsonrpc: String, id: Long, result: String) : RpcResponse<String>(jsonrpc, id, result)

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class KevmWebRpcService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun getCustomer(request: RpcRequest<*>): ClientVersionResponse {
        //return Greeting("hello")
        return ClientVersionResponse("2.0", 67, "Mist/v0.9.3/darwin/go1.4.1")
    }

}