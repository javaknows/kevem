package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.gas.GasCost
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger
import java.time.Instant
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.numbers.generateAddressFromSenderAndNonce

class TransactionProcessor(private val executor: Executor, private val coinbase: Address) {

    fun process(
        worldState: WorldState,
        transaction: TransactionMessage,
        timestamp: Instant
    ): Pair<WorldState, TransactionResult> =
        if (isValid(worldState, transaction)) {
            val (newWorldState, recipient)
                    = updateBalancesAndCreateInitialContractIfRequired(worldState, transaction)

            val executionContext = createExecutionContext(newWorldState, timestamp, transaction, recipient)
            val executionResult = executor.executeAll(executionContext)

            if (executionResult.lastCallError == EvmError.None) {
                val finalisedAccounts = applyRefundsAndSuicides(executionResult)

                val accountsWithNewContractCode =
                    updateCreatedContractCodeIfRequired(finalisedAccounts, transaction, executionResult, recipient)

                Pair(
                    newWorldState.copy(accounts = accountsWithNewContractCode),
                    TransactionResult(
                        ResultStatus.COMPLETE,
                        executionResult.logs
                    )
                )
            } else Pair(
                worldState.copy(accounts = deductAllSenderGas(worldState.accounts, transaction)),
                TransactionResult(ResultStatus.FAILED)
            )

        } else Pair(worldState, TransactionResult(ResultStatus.REJECTED))

    private fun updateCreatedContractCodeIfRequired(
        finalisedAccounts: Accounts,
        transaction: TransactionMessage,
        executionResult: ExecutionContext,
        recipient: Address
    ): Accounts =
        if (transaction.to != null) {
            val c = finalisedAccounts.contractAt(recipient) ?: Contract()
            val newContract = c.copy(code = executionResult.lastReturnData)
            finalisedAccounts.updateContract(recipient, newContract)
        } else finalisedAccounts

    private fun applyRefundsAndSuicides(executionResult: ExecutionContext): Accounts =
        removeSuicidedAccounts(
            executionResult.suicidedAccounts,
            applyRefunds(executionResult.refunds, executionResult.accounts)
        )

    private fun updateBalancesAndCreateInitialContractIfRequired(
        worldState: WorldState,
        transaction: TransactionMessage
    ): Pair<WorldState, Address> {
        val newWorldState = worldState.copy(accounts = deductFromSender(worldState.accounts, transaction))

        val (recipient, newWorldState2) =
            if (isContractCreation(transaction)) {
                val contractAddress = createContractAddress(newWorldState, transaction)
                val newAccounts = newWorldState.accounts.updateContract(contractAddress, Contract())
                Pair(contractAddress, newWorldState.copy(accounts = newAccounts))
            } else Pair(transaction.to!!, newWorldState)

        val newWorldState3 =
            newWorldState2.copy(accounts = depositToRecipient(newWorldState2.accounts, recipient, transaction.value))

        return Pair(newWorldState3, recipient)
    }

    private fun depositToRecipient(accounts: Accounts, recipient: Address, value: BigInteger): Accounts {
        val recipientBalance = accounts.balanceOf(recipient)
        val newRecipientBalance = recipientBalance + value

        return accounts.updateBalance(recipient, newRecipientBalance)
    }

    private fun createContractAddress(worldState: WorldState, transaction: TransactionMessage): Address {
        val nonce = worldState.accounts.nonceOf(transaction.from)
        return generateAddressFromSenderAndNonce(transaction.from, nonce)
        // TODO - fail if exists already?
    }

    private fun deductAllSenderGas(accounts: Accounts, transaction: TransactionMessage): Accounts {
        val newBalance = accounts.balanceOf(transaction.from) - transaction.gasLimit
        return accounts.updateBalance(transaction.from, newBalance)
    }

    private fun removeSuicidedAccounts(suicidedAccounts: List<Address>, accounts2: Accounts): Accounts =
        suicidedAccounts.fold(accounts2) { acc, suicideAccount ->
            acc.removeAccount(suicideAccount)
        }

    private fun applyRefunds(refunds: Map<Address, BigInteger>, accounts: Accounts): Accounts =
        refunds.entries.fold(accounts) { acc, entry ->
            val (address, refundAmount) = entry
            val newBalance = acc.balanceOf(address) + refundAmount
            acc.updateBalance(address, newBalance)
        }

    /**
     * update sender balance to deduct call value and intrinsic gas cost
     */
    private fun deductFromSender(accounts: Accounts, transaction: TransactionMessage): Accounts {
        val senderBalance = accounts.balanceOf(transaction.from)
        val newSenderBalance = senderBalance - upFrontCost(transaction)
        return accounts.updateBalance(transaction.from, newSenderBalance)
    }

    private fun isValid(worldState: WorldState, transaction: TransactionMessage): Boolean =
        when {
            transaction.nonce != worldState.accounts.nonceOf(transaction.from) -> false
            instrinsicGas(transaction) > transaction.gasLimit -> false
            upFrontCost(transaction) >= worldState.accounts.balanceOf(transaction.from) -> false
            else -> true
        }


    private fun upFrontCost(transaction: TransactionMessage) =
        transaction.value + (transaction.gasLimit * transaction.gasPrice)

    // gas cost required to consider transaction valid
    private fun instrinsicGas(transaction: TransactionMessage): BigInteger {
        val createCost =
            if (isContractCreation(transaction)) GasCost.TxCreate.costBigInt
            else BigInteger.ZERO

        val dataCost = transaction.data
            .map { if (it == Byte.Zero) GasCost.TxDataZero.costBigInt else GasCost.TxDataNonZero.costBigInt }
            .fold(BigInteger.ZERO) { acc, n -> acc + n }

        return GasCost.Transaction.costBigInt + createCost + dataCost
    }

    private fun createExecutionContext(
        worldState: WorldState,
        timestamp: Instant,
        transactionMessage: TransactionMessage,
        recipient: Address
    ): ExecutionContext {
        val code =
            if (transactionMessage.to != null) {
                worldState.accounts.codeAt(transactionMessage.to)
            } else transactionMessage.data

        val block = worldState.blocks.last().let {
            it.copy(
                number = it.number + BigInteger.ONE,
                timestamp = timestamp
            )
        }

        val transaction = Transaction(transactionMessage.from, transactionMessage.gasPrice)

        val callData =
            if (isContractCreation(transactionMessage)) transactionMessage.data
            else emptyList()

        val callContext = CallContext(
            caller = transactionMessage.from,
            callData = callData,
            type = CallType.INITIAL,
            value = transactionMessage.value,
            code = code,
            gas = transactionMessage.gasLimit,
            contractAddress = recipient,
            storageAddress = recipient
        )

        val previousBlocks = worldState.blocks.takeLast(256)
            .map { Pair(it.number, Word.coerceFrom(it.number)) } // TODO - should be a real block hash
            .toMap()

        return ExecutionContext(
            currentBlock = block,
            currentTransaction = transaction,
            coinBase = coinbase,
            callStack = listOf(callContext),
            accounts = worldState.accounts,
            previousBlocks = previousBlocks
        )
    }

    private fun isContractCreation(transaction: TransactionMessage) = transaction.to == null
}