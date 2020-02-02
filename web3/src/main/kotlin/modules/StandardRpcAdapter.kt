package org.kevm.web3.modules

import org.kevm.evm.bytesToString
import org.kevm.evm.toStringHexPrefix
import org.kevm.rpc.*
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.*
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.ShhPost
import org.web3j.protocol.core.methods.response.*
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.management.AdminNodeInfo
import java.math.BigInteger
import org.web3j.protocol.core.methods.response.Transaction as Web3TransactionResponse

/**
 * Adapts to/from Web3 objects to KEV-M objects
 */
class StandardRpcAdapter(private val standardRPC: StandardRPC) {

    fun ethGetTransactionCount(address: String, block: String?): EthGetTransactionCount {
        return EthGetTransactionCount().apply {
            result = standardRPC.ethGetTransactionCount(address, block)
        }
    }

    fun ethSendRawTransaction(signedTransactionData: String?): EthSendTransaction {
        val std = requireNotNull(signedTransactionData) { "signed transaction data is null" }

        return EthSendTransaction().apply {
            result = standardRPC.ethSendRawTransaction(std)
        }
    }

    fun ethGetTransactionReceipt(hash: String?): EthGetTransactionReceipt {
        val h = requireNotNull(hash) { "receipt hash is null" }

        val receipt = standardRPC.ethGetTransactionReceipt(h)

        val txRecepit = if (receipt != null) {
            TransactionReceipt(
                receipt.transactionHash,
                receipt.transactionIndex,
                receipt.blockHash,
                receipt.blockNumber,
                receipt.cumulativeGasUsed,
                receipt.gasUsed,
                receipt.contractAddress,
                "0x0",
                receipt.status,
                receipt.from,
                receipt.to,
                receipt.logs.map {
                    Log(
                        it.removed,
                        it.logIndex,
                        it.transactionIndex,
                        it.transactionHash,
                        it.blockHash,
                        it.blockNumber,
                        it.address,
                        it.data,
                        "", // TODO - what is this
                        it.topics
                    )
                },
                "0x0"
            )
        } else TransactionReceipt()

        return EthGetTransactionReceipt().apply { result = txRecepit }
    }

    fun ethCompileSolidity(sourceCode: String?): EthCompileSolidity = notImplemented()

    fun ethCoinbase(): EthCoinbase = EthCoinbase().apply { result = standardRPC.ethCoinbase() }

    fun ethGetUncleCountByBlockNumber(defaultBlockParameter: String?): EthGetUncleCountByBlockNumber {
        val count = standardRPC.ethGetUncleCountByBlockNumber(defaultBlockParameter)

        return EthGetUncleCountByBlockNumber().apply { result = count }
    }

    fun ethGetTransactionByBlockHashAndIndex(blockHash: String, transactionIndex: String): EthTransaction {
        val tx = standardRPC.ethGetTransactionByBlockHashAndIndex(blockHash, transactionIndex)

        return EthTransaction().apply { result = toWeb3TransactionResponse(tx) }
    }

    fun dbGetString(databaseName: String?, keyName: String?): DbGetString = notImplemented()

    fun shhVersion(): ShhVersion = notImplemented()

    fun adminNodeInfo(): AdminNodeInfo = notImplemented()

    fun shhAddToGroup(identityAddress: String?): ShhAddToGroup = notImplemented()

    fun ethGetFilterChanges(filterId: BigInteger?): EthLog = notImplemented()

    fun shhGetMessages(filterId: BigInteger?): ShhMessages = notImplemented()

    fun ethGetWork(): EthGetWork = notImplemented()

    fun dbPutHex(databaseName: String?, keyName: String?, dataToStore: String?): DbPutHex = notImplemented()

    fun ethCompileSerpent(sourceCode: String?): EthCompileSerpent = notImplemented()

    fun shhNewFilter(shhFilter: ShhFilter?): ShhNewFilter = notImplemented()

    fun ethGetFilterLogs(filterId: BigInteger?): EthLog = notImplemented()

    fun ethGetUncleCountByBlockHash(blockHash: String?): EthGetUncleCountByBlockHash = notImplemented()

    fun web3Sha3(data: String): Web3Sha3 = Web3Sha3().apply { result = standardRPC.web3sha3(data) }

    fun shhUninstallFilter(filterId: BigInteger?): ShhUninstallFilter = notImplemented()

    fun ethGetBlockTransactionCountByHash(blockHash: String): EthGetBlockTransactionCountByHash =
        EthGetBlockTransactionCountByHash().apply { result = standardRPC.ethGetBlockTransactionCountByHash(blockHash) }

    fun ethNewBlockFilter(): org.web3j.protocol.core.methods.response.EthFilter = notImplemented()

    fun netVersion(): NetVersion = NetVersion().apply { result = standardRPC.netVersion() }

    fun netPeerCount(): NetPeerCount = NetPeerCount().apply { result = standardRPC.netPeerCount() }

    fun ethGetTransactionByHash(transactionHash: String): EthTransaction {
        val tx = standardRPC.ethGetTransactionByHash(transactionHash)

        return EthTransaction().apply { result = toWeb3TransactionResponse(tx) }
    }

    fun ethSign(address: String, sha3HashOfDataToSign: String): EthSign =
        EthSign().apply { result = standardRPC.ethSign(address, sha3HashOfDataToSign) }

    fun ethNewFilter(ethFilter: EthFilter?): EthFilter = notImplemented()

    fun shhPost(shhPost: ShhPost?): org.web3j.protocol.core.methods.response.ShhPost = notImplemented()

    fun ethSendTransaction(transaction: Transaction): EthSendTransaction {
        val tx = SendTransactionParamDTO(
            transaction.from,
            transaction.to,
            transaction.gas,
            transaction.gasPrice,
            transaction.value,
            transaction.data ?: "",
            transaction.nonce
        )

        return EthSendTransaction().apply { result = standardRPC.ethSendTransaction(tx) }
    }

    fun ethGasPrice(): EthGasPrice = EthGasPrice().apply { result = standardRPC.ethGasPrice() }

    fun ethCall(transaction: Transaction, defaultBlockParameter: String?): EthCall {
        val call = SendCallParamDTO(
            transaction.from,
            transaction.to,
            transaction.gas,
            transaction.gasPrice,
            transaction.value,
            transaction.data
        )

        val callResult = bytesToString(standardRPC.ethCall(call, defaultBlockParameter))

        return EthCall().apply { result = callResult }
    }

    fun web3ClientVersion(): Web3ClientVersion = Web3ClientVersion().apply { result = standardRPC.web3clientVersion() }

    fun ethBlockNumber(): EthBlockNumber = EthBlockNumber().apply { result = standardRPC.ethBlockNumber() }

    // TODO - add chain ID method
    fun ethChainId(): EthChainId = TODO()

    fun shhHasIdentity(identityAddress: String?): ShhHasIdentity = notImplemented()

    fun ethGetBlockByNumber(
        defaultBlockParameter: String,
        returnFullTransactionObjects: Boolean
    ): EthBlock {
        val block = standardRPC.ethGetBlockByNumber(defaultBlockParameter, returnFullTransactionObjects)

        return EthBlock().apply {
            result = toWeb3Block(block)
        }
    }

    private fun <T> convertTransactions(blockByNumber: List<T>): List<EthBlock.TransactionResult<*>> =
        blockByNumber.map {
            when (it) {
                is String -> EthBlock.TransactionHash(it)
                is TransactionDTO -> EthBlock.TransactionObject(
                    it.hash,
                    it.nonce,
                    it.blockHash,
                    it.blockNumber,
                    it.transactionIndex,
                    it.from,
                    it.to,
                    it.value,
                    it.gasPrice,
                    it.gas,
                    it.input,
                    null, // creates
                    null, // publicKey
                    null, // raw
                    it.r,
                    it.s,
                    toBigInteger(it.v).toInt()
                )
                else -> TODO()
            }
        }

    fun ethGetCompilers(): EthGetCompilers = EthGetCompilers().apply { result = standardRPC.ethGetCompilers() }

    fun ethUninstallFilter(filterId: BigInteger?): EthUninstallFilter = notImplemented()

    fun ethSyncing(): EthSyncing =
        EthSyncing().apply { result = EthSyncing.Result().apply { isSyncing = standardRPC.ethSyncing() } }

    fun ethEstimateGas(transaction: Transaction): EthEstimateGas {
        val call = SendCallParamDTO(
            transaction.from,
            transaction.to,
            transaction.gas,
            transaction.gasPrice,
            transaction.value,
            transaction.data
        )

        return EthEstimateGas().apply {
            result = standardRPC.ethEstimateGas(call)
        }
    }

    fun shhNewGroup(): ShhNewGroup = notImplemented()

    fun ethSubmitWork(nonce: String?, headerPowHash: String?, mixDigest: String?): EthSubmitWork = notImplemented()

    fun ethHashrate(): EthHashrate = EthHashrate().apply{
        result = standardRPC.ethHashrate()
    }

    fun shhGetFilterChanges(filterId: BigInteger?): ShhMessages = notImplemented()

    fun ethGetBalance(address: String, defaultBlockParameter: String??): EthGetBalance {
        val balance = standardRPC.ethGetBalance(address, defaultBlockParameter)

        return EthGetBalance().apply {
            result = balance
        }
    }

    fun netListening(): NetListening = NetListening().apply {
        result = standardRPC.netListening()
    }

    fun ethSubmitHashrate(hashrate: String?, clientId: String?): EthSubmitHashrate = notImplemented()

    fun ethAccounts(): EthAccounts = EthAccounts().apply { result = standardRPC.ethAccounts() }

    fun ethGetLogs(ethFilter: EthFilter): EthLog {
        val filter = GetLogsParamDTO(
            ethFilter.fromBlock?.value,
            ethFilter.toBlock?.value,
            ethFilter.address,
            toTopicsDTO(ethFilter.topics),
            null // TODO - web3 doesn't seem to support blockHash
        )

        val logs = standardRPC.ethGetLogs(filter).map {
            EthLog.LogObject(
                it.removed,
                it.logIndex,
                it.transactionIndex,
                it.transactionHash,
                it.blockHash,
                it.blockNumber,
                it.address,
                it.data,
                null, // TODO - what is type - doesn't appear in https://github.com/ethereum/wiki/wiki/JSON%20RPC#eth_getfilterchanges
                it.topics
            )
        }

        return EthLog().apply { result = logs }
    }

    fun ethGetBlockByHash(blockHash: String, returnFullTransactionObjects: Boolean): EthBlock {
        val block = standardRPC.ethGetBlockByHash(blockHash, returnFullTransactionObjects)

        return EthBlock().apply {
            result = toWeb3Block(block)
        }
    }

    fun ethGetCode(address: String, defaultBlockParameter: String?): EthGetCode {
        val code = standardRPC.ethGetCode(address, defaultBlockParameter)

        return EthGetCode().apply {
            result = code
        }
    }

    fun dbPutString(databaseName: String?, keyName: String?, stringToStore: String?): DbPutString = notImplemented()

    fun ethCompileLLL(sourceCode: String?): EthCompileLLL = notImplemented()

    fun ethGetBlockTransactionCountByNumber(defaultBlockParameter: String?): EthGetBlockTransactionCountByNumber {
        val count = standardRPC.ethGetBlockTransactionCountByNumber(defaultBlockParameter)

        return EthGetBlockTransactionCountByNumber().apply {
            result = count
        }
    }

    fun ethNewPendingTransactionFilter(): org.web3j.protocol.core.methods.response.EthFilter = notImplemented()

    fun ethGetTransactionByBlockNumberAndIndex(
        defaultBlockParameter: String,
        transactionIndex: String
    ): EthTransaction {
        val tx = standardRPC.ethGetTransactionByBlockNumberAndIndex(
            defaultBlockParameter,
            transactionIndex
        )

        return EthTransaction().apply {
            result = toWeb3TransactionResponse(tx)
        }
    }

    fun ethGetUncleByBlockHashAndIndex(blockHash: String?, transactionIndex: BigInteger?): EthBlock = notImplemented()

    fun shhNewIdentity(): ShhNewIdentity = notImplemented()

    fun ethGetStorageAt(
        address: String,
        position: String,
        defaultBlockParameter: String?
    ): EthGetStorageAt = EthGetStorageAt().apply {
        result = standardRPC.ethGetStorageAt(address, position, defaultBlockParameter)
    }

    fun ethMining(): EthMining = EthMining().apply {
        result = standardRPC.ethMining()
    }

    fun ethGetUncleByBlockNumberAndIndex(
        defaultBlockParameter: DefaultBlockParameter,
        transactionIndex: BigInteger
    ): EthBlock = notImplemented()

    fun ethProtocolVersion(): EthProtocolVersion = EthProtocolVersion().apply {
        result = standardRPC.ethProtocolVersion()
    }

    fun dbGetHex(databaseName: String?, keyName: String?): DbGetHex = notImplemented()

    private fun toWeb3Block(block: BlockDTO<*>?): EthBlock.Block? =
        if (block == null) null
        else EthBlock.Block(
            block.number,
            block.hash,
            block.parentHash,
            block.nonce,
            block.sha3Uncles,
            block.logsBloom,
            block.transactionsRoot,
            block.stateRoot,
            null, //receiptsRoot
            null, // author
            block.miner,
            null, // mixHash
            block.difficulty,
            block.totalDifficulty,
            block.extraData,
            block.size,
            block.gasLimit,
            block.gasUsed,
            block.timestamp,
            convertTransactions(block.transactions),
            block.uncles,
            null //sealFields
        )

    private fun toTopicsDTO(topics: List<Filter.FilterTopic<Any>>): List<String> = topics.flatMap {
        when (it) {
            is Filter.SingleTopic -> listOf(it.value)
            is Filter.ListTopic -> it.value.map { it.value }
            else -> TODO("")
        }.filterNotNull()
    }

    private fun notImplemented(): Nothing = throw UnsupportedOperationException("Not implemented")

    private fun toWeb3TransactionResponse(tx: TransactionDTO?): org.web3j.protocol.core.methods.response.Transaction? =
        if (tx == null) null
        else Web3TransactionResponse(
            tx.hash,
            tx.nonce,
            tx.blockHash,
            tx.blockNumber,
            tx.transactionIndex,
            tx.from,
            tx.to,
            tx.value,
            tx.gas,
            tx.gasPrice,
            tx.input,
            null, // TODO - include created address ?
            null, // TODO - include publicKey ?
            null, // TODO - include raw?
            tx.v,
            tx.r,
            toBigInteger(tx.s).toLong()
        )

}