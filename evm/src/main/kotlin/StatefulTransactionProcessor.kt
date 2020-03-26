package org.kevem.evm

import org.kevem.common.KevemException
import org.kevem.evm.crypto.keccak256
import org.kevem.evm.locking.readLock
import org.kevem.evm.locking.writeLock
import org.kevem.evm.model.*
import java.math.BigInteger
import java.time.Clock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import org.kevem.evm.model.Byte

// TODO - generate block hash properly
/**
 * Thread safe - all state guarded by lock
 */
class StatefulTransactionProcessor(
    private val transactionProcessor: TransactionProcessor,
    private var clock: Clock,
    private var worldState: WorldState,
    private var pendingTransactions: List<TransactionMessage> = emptyList(),
    private var previousStates: List<WorldState> = emptyList(),
    private var autoMine: Boolean = true
) {
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    fun process(tx: TransactionMessage): TransactionReceipt = writeLock(lock) {
        val receipt = enqueTransaction(tx)
        if (autoMine) {
            mine()
        }
        receipt
    }

    fun getWorldState(): WorldState = readLock(lock) {
        worldState
    }

    fun setWorldState(worldState: WorldState) = writeLock(lock) {
        this.worldState = worldState
        this.previousStates = emptyList()
    }

    fun setAutoMine(autoMine: Boolean) = writeLock(lock) {
        this.autoMine = autoMine
    }

    fun setClock(clock: Clock) = writeLock(lock) {
        this.clock = clock
    }

    // TODO - should use previous world state
    fun call(tx: TransactionMessage): TransactionResult = readLock(lock) {
        val (_, result) = transactionProcessor.process(getWorldState(), tx, worldState.blocks.last().block)
        result
    }

    fun enqueTransaction(tx: TransactionMessage): TransactionReceipt {
        pendingTransactions += tx

        return TransactionReceipt(tx.hash)
    }

    fun mine(): Unit = writeLock(lock) {
        val (newMinedBlock, newWorldState) = processTransactions(pendingTransactions, worldState)

        previousStates += worldState
        this.worldState = newWorldState.copy(blocks = newWorldState.blocks + newMinedBlock)
    }

    private fun processTransactions(
        transactions: List<TransactionMessage>,
        worldState: WorldState
    ): Pair<MinedBlock, WorldState> = transactions.fold(Pair(createBlock(), worldState)) { acc, tx ->
        val (b, ws) = acc
        val (newWorldState, txResult) = transactionProcessor.process(ws, tx, b.block)

        val newMinedBlock =
            if (txResult.status == ResultStatus.COMPLETE)
                b.copy(
                    gasUsed = b.gasUsed + txResult.gasUsed,
                    transactions = b.transactions + MinedTransaction(tx, txResult)
                )
            else b

        Pair(newMinedBlock, newWorldState)
    }

    fun getTransactionResult(txHash: List<Byte>): TransactionResult = readLock(lock) {
        worldState.blocks
            .flatMap { it.transactions }
            .find { it.message.hash == txHash }
            ?.let { it: MinedTransaction -> it.result } ?: throw KevemException("unknown transaction")
    }

    fun revertToBlock(number: BigInteger) = writeLock(lock) {
        val worldState = previousStates.find { it.blocks.lastOrNull()?.block?.number == number }

        if (worldState != null) {
            this.worldState = worldState
            this.previousStates = previousStates.takeWhile {
                val maxBxBlock = it.blocks.lastOrNull()?.block?.number

                (maxBxBlock == null) || maxBxBlock < number
            }
        }
    }

    private fun createBlock(): MinedBlock {
        val currBlock = worldState.blocks.last().block

        val nextBlock = currBlock.copy(
            number = currBlock.number + BigInteger.ONE,
            timestamp = clock.instant()
        )

        return MinedBlock(nextBlock, BigInteger.ZERO, blockHash(nextBlock.number), emptyList())
    }

    private fun blockHash(num: BigInteger) =
        keccak256(Word.coerceFrom(num).data)
}
