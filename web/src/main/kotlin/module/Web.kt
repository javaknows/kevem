package org.kevm.web.module

import org.kevm.web.module.Method.Companion.create

class ClientVersionRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class ClientVersionResponse(request: ClientVersionRequest, result: String) : RpcResponse<String>(request, result)

class Web3Sha3Request(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class Web3Sha3Response(request: Web3Sha3Request, result: String) : RpcResponse<String>(request, result)

private val Web3ClientVersion =
    create("web3_clientVersion", ClientVersionRequest::class, ClientVersionResponse::class) { request, context ->
        val web3clientVersion = context.standardRpc.web3clientVersion()
        ClientVersionResponse(request, web3clientVersion)
    }

private val Web3Sha3 = create("web3_sha3", Web3Sha3Request::class, Web3Sha3Response::class) { request, context ->
    val sha3 = context.standardRpc.web3sha3(request.params.first())
    Web3Sha3Response(request, sha3)
}

@Suppress("UNCHECKED_CAST")
private val webMethods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    Web3ClientVersion,
    Web3Sha3
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val WebModule = Module(webMethods)
