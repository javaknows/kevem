package com.gammadex.kevin.evm.test.transaction_processor

import com.gammadex.kevin.evm.*
import com.gammadex.kevin.evm.gas.*
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.lang.*
import com.gammadex.kevin.evm.model.Byte
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import com.gammadex.kevin.evm.util.*
import java.time.ZoneId

/**
 * Step definitions for TransactionProcessor and StatefulTransactionProcessor
 */
class TransactionProcessorStepDefs : En {

    private val executor = Executor(
        GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc()),
            MemoryUsageGasCostCalculator(MemoryUsageGasCalc())
        )
    )

    private var coinbase: Address = Address("0xFFEEDD")

    private var worldState: WorldState = WorldState(
        listOf(
            MinedBlock(
                Block(
                    number = BigInteger.ONE,
                    difficulty = BigInteger.ONE,
                    gasLimit = BigInteger("1000000000000000000000000000000"),
                    timestamp = Instant.parse("2006-12-03T10:15:30.00Z")
                ),
                BigInteger.TWO,
                keccak256(Word.coerceFrom(BigInteger.ONE).data).data
            )
        ), Accounts()
    )

    private var transaction: TransactionMessage = TransactionMessage(
        Address("0x1"), Address("0x2"), BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, emptyList(), BigInteger.ZERO
    )

    private var currentBlock = Block(
        number = BigInteger.TWO,
        difficulty = BigInteger.TWO,
        gasLimit = BigInteger("1000000000000000000000000000000"),
        timestamp = Instant.parse("2006-12-04T10:15:30.00Z")
    )

    private var clock: Clock = Clock.fixed(Instant.parse("2006-12-05T15:15:30.00Z"), ZoneId.of("UTC"))

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
            val tp = TransactionProcessor(executor, coinbase)
            val (ws, tr) = tp.process(worldState, transaction, currentBlock)

            worldStateResult = ws
            transactionResult = tr
        }

        When("the transaction is executed via stateful transaction processor") {
            val tp = TransactionProcessor(executor, coinbase)
            val stp = StatefulTransactionProcessor(tp, clock, worldState)

            val tr = stp.process(transaction)

            worldStateResult = stp.getWorldState()
            transactionResult = tr
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
            val numLogs = toBigInteger(row[4])
            val numTransactions = toBigInteger(row[5])

            val lastMinedBlock = worldStateResult!!.blocks.last()
            val lastBlock = lastMinedBlock.block
            assertThat(number).isEqualTo(lastBlock.number)
            assertThat(difficulty).isEqualTo(lastBlock.difficulty)
            assertThat(gasLimit).isEqualTo(lastBlock.gasLimit)
            assertThat(timestamp).isEqualTo(lastBlock.timestamp)
            assertThat(numLogs).isEqualTo(lastBlock.logs.size)
            assertThat(numTransactions).isEqualTo(lastBlock.transactions.size)
        }

        Given("the current time is (.*)") { time: String ->
            clock = Clock.fixed(Instant.parse(time), ZoneId.of("UTC"))
        }
    }

    private fun toCodeList(code: String): List<Byte> =
        if (code.startsWith("["))
            byteCodeOrDataFromNamesOrHex(code.replace("[\\[\\]]".toRegex(), ""))
        else
            toByteList(code)

}
