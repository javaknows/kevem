package org.kevm.web3

import org.kevm.web3.modules.StandardRpcAdapter
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response
import org.web3j.protocol.core.methods.request.Transaction
import kotlin.reflect.KClass

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
class StandardRPCProvider(private val adapter: StandardRpcAdapter): RPCProvider {

    private fun <T : Any> nonNullParam(params: List<Any>, index: Int, type: KClass<T>): T
            = params.getOrNull(index) as T

    private fun <T : Any> nullableParam(params: List<Any>, index: Int, type: KClass<T>): T?
            = params.getOrNull(index) as T?

    override fun <T : Response<*>> execute(request: Request<*, out Response<*>>, responseType: Class<T>): T? {

        val method = requireNotNull(request.method) { "method field is null in web3j request" }

        return when (method) {
            "web3_clientVersion" -> adapter.web3ClientVersion()
            "web3_sha3" -> adapter.web3Sha3(
                nonNullParam(request.params, 0, String::class)
            )
            "net_version" -> adapter.netVersion()
            "net_peerCount" -> adapter.netPeerCount()
            "net_listening" -> adapter.netListening()
            "eth_protocolVersion" -> adapter.ethProtocolVersion()
            "eth_syncing" -> adapter.ethSyncing()
            "eth_coinbase" -> adapter.ethCoinbase()
            "eth_mining" -> adapter.ethMining()
            "eth_hashrate" -> adapter.ethHashrate()
            "eth_gasPrice" -> adapter.ethGasPrice()
            "eth_accounts" -> adapter.ethAccounts()
            "eth_blockNumber" -> adapter.ethBlockNumber()
            "eth_getBalance" -> adapter.ethGetBalance(
                nonNullParam(request.params, 0, String::class),
                nullableParam(request.params, 1, String::class)
            )
            "eth_getStorageAt" -> adapter.ethGetStorageAt(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, String::class),
                nullableParam(request.params, 2, String::class)
            )
            "eth_getTransactionCount" -> adapter.ethGetTransactionCount(
                nonNullParam(request.params, 0, String::class),
                nullableParam(request.params, 1, String::class)
            )
            "eth_getBlockTransactionCountByHash" -> adapter.ethGetBlockTransactionCountByHash(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_getBlockTransactionCountByNumber" -> adapter.ethGetBlockTransactionCountByNumber(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_getUncleCountByBlockHash" -> adapter.ethGetUncleCountByBlockHash(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_getUncleCountByBlockNumber" -> adapter.ethGetUncleCountByBlockNumber(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_getCode" -> adapter.ethGetCode(
                nonNullParam(request.params, 0, String::class),
                nullableParam(request.params, 1, String::class)
            )
            "eth_sign" -> adapter.ethSign(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, String::class)
            )
            "eth_sendTransaction" -> adapter.ethSendTransaction(
                nonNullParam(request.params, 0, Transaction::class)
            )
            "eth_sendRawTransaction" -> adapter.ethSendRawTransaction(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_call" -> adapter.ethCall(
                nonNullParam(request.params, 0, Transaction::class),
                nullableParam(request.params, 1, DefaultBlockParameter::class)?.toString()
            )
            "eth_estimateGas" -> adapter.ethEstimateGas(
                nonNullParam(request.params, 0, Transaction::class)
            )
            "eth_getBlockByHash" -> adapter.ethGetBlockByHash(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, Boolean::class)
            )
            "eth_getBlockByNumber" -> adapter.ethGetBlockByNumber(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, Boolean::class)
            )
            "eth_getTransactionByHash" -> adapter.ethGetTransactionByHash(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_getTransactionByBlockHashAndIndex" -> adapter.ethGetTransactionByBlockHashAndIndex(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, String::class)
            )
            "eth_getTransactionByBlockNumberAndIndex" -> adapter.ethGetTransactionByBlockNumberAndIndex(
                nonNullParam(request.params, 0, String::class),
                nonNullParam(request.params, 1, String::class)
            )
            "eth_getTransactionReceipt" -> adapter.ethGetTransactionReceipt(
                nonNullParam(request.params, 0, String::class)
            )
            "eth_pendingTransactions" -> throw UnsupportedOperationException("not implemented")
            "eth_getUncleByBlockHashAndIndex" -> throw UnsupportedOperationException("not implemented")
            "eth_getUncleByBlockNumberAndIndex" -> throw UnsupportedOperationException("not implemented")
            "eth_getCompilers" -> throw UnsupportedOperationException("not implemented")
            "eth_compileLLL" -> throw UnsupportedOperationException("not implemented")
            "eth_compileSolidity" -> throw UnsupportedOperationException("not implemented")
            "eth_compileSerpent" -> throw UnsupportedOperationException("not implemented")
            "eth_newFilter" -> throw UnsupportedOperationException("not implemented")
            "eth_newBlockFilter" -> throw UnsupportedOperationException("not implemented")
            "eth_newPendingTransactionFilter" -> throw UnsupportedOperationException("not implemented")
            "eth_uninstallFilter" -> throw UnsupportedOperationException("not implemented")
            "eth_getFilterChanges" -> throw UnsupportedOperationException("not implemented")
            "eth_getFilterLogs" -> throw UnsupportedOperationException("not implemented")
            "eth_getLogs" -> throw UnsupportedOperationException("not implemented")
            "eth_getWork" -> throw UnsupportedOperationException("not implemented")
            "eth_submitWork" -> throw UnsupportedOperationException("not implemented")
            "eth_submitHashrate" -> throw UnsupportedOperationException("not implemented")
            "eth_getProof" -> throw UnsupportedOperationException("not implemented")
            "db_putString" -> throw UnsupportedOperationException("not implemented")
            "db_getString" -> throw UnsupportedOperationException("not implemented")
            "db_putHex" -> throw UnsupportedOperationException("not implemented")
            "db_getHex" -> throw UnsupportedOperationException("not implemented")
            "shh_post" -> throw UnsupportedOperationException("not implemented")
            "shh_version" -> throw UnsupportedOperationException("not implemented")
            "shh_newIdentity" -> throw UnsupportedOperationException("not implemented")
            "shh_hasIdentity" -> throw UnsupportedOperationException("not implemented")
            "shh_newGroup" -> throw UnsupportedOperationException("not implemented")
            "shh_addToGroup" -> throw UnsupportedOperationException("not implemented")
            "shh_newFilter" -> throw UnsupportedOperationException("not implemented")
            "shh_uninstallFilter" -> throw UnsupportedOperationException("not implemented")
            "shh_getFilterChanges" -> throw UnsupportedOperationException("not implemented")
            "shh_getMessages" -> throw UnsupportedOperationException("not implemented")

            else -> null
        } as T
    }


}