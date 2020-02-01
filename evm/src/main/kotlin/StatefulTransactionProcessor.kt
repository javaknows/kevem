package org.kevm.evm

import org.kevm.evm.crypto.keccak256
import org.kevm.evm.locking.readLock
import org.kevm.evm.locking.writeLock
import org.kevm.evm.model.*
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

    fun setWorldState(worldState: WorldState) =
        writeLock(lock) {
            this.worldState = worldState
        }

    fun call(tx: TransactionMessage): TransactionResult {
        val (_, result) = transactionProcessor.process(getWorldState(), tx, worldState.blocks.last().block)
        // TODO - should use previous world state

        return result
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
        val hash = blockHash(block)
        val newMinedBlock = MinedBlock(block, result.gasUsed, hash.data, listOf(MinedTransaction(tx, result)))
        return worldState.copy(blocks = worldState.blocks + newMinedBlock)
    }

    private fun blockHash(block: Block) =
        keccak256(Word.coerceFrom(block.number).data)
}
