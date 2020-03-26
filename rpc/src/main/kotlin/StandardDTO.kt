package org.kevem.rpc

data class RequestDTO<T>(val jsonrpc: String, val id: Int, val params: List<T>)

data class ErrorDTO(val code: Int, val message: String)

sealed class ResponseDTO<T>(val jsonrpc: String, val id: Int)
class SuccessResponseDTO<T>(jsonrpc: String, id: Int, val result: T) : ResponseDTO<T>(jsonrpc, id)
class ErrorResponseDTO<T>(jsonrpc: String, id: Int, val error: ErrorDTO?) : ResponseDTO<T>(jsonrpc, id)

data class LogDTO(
    val removed: Boolean,
    val logIndex: String,
    val transactionIndex: String,
    val transactionHash: String,
    val blockHash: String,
    val blockNumber: String,
    val address: String,
    val data: String,
    val topics: List<String>
)

data class TransactionReceiptDTO(
    val transactionHash: String,
    val transactionIndex: String,
    val blockHash: String,
    val blockNumber: String,
    val from: String,
    val to: String?,
    val cumulativeGasUsed: String,
    val gasUsed: String,
    val contractAddress: String?,
    val logs: List<LogDTO>,
    val logsBloom: String,
    val status: String
)

data class BlockDTO<T>(
    val number: String,
    val hash: String,
    val parentHash: String,
    val nonce: String,
    val sha3Uncles: String,
    val logsBloom: String,
    val transactionsRoot: String,
    val stateRoot: String,
    val miner: String,
    val difficulty: String,
    val totalDifficulty: String,
    val extraData: String,
    val size: String,
    val gasLimit: String,
    val gasUsed: String,
    val timestamp: String,
    val transactions: List<T>,
    val uncles: List<String>
)

typealias BlockWithTxHashOnlyDTO = BlockDTO<String>

typealias BlockWithFullTxDTO = BlockDTO<TransactionDTO>

data class TransactionDTO(
    val blockHash: String?,
    val blockNumber: String?,
    val from: String,
    val gas: String,
    val gasPrice: String,
    val hash: String,
    val input: String,
    val nonce: String,
    val to: String?,
    val transactionIndex: String?,
    val value: String,
    val v: String,
    val r: String,
    val s: String
)

data class EthFilterChangeDTO(
    val logIndex: String,
    val blockNumber: String,
    val blockHash: String,
    val transactionHash: String,
    val transactionIndex: String,
    val address: String,
    val data: String,
    val topics: List<String>
)

data class StorageProofDTO(
    val key: String,
    val value: String,
    val proof: List<String>
)

data class ProofDTO(
    val address: String,
    val accountProof: List<String>,
    val balance: String,
    val codeHash: String,
    val nonce: String,
    val storageHash: String,
    val storageProof: List<StorageProofDTO>
)

data class ShhFilterChangeDTO(
    val hash: String,
    val from: String,
    val to: String,
    val expiry: String,
    val sent: String,
    val ttl: String,
    val topics: List<String>,
    val payload: String,
    val workProved: String
)

data class SendTransactionParamDTO(
    val from: String,
    val to: String? = null,
    val gas: String = "0x15f90",
    val gasPrice: String, // may be optional in the future - see https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
    val value: String? = "0x0",
    val data: String = "",
    val nonce: String? = null
)

data class SendCallParamDTO(
    val from: String, // TODO - should be optional
    val to: String,
    val gas: String? = "0x0",
    val gasPrice: String = "0x0",
    val value: String? = "0x0",
    val data: String? = ""
)

typealias BlockByHashParamDTO = Pair<String, Boolean>

typealias BlockByNumberParamDTO = Pair<String, Boolean>

typealias NewFilterParamDTO = Pair<String, Boolean>

data class EthNewFilterParamDTO(
    val fromBlock: String,
    val toBlock: String,
    val address: String,
    val topics: List<String>
)

data class GetLogsParamDTO(
    val fromBlock: String?,
    val toBlock: String?,
    val address: List<String>?,
    val topics: List<String>?,
    val blockHash: String?
)

data class EmptyDTO(
    val error: String
)