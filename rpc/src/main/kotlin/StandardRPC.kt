package org.kevm.rpc

import org.kevm.common.KevmException
import org.kevm.evm.bytesToString
import org.kevm.evm.crypto.keccak256
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.evm.toStringHexPrefix
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Sign
import java.math.BigInteger
import java.time.Instant

data class AppConfig(
    val chainId: Int = 0,
    val netVersion: Int = 1,
    val peerCount: Int = 0,
    val coinbase: String = "0x0",
    val hashRate: BigInteger = BigInteger.ZERO,
    val clientVersion: String = "KEVM TestRPC", // TODO - generate real client version
    val difficulty: BigInteger = BigInteger.ZERO, // 17,171,480,576
    val extraData: Word = Word.Zero,
    val gasPrice: BigInteger = BigInteger("20000000000"),
    val blockGasLimit: BigInteger = BigInteger("1000000000000000000000000000000"),
    val genesisBlockTimestamp: Instant = Instant.parse("2015-06-30T03:26:28.00Z")
)

class RpcException(msg: String) : KevmException(msg)

class CategorisedRpcException(val code: Int, msg: String) : KevmException(msg)

/**
 * https://github.com/ethereum/wiki/wiki/JSON-RPC
 */
class StandardRPC(
    private val standardEvmOperations: StandardEvmOperations,
    private val config: AppConfig,
    private val localAccounts: LocalAccounts = LocalAccounts(),
    private val transactionSigner: TransactionSigner = TransactionSigner()
) {

    fun web3clientVersion(): String = config.clientVersion

    fun web3sha3(data: String): String = bytesToString(keccak256(toByteList(data)))

    fun netVersion(): String = config.netVersion.toString(10)

    fun netPeerCount(): String = config.peerCount.toStringHexPrefix()

    fun netListening(): Boolean = true

    fun ethProtocolVersion(): String = "63" // Does this change after a hard-fork?

    fun ethSyncing(): Boolean = false

    fun ethCoinbase(): String = config.coinbase

    fun ethMining(): Boolean = true

    fun ethHashrate(): String = config.hashRate.toStringHexPrefix()

    fun ethGasPrice(): String = config.gasPrice.toStringHexPrefix()

    fun ethAccounts(): List<String> = localAccounts.accounts.map { it.address.toString() }

    fun ethBlockNumber(): String = standardEvmOperations.blockNumber().toStringHexPrefix()

    fun ethGetBalance(address: String, block: String? = "latest"): String =
        standardEvmOperations.getBalance(Address(address), BlockReference.fromString(block)).toStringHexPrefix()

    fun ethGetStorageAt(address: String, location: String, block: String?): String {
        val a = toAddress(address)
        val l = toBigInteger(location)
        val b = BlockReference.fromString(block)
        return standardEvmOperations.getStorageAt(a, l, b).toString()
    }

    fun ethGetTransactionCount(address: String, block: String?): String {
        val a = toAddress(address)
        val b = BlockReference.fromString(block)
        return standardEvmOperations.getTransactionCount(a, b).toStringHexPrefix()
    }

    fun ethGetBlockTransactionCountByHash(hash: String): String =
        standardEvmOperations.getBlockTransactionCountByHash(toByteList(hash)).toStringHexPrefix()

    fun ethGetBlockTransactionCountByNumber(block: String?): String =
        standardEvmOperations.getBlockTransactionCountByNumber(BlockReference.fromString(block)).toStringHexPrefix()

    fun ethGetUncleCountByBlockHash(hash: String): String =
        standardEvmOperations.getUncleCountByBlockHash(toByteList(hash)).toStringHexPrefix()

    fun ethGetUncleCountByBlockNumber(block: String?) =
        standardEvmOperations.getUncleCountByBlockNumber(BlockReference.fromString(block)).toStringHexPrefix()

    fun ethGetCode(address: String, block: String?): String {
        val a = toAddress(address)
        val b = BlockReference.fromString(block)
        return bytesToString(standardEvmOperations.getCode(a, b))
    }

    fun ethSign(address: String, data: String): String {
        val account = getUnlockedAccount(address)
        val dataBytes = toByteList(data).map { it.javaByte() }.toByteArray()

        val privateKey = BigInteger(account.privateKey.map { it.toStringNoHexPrefix() }.joinToString(""), 16)
        val publicKey = Sign.publicKeyFromPrivate(privateKey)
        val keyPair = ECKeyPair(privateKey, publicKey)

        val signed = Sign.signPrefixedMessage(dataBytes, keyPair)

        val s = zeroPadTruncate(signed.r.toList(), 32) +
                zeroPadTruncate(signed.s.toList(), 32) +
                signed.v.map{ (it.toInt() - 27).toByte() }.toList() // not sure if subtracting the 27 is correct here

        return bytesToString(s.map { Byte(it.toInt() and 0xFF) })
    }

    private fun zeroPadTruncate(bytes: List<kotlin.Byte>, length: Int): List<kotlin.Byte> {
        val zeros = (0 until length - bytes.size).map { 0.toByte() }
        return (zeros + bytes).take(length)
    }

    private fun getUnlockedAccount(address: String): LocalAccount {
        val account = localAccounts.getByAddress(Address(address))

        return when {
            account == null -> throw RpcException("$address is not a local account")
            account.locked -> throw RpcException("account $account is locked")
            else -> account
        }
    }

    fun ethSendTransaction(transaction: SendTransactionParamDTO): String {
        val account = getUnlockedAccount(transaction.from)

        val nonce = if (transaction.nonce != null) toBigInteger(transaction.nonce)
        else standardEvmOperations.getNonce(account.address)

        val signed = transactionSigner.sign(transaction, account)
        val hash = keccak256(signed)

        val tx = TransactionMessage(
            account.address,
            toAddressOrNull(transaction.to),
            toBigIntegerOrZero(transaction.value),
            toBigInteger(transaction.gasPrice),
            toBigIntegerOrZero(transaction.gas),
            toByteList(transaction.data),
            nonce,
            hash
        )

        return bytesToString(standardEvmOperations.sendTransaction(tx))
    }

    private fun toAddressOrNull(X: String?) =
        if (X == null || isEmptyHex(X)) null
        else Address(X)

    fun ethSendRawTransaction(signedTxData: String): String {
        val signedTxDataBytes = toByteList(signedTxData)
        val hash = standardEvmOperations.sendRawTransaction(signedTxDataBytes)

        return bytesToString(hash)
    }

    fun ethCall(transaction: SendCallParamDTO, block: String?): String {
        val gas =
            if (transaction.gas == null) standardEvmOperations.pendingBlockGasLimit()
            else toBigInteger(transaction.gas)

        val tx = TransactionMessage(
            Address(transaction.from),
            toAddressOrNull(transaction.to),
            toBigIntegerOrZero(transaction.value),
            toBigInteger(transaction.gasPrice),
            gas,
            toByteList(transaction.data),
            BigInteger.ZERO,
            emptyList()
        )

        return bytesToString(standardEvmOperations.call(tx, BlockReference.fromString(block)))
    }

    fun ethEstimateGas(transaction: SendCallParamDTO, block: String = "latest"): String {
        val gas =
            if (transaction.gas == null) standardEvmOperations.pendingBlockGasLimit()
            else toBigInteger(transaction.gas)

        val tx = TransactionMessage(
            Address(transaction.from),
            toAddressOrNull(transaction.to),
            toBigIntegerOrZero(transaction.value),
            toBigInteger(transaction.gasPrice),
            gas,
            toByteList(transaction.data),
            BigInteger.ZERO,
            emptyList()
        )

        return standardEvmOperations.estimateGas(tx, BlockReference.fromString(block)).toStringHexPrefix()
    }

    fun ethGetBlockByHash(hash: String, fullTransactionObjects: Boolean): BlockDTO<*>? {
        val block = standardEvmOperations.getBlockByHash(toByteList(hash))

        return toBlockDTO(block, fullTransactionObjects)
    }

    fun ethGetBlockByNumber(block: String, fullTransactionObjects: Boolean): BlockDTO<*>? {
        val b = standardEvmOperations.getBlockByNumber(BlockReference.fromString(block))

        return toBlockDTO(b, fullTransactionObjects)
    }

    fun ethGetTransactionByHash(hash: String): TransactionDTO? {
        val pair = standardEvmOperations.getTransactionByHash(toByteList(hash))

        return toTransactionDTOOrNull(pair)
    }

    fun ethGetTransactionByBlockHashAndIndex(hash: String, index: String): TransactionDTO? {
        val pair = standardEvmOperations.getTransactionByBlockHashAndIndex(toByteList(hash), toBigInteger(index))

        return toTransactionDTOOrNull(pair)
    }

    fun ethGetTransactionByBlockNumberAndIndex(block: String, index: String): TransactionDTO? {
        val pair = standardEvmOperations.getTransactionByBlockNumberAndIndex(
            BlockReference.fromString(block),
            toBigInteger(index)
        )

        return toTransactionDTOOrNull(pair)
    }

    fun ethGetTransactionReceipt(txHash: String): TransactionReceiptDTO? {
        val hash = toByteList(txHash)

        val pair = standardEvmOperations.getTransactionReceipt(hash)

        return toTransactionReceiptDTO(pair, hash, txHash)
    }

    fun ethPendingTransactions(): List<TransactionDTO> = emptyList()

    fun ethGetUncleByBlockHashAndIndex() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethGetUncleByBlockNumberAndIndex() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethGetCompilers(): List<String> = emptyList()

    fun ethCompileLLL() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethCompileSolidity(): EmptyDTO {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethCompileSerpent() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethNewFilter() {
        TODO()
    }

    fun ethNewBlockFilter() {
        TODO()
    }

    fun ethNewPendingTransactionFilter() {
        TODO()
    }

    fun ethUninstallFilter() {
        TODO()
    }

    fun ethGetFilterChanges() {
        TODO()
    }

    fun ethGetFilterLogs() {
        TODO()
    }

    fun ethGetLogs(filter: GetLogsParamDTO): List<LogDTO> {
        val from = BlockReference.fromString(filter.fromBlock)
        val to = BlockReference.fromString(filter.toBlock)
        val address = filter.address?.mapNotNull { toAddressOrNull(it) }
        val topics = filter.topics?.map { Word.coerceFrom(it) }
        val blockHash = if (filter.blockHash != null) Word.coerceFrom(filter.blockHash).data else null

        val logs = standardEvmOperations.getLogs(from, to, address, topics, blockHash)

        return logs.map { log ->
            // TODO - add all the tx and block values
            LogDTO(
                false,
                "0x0",
                "0x0",
                "0x0",
                "0x0",
                "0x0",
                "0x0",
                bytesToString(log.data),
                log.topics.map { it.toStringNoHexPrefix() })
        }
    }

    fun ethGetWork() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethSubmitWork() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethSubmitHashrate() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun ethGetProof() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun dbpUtString() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun dbgEtString() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun dbpUtHex() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun dbgEtHex() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhPost() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhVersion() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhNewIdentity() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhHasIdentity() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhNewGroup() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhAddToGroup() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhNewFilter() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhUninstallFilter() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhGetFilterChanges() {
        throw UnsupportedOperationException("Not implemented")
    }

    fun shhGetMessages() {
        throw UnsupportedOperationException("Not implemented")
    }

    private fun toTransactionReceiptDTO(
        pair: Pair<MinedTransaction, MinedBlock>?,
        hash: List<Byte>,
        txHash: String
    ): TransactionReceiptDTO? {
        return if (pair != null) {
            val (tx, block) = pair
            val txIndex = getTxIndex(block, hash)

            TransactionReceiptDTO(
                txHash,
                txIndex.toStringHexPrefix(),
                bytesToString(block.hash),
                block.block.number.toStringHexPrefix(),
                tx.message.from.toString(),
                tx.message.to.toString(),
                block.gasUsed.toStringHexPrefix(), // TODO - should be cumulativeGasUsed
                block.gasUsed.toStringHexPrefix(),
                tx.result.created?.toString(),
                tx.result.logs.mapIndexed { i, l -> toLogDTO(i, l, txIndex, tx, block) }, // TODO - populate
                "0x0",
                if (tx.result.status == ResultStatus.COMPLETE) "0x1" else "0x0"
            )
        } else null
    }

    private fun toLogDTO(index: Int, log: Log, txIndex: Int, tx: MinedTransaction, block: MinedBlock): LogDTO =
        LogDTO(
            false,
            index.toStringHexPrefix(),
            txIndex.toStringHexPrefix(),
            bytesToString(tx.message.hash),
            bytesToString(block.hash),
            block.block.number.toStringHexPrefix(),
            tx.message.from.toString(), // TODO - should be address where log originated, not "from"
            bytesToString(log.data),
            log.topics.map { it.toString() }
        )

    private fun toTransactionDTOOrNull(pair: Pair<MinedTransaction, MinedBlock>?): TransactionDTO? =
        if (pair != null) toTransactionDTO(pair)
        else null

    private fun toTransactionDTO(pair: Pair<MinedTransaction, MinedBlock>): TransactionDTO {
        val (tx, block) = pair
        val txIndex = getTxIndex(block, tx.message.hash)

        return TransactionDTO(
            bytesToString(block.hash),
            block.block.number.toStringHexPrefix(),
            tx.message.from.toString(),
            tx.result.gasUsed.toStringHexPrefix(),
            tx.message.gasPrice.toStringHexPrefix(),
            bytesToString(tx.message.hash),
            bytesToString(tx.message.data),
            tx.message.nonce.toStringHexPrefix(),
            tx.message.to?.toString(),
            txIndex.toStringHexPrefix(),
            tx.message.value.toStringHexPrefix(),
            "0x0", // TODO - include V, R, S
            "0x0",
            "0x0"
        )
    }


    private fun getTxIndex(block: MinedBlock, hash: List<Byte>): Int {
        val txZippedWithIndex = block.transactions.indices zip block.transactions
        return txZippedWithIndex
            .find { it.second.message.hash == hash }?.first
            ?: throw RuntimeException("can't find transaction in block")
    }

    private fun toBlockDTO(block: MinedBlock?, fullTransactionObjects: Boolean): BlockDTO<*>? {
        return if (block != null) {
            if (fullTransactionObjects) {
                blockWithTransactions(block)
            } else {
                blockWithTransactionHashes(block)
            }
        } else null
    }

    private fun blockWithTransactionHashes(block: MinedBlock): BlockDTO<String> {
        return BlockDTO(
            block.block.number.toStringHexPrefix(),
            bytesToString(block.hash),
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            config.coinbase,
            config.difficulty.toStringHexPrefix(),
            config.difficulty.toStringHexPrefix(),
            config.extraData.toStringNoHexPrefix(),
            "0x0",
            block.block.gasLimit.toStringHexPrefix(),
            block.gasUsed.toStringHexPrefix(),
            block.block.timestamp.epochSecond.toStringHexPrefix(),
            block.transactions.map { bytesToString(it.message.hash) },
            emptyList()
        )
    }

    private fun blockWithTransactions(block: MinedBlock): BlockDTO<TransactionDTO> {
        return BlockDTO(
            block.block.number.toStringHexPrefix(),
            bytesToString(block.hash),
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            "0x0",
            config.coinbase,
            config.difficulty.toStringHexPrefix(),
            config.difficulty.toStringHexPrefix(),
            config.extraData.toStringNoHexPrefix(),
            "0x0",
            block.block.gasLimit.toStringHexPrefix(),
            block.gasUsed.toStringHexPrefix(),
            block.block.timestamp.epochSecond.toStringHexPrefix(),
            block.transactions.map { toTransactionDTO(Pair(it, block)) },
            emptyList()
        )
    }
}