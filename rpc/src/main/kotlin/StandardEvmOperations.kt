package com.gammadex.kevin.rpc

import com.gammadex.kevin.evm.StatefulTransactionProcessor
import com.gammadex.kevin.evm.bytesToString
import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.toByteList
import org.web3j.crypto.SignedRawTransaction
import org.web3j.crypto.TransactionDecoder
import java.math.BigInteger

sealed class BlockReference {
    companion object {
        fun fromString(blockValue: String?) =
            when (blockValue?.toLowerCase()) {
                null, "latest" -> LatestBlock
                "pending" -> PendingBlock
                "earliest" -> EarliestBlock
                else -> NumericBlock(
                    toBigInteger(
                        blockValue
                    )
                )
            }
    }
}

data class NumericBlock(val number: BigInteger) : BlockReference()
object LatestBlock : BlockReference()
object PendingBlock : BlockReference()
object EarliestBlock : BlockReference()

// TODO - move these conversion functions to common location

fun toBigInteger(number: String) =
    if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

fun toBigIntegerNullZero(number: String?) =
    if (number == null) BigInteger.ZERO
    else if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

fun toBigIntegerOrNull(number: String?) =
    if (number == null) null
    else if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

fun toAddress(a: String?) = Address(checkNotNull(a) { "address field is null" })


fun isEmptyHex(to: String?): Boolean = to == null || to == "0x" || to == ""

class StandardEvmOperations(val evm: StatefulTransactionProcessor) {

    fun getTransactionCount(address: Address, block: BlockReference): BigInteger = evm.getWorldState().let { ws ->
        return getTransactions(address, block, ws).size.toBigInteger()
    }

    // TODO - remove web3 dependency for TX decoding (RLP)
    fun sendRawTransaction(signedTxData: List<Byte>): List<Byte> {
        val tran = TransactionDecoder.decode(bytesToString(signedTxData)) as SignedRawTransaction

        val from =
            if (tran.from != null) Address(tran.from)
            else throw RuntimeException("can't determine transaction sender")

        val to = if (isEmptyHex(tran.to)) null else Address(tran.to)
        val value = tran.value ?: BigInteger.ZERO

        val hash = keccak256(signedTxData)
        val tx = TransactionMessage(
            from,
            to,
            value,
            tran.gasPrice,
            tran.gasLimit,
            toByteList(tran.data),
            tran.nonce,
            hash.data
        )

        return sendTransaction(tx)
    }

    fun sendTransaction(tx: TransactionMessage): List<Byte> {
        evm.process(tx)
        return tx.hash
    }

    fun getTransactionReceipt(txHash: List<Byte>) = getTxAndBlockByTxHash(txHash)

    private fun getTransactions(address: Address, block: BlockReference, ws: WorldState): List<MinedTransaction> =
        when (block) {
            is LatestBlock -> ws.blocks
                .flatMap { it.transactions }
                .filter { it.message.from == address }
            is EarliestBlock -> ws.blocks
                .first().transactions
                .filter { it.message.from == address }
            is NumericBlock -> ws.blocks
                .filter { it.block.number <= block.number }
                .flatMap { it.transactions }
                .filter { it.message.from == address }
            is PendingBlock -> ws.blocks
                .flatMap { it.transactions }
                .filter { it.message.from == address }
        }

    fun coinbase(): Address = evm.getWorldState().let { ws ->
        return ws.coinbase
    }

    fun blockNumber(): BigInteger = evm.getWorldState().blocks.last().block.number

    fun getBalance(address: Address, block: BlockReference?): BigInteger = evm.getWorldState().let { ws ->
        return when (block) {
            is LatestBlock -> ws.accounts.balanceOf(address)
            is PendingBlock -> ws.accounts.balanceOf(address)
            else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
        }
    }

    fun getStorageAt(address: Address, location: BigInteger, block: BlockReference): Word =
        evm.getWorldState().let { ws ->
            return when (block) {
                is LatestBlock -> ws.accounts.storageAt(address, location)
                is PendingBlock -> ws.accounts.storageAt(address, location)
                else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
            }
        }

    fun getBlockTransactionCountByHash(hash: List<Byte>): Int =
        evm.getWorldState().blocks.find { it.hash == hash }?.transactions?.size ?: 0

    fun getBlockTransactionCountByNumber(block: BlockReference): Int = evm.getWorldState().let { ws ->
        return when (block) {
            is LatestBlock -> ws.blocks.last().transactions.size
            is EarliestBlock -> ws.blocks.first().transactions.size
            is NumericBlock -> ws.blocks.find { it.block.number == block.number }?.transactions?.size ?: 0
            is PendingBlock -> 0
        }
    }

    fun getUncleCountByBlockHash(hash: List<Byte>): BigInteger = BigInteger.ZERO

    fun getUncleCountByBlockNumber(block: BlockReference): BigInteger = BigInteger.ZERO

    fun getCode(address: Address, block: BlockReference): List<Byte> =
        evm.getWorldState().let { ws ->
            return when (block) {
                is LatestBlock -> ws.accounts.contractAt(address)?.code ?: emptyList()
                is PendingBlock -> ws.accounts.contractAt(address)?.code ?: emptyList()
                else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
            }
        }

    fun getBlockByHash(hash: List<Byte>): MinedBlock? =
        evm.getWorldState().blocks.find { it.hash == hash }

    fun getBlockByNumber(block: BlockReference): MinedBlock? =
        evm.getWorldState().let { ws ->
            return when (block) {
                is LatestBlock -> ws.blocks.last()
                is NumericBlock -> ws.blocks.find { it.block.number == block.number }
                is EarliestBlock -> ws.blocks.first()
                else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
            }
        }

    fun getTransactionByHash(txHash: List<Byte>) = getTxAndBlockByTxHash(txHash)

    private fun getTxAndBlockByTxHash(txHash: List<Byte>): Pair<MinedTransaction, MinedBlock>? =
        evm.getWorldState().let { ws ->
            val block = ws.blocks.find { b ->
                b.transactions.any { it.message.hash == txHash }
            }

            if (block == null) return null
            else Pair(block.transactions.find { it.message.hash == txHash }!!, block)
        }

    fun getTransactionByBlockHashAndIndex(
        blockHash: List<Byte>,
        txIndex: BigInteger
    ): Pair<MinedTransaction, MinedBlock>? = evm.getWorldState().let { ws ->
        val block = ws.blocks.find { it.hash == blockHash }

        return getPairOfBlockAndTxByIndex(block, txIndex)
    }

    fun getTransactionByBlockNumberAndIndex(
        block: BlockReference,
        txIndex: BigInteger
    ): Pair<MinedTransaction, MinedBlock>? = evm.getWorldState().let { ws ->
        return when (block) {
            is LatestBlock -> getPairOfBlockAndTxByIndex(ws.blocks.last(), txIndex)
            is NumericBlock -> getPairOfBlockAndTxByIndex(ws.blocks.find { it.block.number == block.number }, txIndex)
            is EarliestBlock -> getPairOfBlockAndTxByIndex(ws.blocks.first(), txIndex)
            else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
        }
    }

    fun getNonce(address: Address): BigInteger = evm.getWorldState().accounts.nonceOf(address)

    fun pendingBlockGasLimit(): BigInteger = evm.getWorldState().blocks.last().block.gasLimit

    fun estimateGas(tx: TransactionMessage, block: BlockReference): BigInteger = evm.getWorldState().let { ws ->
        return when (block) {
            is LatestBlock -> evm.call(tx).gasUsed
            is PendingBlock -> evm.call(tx).gasUsed
            else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
        }
    }

    fun call(tx: TransactionMessage, block: BlockReference): List<Byte> = evm.getWorldState().let { ws ->
        return when (block) {
            is LatestBlock -> evm.call(tx).returnData
            is PendingBlock -> evm.call(tx).returnData
            else -> throw RuntimeException("only latest block is supported") // TODO - support historical blocks
        }
    }

    fun getLogs(
        from: BlockReference? = null,
        to: BlockReference? = null,
        address: List<Address>? = null,
        topics: List<Word>? = null,
        blockHash: List<Byte>? = null
    ) = evm.getWorldState().let { ws ->
        val fromBlock = getBlockNumber(from, ws) ?: ws.blocks.first().block.number
        val toBlock = getBlockNumber(to, ws) ?: ws.blocks.last().block.number

        val blocks = ws.blocks
            .filter { it.block.number >= fromBlock }
            .filter { it.block.number <= toBlock }
            .filter { blockHash == null || blockHash == it.hash }

        blocks.flatMap { it.transactions }
            .flatMap { it.result.logs }
            .filter { address == null || address != null } // TODO - include source address in Log and filter here
            .filter { topics == null || it.topics.any { t -> topics.contains(t) } }
    }

    private fun getBlockNumber(block: BlockReference?, ws: WorldState): BigInteger? {
        return when (block) {
            is LatestBlock -> ws.blocks.last().block.number
            is NumericBlock -> block.number
            is EarliestBlock -> ws.blocks.first().block.number
            is PendingBlock -> ws.blocks.last().block.number
            null -> null
        }
    }

    private fun getPairOfBlockAndTxByIndex(
        block: MinedBlock?,
        txIndex: BigInteger
    ): Pair<MinedTransaction, MinedBlock>? =
        if (block == null) null
        else {
            val tx = block.transactions.getOrNull(txIndex.toInt()) // TODO - should be indexed by BigInteger
            if (tx == null) null
            else Pair(tx, block)
        }

}
