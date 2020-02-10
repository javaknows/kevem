package org.kevm.evm

import org.kevm.common.Logger
import org.kevm.evm.collections.BigIntegerIndexedList
import org.kevm.evm.gas.TransactionGasCalculator
import org.kevm.evm.gas.TransactionValidator
import org.kevm.evm.model.*
import java.math.BigInteger
import org.kevm.evm.model.Byte
import org.kevm.evm.numbers.generateAddressFromSenderAndNonce

typealias ProcessResult = Pair<WorldState, TransactionResult>

// TODO - consider block gas limit
// TODO - define behaviour for two suicides of same contract in same tx
// TODO - proper transaction hash generation
class TransactionProcessor(
    private val executor: Executor,
    private val txValidator: TransactionValidator = TransactionValidator(),
    private val txGasCalculator: TransactionGasCalculator = TransactionGasCalculator(),
    private val log: Logger = Logger.createLogger(TransactionProcessor::class)
) {

    internal fun process(worldState: WorldState, tx: TransactionMessage, currentBlock: Block): ProcessResult =
        if (txValidator.isValid(worldState, tx)) {
            val ws = incrementSenderNonce(worldState, tx.from)
            processValidTx(ws, tx, currentBlock)
        } else
            rejectInvalidTx(worldState, tx)

    private fun incrementSenderNonce(worldState: WorldState, sender: Address) = worldState.apply {
        return copy(accounts = accounts.incrementNonce(sender))
    }

    private fun processValidTx(worldState: WorldState, tx: TransactionMessage, currentBlock: Block): ProcessResult {
        val (newWorldState, recipient) = updateBalancesAndCreateInitialContractIfRequired(worldState, tx)

        val execResult = executor.executeAll(
            createExecutionCtx(newWorldState, currentBlock, tx, recipient)
        )

        return if (execResult.lastCallError == EvmError.None) {
            finaliseSuccessfulExecution(tx, execResult, worldState, recipient, newWorldState)
        } else
            consumeGasLimitAndFailResult(worldState, tx)
    }

    private fun rejectInvalidTx(
        worldState: WorldState,
        transaction: TransactionMessage
    ) = ProcessResult(
        worldState, TransactionResult(ResultStatus.REJECTED, BigInteger.ZERO)
    )

    private fun finaliseSuccessfulExecution(
        tx: TransactionMessage,
        execResult: ExecutionContext,
        worldState: WorldState,
        recipient: Address,
        newWorldState: WorldState
    ): ProcessResult {
        val contractCreationGas = txGasCalculator.contractCreationCost(tx, execResult)

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
            accounts = deductFromAccount(worldState.accounts, transaction.from, txGasCalculator.upFrontCost(transaction))
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



    private fun createExecutionCtx(
        worldState: WorldState,
        currentBlock: Block,
        tx: TransactionMessage,
        recipient: Address
    ): ExecutionContext {

        val transaction = Transaction(tx.from, tx.gasPrice)

        val (code, callData) =
            if (tx.to != null) Pair(worldState.accounts.codeAt(tx.to), tx.data)
            else Pair(tx.data, emptyList())

        val intrinsicGas = txGasCalculator.intrinsicGas(tx)

        val callContext = CallContext(
            caller = tx.from,
            callData = BigIntegerIndexedList.fromBytes(callData),
            type = CallType.INITIAL,
            value = tx.value,
            code = code,
            gas = tx.gasLimit - intrinsicGas,
            contractAddress = recipient,
            storageAddress = recipient
        )

        val previousBlocks = worldState.blocks
            .takeLast(256)
            .map { Pair(it.block.number, Word.coerceFrom(it.hash)) }
            .toMap()

        return ExecutionContext(
            currentBlock = currentBlock,
            currentTransaction = transaction,
            coinBase = worldState.coinbase,
            callStack = listOf(callContext),
            accounts = worldState.accounts,
            previousBlocks = previousBlocks,
            gasUsed = intrinsicGas
        )
    }

    private fun isContractCreation(transaction: TransactionMessage) = transaction.to == null
}