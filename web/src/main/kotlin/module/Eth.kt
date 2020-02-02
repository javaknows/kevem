package org.kevm.web.module

class EthProtocolVersionRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthProtocolVersionResponse(request: EthProtocolVersionRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthProtocolVersion =
    Method.create(
        "eth_protocolVersion",
        EthProtocolVersionRequest::class,
        EthProtocolVersionResponse::class
    ) { request, context ->
        val web3EthProtocolVersion = context.standardRpc.ethProtocolVersion()
        EthProtocolVersionResponse(request, web3EthProtocolVersion)
    }

class EthSyncingRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthSyncingResponse(request: EthSyncingRequest, result: Boolean) :
    RpcResponse<Boolean>(request, result)

private val EthSyncing =
    Method.create("eth_syncing", EthSyncingRequest::class, EthSyncingResponse::class) { request, context ->
        val web3EthSyncing = context.standardRpc.ethSyncing()
        EthSyncingResponse(request, web3EthSyncing)
    }

class EthCoinbaseRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthCoinbaseResponse(request: EthCoinbaseRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthCoinbase =
    Method.create("eth_coinbase", EthCoinbaseRequest::class, EthCoinbaseResponse::class) { request, context ->
        val web3EthCoinbase = context.standardRpc.ethCoinbase()
        EthCoinbaseResponse(request, web3EthCoinbase)
    }

class EthMiningRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthMiningResponse(request: EthMiningRequest, result: Boolean) :
    RpcResponse<Boolean>(request, result)

private val EthMining =
    Method.create("eth_mining", EthMiningRequest::class, EthMiningResponse::class) { request, context ->
        val web3EthMining = context.standardRpc.ethMining()
        EthMiningResponse(request, web3EthMining)
    }

class EthHashrateRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthHashrateResponse(request: EthHashrateRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthHashrate =
    Method.create("eth_hashrate", EthHashrateRequest::class, EthHashrateResponse::class) { request, context ->
        val web3EthHashrate = context.standardRpc.ethHashrate()
        EthHashrateResponse(request, web3EthHashrate)
    }

class EthGasPriceRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthGasPriceResponse(request: EthGasPriceRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGasPrice =
    Method.create("eth_gasPrice", EthGasPriceRequest::class, EthGasPriceResponse::class) { request, context ->
        val web3EthGasPrice = context.standardRpc.ethGasPrice()
        EthGasPriceResponse(request, web3EthGasPrice)
    }

class EthAccountsRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthAccountsResponse(request: EthAccountsRequest, result: List<String>) :
    RpcResponse<List<String>>(request, result)

private val EthAccounts =
    Method.create("eth_accounts", EthAccountsRequest::class, EthAccountsResponse::class) { request, context ->
        val web3EthAccounts = context.standardRpc.ethAccounts()
        EthAccountsResponse(request, web3EthAccounts)
    }

@Suppress("UNCHECKED_CAST")
private val webMethods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    EthProtocolVersion,
    EthSyncing,
    EthCoinbase,
    EthMining,
    EthHashrate,
    EthGasPrice,
    EthAccounts
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val EthModule = Module(webMethods)