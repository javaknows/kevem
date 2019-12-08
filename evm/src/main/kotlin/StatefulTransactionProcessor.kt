package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.locking.readLock
import com.gammadex.kevin.evm.locking.writeLock
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger
import java.time.Clock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

// TODO - generate block hash properly
class StatefulTransactionProcessor(
    private val transactionProcessor: TransactionProcessor,
    private val clock: Clock,
    private var worldState: WorldState // var - guarded by "lock"
) {
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    fun process(tx: TransactionMessage): TransactionResult =
        writeLock(lock) {
            processAndUpdateWorldState(tx)
        }

    fun getWorldState(): WorldState =
        readLock(lock) {
            worldState
        }

    private fun processAndUpdateWorldState(tx: TransactionMessage): TransactionResult {
        val block = createBlock()
        val (newWorldState, result) = transactionProcessor.process(worldState, tx, block)
        worldState = addMinedTransactionToBlock(block, tx, result, newWorldState)

        return result
    }

    private fun createBlock(): Block =
        worldState.blocks.last().let { lastMinedBlock ->
            val block = lastMinedBlock.block
            block.copy(
                number = block.number + BigInteger.ONE,
                timestamp = clock.instant()
            )
        }

    private fun addMinedTransactionToBlock(
        block: Block,
        tx: TransactionMessage,
        result: TransactionResult,
        worldState: WorldState
    ): WorldState {
        val newBlock = block.copy(transactions = block.transactions + MinedTransaction(tx, result))
        val hash = blockHash(block)
        val newMinedBlock = MinedBlock(newBlock, result.gasUsed, hash.data)
        return worldState.copy(blocks = worldState.blocks + newMinedBlock)
    }

    private fun blockHash(block: Block) = keccak256(Word.coerceFrom(block.number).data)
}
