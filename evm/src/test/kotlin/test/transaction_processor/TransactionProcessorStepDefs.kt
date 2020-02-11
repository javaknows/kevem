package org.kevm.evm.test.transaction_processor

import org.kevm.evm.*
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.crypto.keccak256
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import org.kevm.evm.util.*
import test.TestObjects
import java.time.ZoneId

/**
 * Step definitions for TransactionProcessor and StatefulTransactionProcessor
 */
class TransactionProcessorStepDefs : En {

    private val executor = Executor(
        GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
            MemoryUsageGasCostCalculator(MemoryUsageGasCalc())
        )
    )

    private var worldState = TestObjects.worldState

    private var transaction = TestObjects.tx

    private var currentBlock = TestObjects.block2

    private var clock: Clock = Clock.fixed(Instant.parse("2006-12-05T15:15:30.00Z"), ZoneId.of("UTC"))

    private var features = Features(emptyList())

    private var worldStateResult: WorldState? = null

    private var transactionResult: TransactionResult? = null

    init {
        Given("a transaction with contents:") { dataTable: DataTable ->
            val row: List<String> = dataTable.asLists().drop(1).first()

            transaction = TransactionMessage(
                Address(row[0]),
                if (row[1].isEmpty()) null else Address(row[1]),
                toBigInteger(row[2]),
                toBigInteger(row[3]),
                toBigInteger(row[4]),
                toCodeList(row[5]),
                toBigInteger(row[6])
            )
        }

        Given("account (0x[a-zA-Z0-9]+) has balance (.*)") { a: String, b: String ->
            val address = Address(a)
            val balance = toBigInteger(b)

            val newAccounts = worldState.accounts.updateBalance(address, balance)
            worldState = worldState.copy(accounts = newAccounts)
        }

        Given("account (0x[a-zA-Z0-9]+) has nonce (.*)") { a: String, n: String ->
            val address = Address(a)
            val nonce = toBigInteger(n)

            val newAccounts = worldState.accounts.updateNonce(address, nonce)
            worldState = worldState.copy(accounts = newAccounts)
        }

        Then("account (0x[a-zA-Z0-9]+) now has nonce (.*)") { a: String, n: String ->
            val address = Address(a)
            val nonce = toBigInteger(n)

            val actual = worldStateResult!!.accounts.nonceOf(address)
            assertThat(actual).isEqualTo(nonce)
        }

        When("the transaction is executed") {
            val tp = TransactionProcessor(executor, features)
            val (ws, tr) = tp.process(worldState, transaction, currentBlock)

            worldStateResult = ws
            transactionResult = tr
        }

        When("the transaction is mined via stateful transaction processor") {
            val tp = TransactionProcessor(executor, features)
            val stp = StatefulTransactionProcessor(tp, clock, worldState)

            val txReceipt = stp.enqueTransaction(transaction)
            stp.mine()

            worldStateResult = stp.getWorldState()
            transactionResult = stp.getTransactionResult(txReceipt.hash)
        }

        Then("the result status is now (.*)") { s: String ->
            val status = ResultStatus.valueOf(s)
            assertThat(transactionResult!!.status).isEqualTo(status)
        }

        Then("account (0x[a-zA-Z0-9]+) now has balance (.*)") { a: String, b: String ->
            val address = Address(a)
            val balance = toBigInteger(b)

            assertThat(worldStateResult!!.accounts.balanceOf(address)).isEqualTo(balance)
        }

        Then("account (0x[a-zA-Z0-9]+) does not exist") { a: String ->
            val address = Address(a)

            assertThat(worldStateResult!!.accounts.accountExists(address)).isEqualTo(false)
        }

        Then("transaction used (.*) gas") { g: String ->
            val gas = toBigInteger(g)

            assertThat(transactionResult!!.gasUsed).isEqualTo(gas)
        }

        Then("a contract with address (.*) was created") { a: String ->
            val address = Address(a)

            assertThat(transactionResult!!.created).isEqualTo(address)
        }

        Then("the code at address (.*) is now (.*)") { a: String, c: String ->
            val address = Address(a)
            val code = toByteList(c)

            val actual = worldStateResult!!.accounts.contractAt(address)!!.code
            assertThat(actual).isEqualTo(code)
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([xA-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeOrDataFromNamesOrHex(byteCodeNames)
            val newAddress = Address(address)
            val newContract = Contract(byteCode)

            worldState = worldState.run {
                copy(accounts = accounts.updateContract(newAddress, newContract))
            }
        }

        Given("the previous block is:") { dataTable: DataTable ->
            val row: List<String> = dataTable.asLists().drop(1).first()

            val block = Block(
                toBigInteger(if (row[0] == "any") "1" else row[0]),
                toBigInteger(if (row[1] == "any") "1" else row[1]),
                toBigInteger(if (row[2] == "any") "1" else row[2]),
                if (row[3].isNullOrBlank()) clock.instant() else Instant.parse(row[3])
            )
            val minedBlock = MinedBlock(block, BigInteger.ZERO, emptyList())

            worldState = worldState.copy(blocks = listOf(minedBlock))
        }

        Then("the mined block now has:") { dataTable: DataTable ->
            val row: List<String> = dataTable.asLists().drop(1).first()

            val number = toBigInteger(row[0])
            val difficulty = toBigInteger(row[1])
            val gasLimit = toBigInteger(row[2])
            val timestamp = Instant.parse(row[3])
            val numTransactions = toBigInteger(row[4])

            val lastMinedBlock = worldStateResult!!.blocks.last()
            val lastBlock = lastMinedBlock.block
            assertThat(number).isEqualTo(lastBlock.number)
            assertThat(difficulty).isEqualTo(lastBlock.difficulty)
            assertThat(gasLimit).isEqualTo(lastBlock.gasLimit)
            assertThat(timestamp).isEqualTo(lastBlock.timestamp)
            assertThat(numTransactions).isEqualTo(lastMinedBlock.transactions.size)
        }

        Given("the current time is (.*)") { time: String ->
            clock = Clock.fixed(Instant.parse(time), ZoneId.of("UTC"))
        }

        Given("EIP ([0-9A-Za-z]+) is enabled") { eipName: String ->
            val eip = EIP.valueOf(eipName)
            features = Features(features.eips + listOf(eip))
        }

        Given("([0-9A-Za-z]+) hard fork features are enabled") { forkName: String ->
            val hardFork = HardFork.valueOf(forkName)
            features = Features(features.eips + hardFork.eips())
        }
    }

    private fun toCodeList(code: String): List<Byte> =
        if (code.startsWith("["))
            byteCodeOrDataFromNamesOrHex(code.replace("[\\[\\]]".toRegex(), ""))
        else
            toByteList(code)

}
