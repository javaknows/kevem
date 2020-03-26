package org.kevem.rpc.module

class NetVersionRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class NetVersionResponse(request: NetVersionRequest, result: String) : RpcResponse<String>(request, result)

private val NetVersion =
    Method.create("net_version", NetVersionRequest::class, NetVersionResponse::class) { request, context ->
        val web3NetVersion = context.standardRpc.netVersion()
        NetVersionResponse(request, web3NetVersion)
    }

class NetListeningRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class NetListeningResponse(request: NetListeningRequest, result: Boolean) : RpcResponse<Boolean>(request, result)

private val NetListening =
    Method.create("net_listening", NetListeningRequest::class, NetListeningResponse::class) { request, context ->
        val web3NetListening = context.standardRpc.netListening()
        NetListeningResponse(request, web3NetListening)
    }

class NetPeerCountRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class NetPeerCountResponse(request: NetPeerCountRequest, result: String) : RpcResponse<String>(request, result)

private val NetPeerCount =
    Method.create("net_peerCount", NetPeerCountRequest::class, NetPeerCountResponse::class) { request, context ->
        val web3NetPeerCount = context.standardRpc.netPeerCount()
        NetPeerCountResponse(request, web3NetPeerCount)
    }

@Suppress("UNCHECKED_CAST")
private val methods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    NetVersion,
    NetListening,
    NetPeerCount
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val NetModule = Module(methods)