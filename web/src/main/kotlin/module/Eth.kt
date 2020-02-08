package org.kevm.web.module

import org.kevm.rpc.BlockDTO
import org.kevm.rpc.SendCallParamDTO
import org.kevm.rpc.SendTransactionParamDTO
import org.kevm.rpc.TransactionDTO
import org.kevm.web.jackson.ObjectTransformer

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
        val syncing = context.standardRpc.ethSyncing()
        EthSyncingResponse(request, syncing)
    }

class EthCoinbaseRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthCoinbaseResponse(request: EthCoinbaseRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthCoinbase =
    Method.create("eth_coinbase", EthCoinbaseRequest::class, EthCoinbaseResponse::class) { request, context ->
        val coinbase = context.standardRpc.ethCoinbase()
        EthCoinbaseResponse(request, coinbase)
    }

class EthMiningRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthMiningResponse(request: EthMiningRequest, result: Boolean) :
    RpcResponse<Boolean>(request, result)

private val EthMining =
    Method.create("eth_mining", EthMiningRequest::class, EthMiningResponse::class) { request, context ->
        val mining = context.standardRpc.ethMining()
        EthMiningResponse(request, mining)
    }

class EthHashrateRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthHashrateResponse(request: EthHashrateRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthHashrate =
    Method.create("eth_hashrate", EthHashrateRequest::class, EthHashrateResponse::class) { request, context ->
        val hashrate = context.standardRpc.ethHashrate()
        EthHashrateResponse(request, hashrate)
    }

class EthGasPriceRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthGasPriceResponse(request: EthGasPriceRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGasPrice =
    Method.create("eth_gasPrice", EthGasPriceRequest::class, EthGasPriceResponse::class) { request, context ->
        val gasPrice = context.standardRpc.ethGasPrice()
        EthGasPriceResponse(request, gasPrice)
    }

class EthAccountsRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthAccountsResponse(request: EthAccountsRequest, result: List<String>) :
    RpcResponse<List<String>>(request, result)

private val EthAccounts =
    Method.create("eth_accounts", EthAccountsRequest::class, EthAccountsResponse::class) { request, context ->
        val accounts = context.standardRpc.ethAccounts()
        EthAccountsResponse(request, accounts)
    }

class EthBlockNumberRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class EthBlockNumberResponse(request: EthBlockNumberRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthBlockNumber =
    Method.create("eth_blockNumber", EthBlockNumberRequest::class, EthBlockNumberResponse::class) { request, context ->
        val blockNumber = context.standardRpc.ethBlockNumber()
        EthBlockNumberResponse(request, blockNumber)
    }

class EthGetBalanceRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetBalanceResponse(request: EthGetBalanceRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetBalance =
    Method.create("eth_getBalance", EthGetBalanceRequest::class, EthGetBalanceResponse::class) { request, context ->
        val (address, blockName) = request.params
        val balance = context.standardRpc.ethGetBalance(address, blockName)
        EthGetBalanceResponse(request, balance)
    }

class EthGetStorageAtRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetStorageAtResponse(request: EthGetStorageAtRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetStorageAt =
    Method.create(
        "eth_getStorageAt",
        EthGetStorageAtRequest::class,
        EthGetStorageAtResponse::class
    ) { request, context ->
        val (address, position, blockName) = request.params
        val balance = context.standardRpc.ethGetStorageAt(address, position, blockName)
        EthGetStorageAtResponse(request, balance)
    }

class EthGetTransactionCountRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetTransactionCountResponse(request: EthGetTransactionCountRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetTransactionCount =
    Method.create(
        "eth_getTransactionCount",
        EthGetTransactionCountRequest::class,
        EthGetTransactionCountResponse::class
    ) { request, context ->
        val (address, blockName) = request.params
        val balance = context.standardRpc.ethGetTransactionCount(address, blockName)
        EthGetTransactionCountResponse(request, balance)
    }

class EthGetBlockTransactionCountByHashRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetBlockTransactionCountByHashResponse(request: EthGetBlockTransactionCountByHashRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetBlockTransactionCountByHash =
    Method.create(
        "eth_getBlockTransactionCountByHash",
        EthGetBlockTransactionCountByHashRequest::class,
        EthGetBlockTransactionCountByHashResponse::class
    ) { request, context ->
        val hash = request.params[0]
        val balance = context.standardRpc.ethGetBlockTransactionCountByHash(hash)
        EthGetBlockTransactionCountByHashResponse(request, balance)
    }

class EthGetBlockTransactionCountByNumberRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetBlockTransactionCountByNumberResponse(request: EthGetBlockTransactionCountByNumberRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetBlockTransactionCountByNumber =
    Method.create(
        "eth_getBlockTransactionCountByNumber",
        EthGetBlockTransactionCountByNumberRequest::class,
        EthGetBlockTransactionCountByNumberResponse::class
    ) { request, context ->
        val blockNumber = request.params[0]
        val balance = context.standardRpc.ethGetBlockTransactionCountByNumber(blockNumber)
        EthGetBlockTransactionCountByNumberResponse(request, balance)
    }

class EthGetUncleCountByBlockHashRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetUncleCountByBlockHashResponse(request: EthGetUncleCountByBlockHashRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetUncleCountByBlockHash =
    Method.create(
        "eth_getUncleCountByBlockHash",
        EthGetUncleCountByBlockHashRequest::class,
        EthGetUncleCountByBlockHashResponse::class
    ) { request, context ->
        val hash = request.params[0]
        val balance = context.standardRpc.ethGetUncleCountByBlockHash(hash)
        EthGetUncleCountByBlockHashResponse(request, balance)
    }

class EthGetUncleCountByBlockNumberRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetUncleCountByBlockNumberResponse(request: EthGetUncleCountByBlockNumberRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetUncleCountByBlockNumber =
    Method.create(
        "eth_getUncleCountByBlockNumber",
        EthGetUncleCountByBlockNumberRequest::class,
        EthGetUncleCountByBlockNumberResponse::class
    ) { request, context ->
        val hash = request.params[0]
        val balance = context.standardRpc.ethGetUncleCountByBlockNumber(hash)
        EthGetUncleCountByBlockNumberResponse(request, balance)
    }

class EthGetCodeRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetCodeResponse(request: EthGetCodeRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthGetCode =
    Method.create("eth_getCode", EthGetCodeRequest::class, EthGetCodeResponse::class) { request, context ->
        val (address, blockName) = request.params
        val balance = context.standardRpc.ethGetCode(address, blockName)
        EthGetCodeResponse(request, balance)
    }

class EthSignRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthSignResponse(request: EthSignRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthSign = Method.create("eth_sign", EthSignRequest::class, EthSignResponse::class) { request, context ->
    val (address, message) = request.params
    val balance = context.standardRpc.ethSign(address, message)
    EthSignResponse(request, balance)
}

class EthSendTransactionRequest(jsonrpc: String, method: String, id: Long, params: List<SendTransactionParamDTO>) :
    RpcRequest<List<SendTransactionParamDTO>>(jsonrpc, method, id, params)

class EthSendTransactionResponse(request: EthSendTransactionRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthSendTransaction = Method.create(
    "eth_sendTransaction",
    EthSendTransactionRequest::class,
    EthSendTransactionResponse::class
) { request, context ->
    val transaction = request.params[0]
    val balance = context.standardRpc.ethSendTransaction(transaction)
    EthSendTransactionResponse(request, balance)
}

class EthSendRawTransactionRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthSendRawTransactionResponse(request: EthSendRawTransactionRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthSendRawTransaction =
    Method.create(
        "eth_sendRawTransaction",
        EthSendRawTransactionRequest::class,
        EthSendRawTransactionResponse::class
    ) { request, context ->
        val data = request.params[0]
        val balance = context.standardRpc.ethSendRawTransaction(data)
        EthSendRawTransactionResponse(request, balance)
    }

class EthCallRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthCallResponse(request: EthCallRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthCall = Method.create("eth_call", EthCallRequest::class, EthCallResponse::class) { request, context ->
    val call = ObjectTransformer.transform(request.params[0] as Map<Any, Any>, SendCallParamDTO::class)
    val block = request.params[1] as String
    val data = context.standardRpc.ethCall(call, block)
    EthCallResponse(request, data)
}

class EthEstimateGasRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthEstimateGasResponse(request: EthEstimateGasRequest, result: String) :
    RpcResponse<String>(request, result)

private val EthEstimateGas =
    Method.create("eth_estimateGas", EthEstimateGasRequest::class, EthEstimateGasResponse::class) { request, context ->
        val call = ObjectTransformer.transform(
            request.params[0] as Map<Any, Any>,
            SendCallParamDTO::class
        ) // TODO - shoud crate and use EstimateGasParamDTO with more nullable fields
        val block = request.params[1] as String
        val data = context.standardRpc.ethEstimateGas(call, block)
        EthEstimateGasResponse(request, data)
    }

class EthGetBlockByHashRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthGetBlockByHashResponse(request: EthGetBlockByHashRequest, result: BlockDTO<*>?) :
    RpcResponse<BlockDTO<*>?>(request, result)

private val EthGetBlockByHash = Method.create(
    "eth_getBlockByHash",
    EthGetBlockByHashRequest::class,
    EthGetBlockByHashResponse::class
) { request, context ->
    val hash = request.params[0] as String
    val fullTransactionObjects = request.params[1] as Boolean
    val data = context.standardRpc.ethGetBlockByHash(hash, fullTransactionObjects)
    EthGetBlockByHashResponse(request, data)
}

class EthGetBlockByNumberRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthGetBlockByNumberResponse(request: EthGetBlockByNumberRequest, result: BlockDTO<*>?) :
    RpcResponse<BlockDTO<*>?>(request, result)

private val EthGetBlockByNumber = Method.create(
    "eth_getBlockByNumber",
    EthGetBlockByNumberRequest::class,
    EthGetBlockByNumberResponse::class
) { request, context ->
    val hash = request.params[0] as String
    val fullTransactionObjects = request.params[1] as Boolean
    val block = context.standardRpc.ethGetBlockByNumber(hash, fullTransactionObjects)
    EthGetBlockByNumberResponse(request, block)
}

class EthGetTransactionByHashRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetTransactionByHashResponse(request: EthGetTransactionByHashRequest, result: TransactionDTO?) :
    RpcResponse<TransactionDTO?>(request, result)

private val EthGetTransactionByHash = Method.create("eth_getTransactionByHash", EthGetTransactionByHashRequest::class, EthGetTransactionByHashResponse::class) { request, context ->
    val hash = request.params[0]
    val transaction = context.standardRpc.ethGetTransactionByHash(hash)
    EthGetTransactionByHashResponse(request, transaction)
}

class EthGetTransactionByBlockHashAndIndexRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthGetTransactionByBlockHashAndIndexResponse(request: EthGetTransactionByBlockHashAndIndexRequest, result: TransactionDTO?) :
    RpcResponse<TransactionDTO?>(request, result)

private val EthGetTransactionByBlockHashAndIndex = Method.create("eth_getTransactionByBlockHashAndIndex", EthGetTransactionByBlockHashAndIndexRequest::class, EthGetTransactionByBlockHashAndIndexResponse::class) { request, context ->
    val hash = request.params[0] as String
    val position = request.params[1] as String // TODO - check if string data type is compatible with
    val transaction = context.standardRpc.ethGetTransactionByBlockHashAndIndex(hash, position)
    EthGetTransactionByBlockHashAndIndexResponse(request, transaction)
}

class EthGetTransactionByBlockNumberAndIndexRequest(jsonrpc: String, method: String, id: Long, params: List<Any>) :
    RpcRequest<List<Any>>(jsonrpc, method, id, params)

class EthGetTransactionByBlockNumberAndIndexResponse(request: EthGetTransactionByBlockNumberAndIndexRequest, result: TransactionDTO?) :
    RpcResponse<TransactionDTO?>(request, result)

private val EthGetTransactionByBlockNumberAndIndex = Method.create("eth_getTransactionByBlockNumberAndIndex", EthGetTransactionByBlockNumberAndIndexRequest::class, EthGetTransactionByBlockNumberAndIndexResponse::class) { request, context ->
    val hash = request.params[0] as String
    val position = request.params[1] as String // TODO - check if string data type is compatible with
    val transaction = context.standardRpc.ethGetTransactionByBlockNumberAndIndex(hash, position)
    EthGetTransactionByBlockNumberAndIndexResponse(request, transaction)
}

class EthGetTransactionReceiptRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class EthGetTransactionReceiptResponse(request: EthGetTransactionReceiptRequest, result: TransactionDTO?) :
    RpcResponse<TransactionDTO?>(request, result)

private val EthGetTransactionReceipt = Method.create("eth_getTransactionReceipt", EthGetTransactionReceiptRequest::class, EthGetTransactionReceiptResponse::class) { request, context ->
    val hash = request.params[0]
    val transaction = context.standardRpc.ethGetTransactionByHash(hash)
    EthGetTransactionReceiptResponse(request, transaction)
}

@Suppress("UNCHECKED_CAST")
private val webMethods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    EthProtocolVersion,
    EthSyncing,
    EthCoinbase,
    EthMining,
    EthHashrate,
    EthGasPrice,
    EthAccounts,
    EthBlockNumber,
    EthGetBalance,
    EthGetStorageAt,
    EthGetTransactionCount,
    EthGetBlockTransactionCountByHash,
    EthGetBlockTransactionCountByNumber,
    EthGetUncleCountByBlockHash,
    EthGetUncleCountByBlockNumber,
    EthGetCode,
    EthSign,
    EthSendTransaction,
    EthSendRawTransaction,
    EthCall,
    EthEstimateGas,
    EthGetBlockByHash,
    EthGetBlockByNumber,
    EthGetTransactionByHash,
    EthGetTransactionByBlockHashAndIndex,
    EthGetTransactionByBlockNumberAndIndex,
    EthGetTransactionReceipt
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val EthModule = Module(webMethods)