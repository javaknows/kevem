package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.gas.GasCost
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger
import java.time.Instant
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.numbers.generateAddressFromSenderAndNonce

typealias ProcessResult = Pair<WorldState, TransactionResult>

// TODO - consider block gas limit
// TODO - pass around account instead of wordState
// TODO - define behaviour for two suicides of same contract in same tx
class TransactionProcessor(private val executor: Executor, private val coinbase: Address) {

    fun process(worldState: WorldState, tx: TransactionMessage, timestamp: Instant): ProcessResult =
        if (isValid(worldState, tx)) {
            val ws = incrementSenderNonce(worldState, tx.from)
            processValidTx(ws, tx, timestamp)
        } else
            rejectInvalidTx(worldState)

    private fun incrementSenderNonce(worldState: WorldState, sender: Address) = worldState.apply {
        return copy(accounts = accounts.incrementNonce(sender))
    }

    private fun processValidTx(worldState: WorldState, tx: TransactionMessage, timestamp: Instant): ProcessResult {
        val (newWorldState, recipient) = updateBalancesAndCreateInitialContractIfRequired(worldState, tx)

        val execResult = executor.executeAll(
            createExecutionCtx(newWorldState, timestamp, tx, recipient)
        )

        return if (execResult.lastCallError == EvmError.None) {
            finaliseSuccessfulExecution(tx, execResult, worldState, recipient, newWorldState)
        } else
            consumeGasLimitAndFailResult(worldState, tx)
    }

    private fun rejectInvalidTx(worldState: WorldState) = ProcessResult(
        worldState, TransactionResult(ResultStatus.REJECTED, BigInteger.ZERO)
    )

    private fun finaliseSuccessfulExecution(
        tx: TransactionMessage,
        execResult: ExecutionContext,
        worldState: WorldState,
        recipient: Address,
        newWorldState: WorldState
    ): ProcessResult {
        val contractCreationGas = contractCreationCost(tx, execResult)

        return if (contractCreationGas + execResult.gasUsed > tx.gasLimit)
            consumeGasLimitAndFailResult(worldState, tx)
        else
            finaliseTransaction(execResult, contractCreationGas, tx, recipient, newWorldState)
    }

    private fun finaliseTransaction(
        execResult: ExecutionContext,
        contractCreationGas: BigInteger,
        tx: TransactionMessage,
        recipient: Address,
        newWorldState: WorldState
    ): ProcessResult {
        val finalisedAccounts = applyRefundsAndSuicides(execResult)

        val contractCreationGasCharge = contractCreationGas * tx.gasPrice

        val accountsAfterFinalCharge = deductFromAccount(finalisedAccounts, tx.from, contractCreationGasCharge)

        val accountsWithNewContractCode = updateCodeIfCreated(
            accountsAfterFinalCharge,
            isContractCreation(tx),
            execResult.lastReturnData,
            recipient
        )

        val gasUsed = contractCreationGas + execResult.gasUsed

        return ProcessResult(
            newWorldState.copy(accounts = accountsWithNewContractCode),
            TransactionResult(
                status = ResultStatus.COMPLETE,
                gasUsed = gasUsed,
                logs = execResult.logs,
                created = if (isContractCreation(tx)) recipient else null
            )
        )
    }

    private fun deductFromAccount(accounts: Accounts, address: Address, amount: BigInteger): Accounts =
        accounts.updateBalance(address, accounts.balanceOf(address) - amount)

    private fun depositToAccount(accounts: Accounts, recipient: Address, value: BigInteger): Accounts =
        accounts.updateBalance(recipient, accounts.balanceOf(recipient) + value)

    private fun consumeGasLimitAndFailResult(
        worldState: WorldState,
        transaction: TransactionMessage
    ): Pair<WorldState, TransactionResult> {
        return Pair(
            worldState.copy(accounts = consumeGasLimit(worldState.accounts, transaction)),
            TransactionResult(ResultStatus.FAILED, transaction.gasLimit)
        )
    }

    private fun updateCodeIfCreated(
        accounts: Accounts,
        isCreate: Boolean,
        code: List<Byte>,
        recipient: Address
    ): Accounts =
        if (isCreate) {
            val c = accounts.contractAt(recipient) ?: Contract()
            val newContract = c.copy(code = code)
            accounts.updateContract(recipient, newContract)
        } else
            accounts

    private fun applyRefundsAndSuicides(executionResult: ExecutionContext): Accounts =
        removeSuicidedAccounts(
            executionResult.suicidedAccounts,
            applyRefunds(executionResult.refunds, executionResult.accounts)
        )

    private fun updateBalancesAndCreateInitialContractIfRequired(
        worldState: WorldState,
        transaction: TransactionMessage
    ): Pair<WorldState, Address> {
        val newWorldState = worldState.copy(
            accounts = deductFromAccount(worldState.accounts, transaction.from, upFrontCost(transaction))
        )

        val (recipient, newWorldState2) =
            if (isContractCreation(transaction)) {
                val contractAddress = createContractAddress(newWorldState, transaction)
                val newAccounts = newWorldState.accounts
                    .updateContract(contractAddress, Contract())
                    .updateNonce(contractAddress, BigInteger.ONE)
                Pair(contractAddress, newWorldState.copy(accounts = newAccounts))
            } else Pair(transaction.to!!, newWorldState)

        val newWorldState3 = newWorldState2.copy(
            accounts = depositToAccount(newWorldState2.accounts, recipient, transaction.value)
        )

        return Pair(newWorldState3, recipient)
    }

    private fun createContractAddress(worldState: WorldState, transaction: TransactionMessage): Address {
        val nonce = worldState.accounts.nonceOf(transaction.from)
        return generateAddressFromSenderAndNonce(transaction.from, nonce - BigInteger.ONE)
        // TODO - fail if exists already?
    }

    private fun consumeGasLimit(accounts: Accounts, transaction: TransactionMessage): Accounts {
        val newBalance = accounts.balanceOf(transaction.from) - (transaction.gasLimit * transaction.gasPrice)
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

    private fun isValid(worldState: WorldState, transaction: TransactionMessage): Boolean =
        when {
            transaction.nonce != worldState.accounts.nonceOf(transaction.from) -> false
            intrinsicGas(transaction) > transaction.gasLimit -> false
            upFrontCost(transaction) >= worldState.accounts.balanceOf(transaction.from) -> false
            else -> true
        }

    private fun upFrontCost(transaction: TransactionMessage) =
        transaction.value + (transaction.gasLimit * transaction.gasPrice)

    // gas cost required to consider transaction valid
    private fun intrinsicGas(transaction: TransactionMessage): BigInteger {
        val createCost =
            if (isContractCreation(transaction)) GasCost.TxCreate.costBigInt
            else BigInteger.ZERO

        val dataCost = transaction.data
            .map { if (it == Byte.Zero) GasCost.TxDataZero.costBigInt else GasCost.TxDataNonZero.costBigInt }
            .fold(BigInteger.ZERO) { acc, n -> acc + n }

        return GasCost.Transaction.costBigInt + createCost + dataCost
    }

    private fun contractCreationCost(transaction: TransactionMessage, executionResult: ExecutionContext): BigInteger =
        if (transaction.to == null)
            GasCost.CodeDeposit.costBigInt * executionResult.lastReturnData.size.toBigInteger() * transaction.gasPrice
        else
            BigInteger.ZERO

    private fun createExecutionCtx(
        worldState: WorldState,
        timestamp: Instant,
        tx: TransactionMessage,
        recipient: Address
    ): ExecutionContext {

        val code =
            if (tx.to != null) worldState.accounts.codeAt(tx.to)
            else tx.data

        val block = worldState.blocks.last().let { lastBlock ->
            lastBlock.copy(
                number = lastBlock.number + BigInteger.ONE,
                timestamp = timestamp
            )
        }

        val transaction = Transaction(tx.from, tx.gasPrice)

        val callData =
            if (isContractCreation(tx)) tx.data
            else emptyList()

        val intrinsicGas = intrinsicGas(tx)

        val callContext = CallContext(
            caller = tx.from,
            callData = callData,
            type = CallType.INITIAL,
            value = tx.value,
            code = code,
            gas = tx.gasLimit - intrinsicGas,
            contractAddress = recipient,
            storageAddress = recipient
        )

        val previousBlocks = worldState.blocks
            .takeLast(256)
            .map { Pair(it.number, Word.coerceFrom(it.number)) } // TODO - should be a real block hash
            .toMap()

        return ExecutionContext(
            currentBlock = block,
            currentTransaction = transaction,
            coinBase = coinbase,
            callStack = listOf(callContext),
            accounts = worldState.accounts,
            previousBlocks = previousBlocks,
            gasUsed = intrinsicGas
        )
    }

    private fun isContractCreation(transaction: TransactionMessage) = transaction.to == null
}