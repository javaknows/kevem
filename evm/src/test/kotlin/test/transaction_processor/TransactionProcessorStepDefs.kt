package com.gammadex.kevin.evm.test.transaction_processor

import com.gammadex.kevin.evm.Executor
import com.gammadex.kevin.evm.Opcode
import com.gammadex.kevin.evm.TransactionProcessor
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
            Block(
                number = BigInteger.ONE,
                difficulty = BigInteger.ONE,
                gasLimit = BigInteger("1000000000000000000000000000000"),
                timestamp = Instant.parse("2006-12-03T10:15:30.00Z")
            )
        ), Accounts()
    )

    private var transaction: TransactionMessage = TransactionMessage(
        Address("0x1"), Address("0x2"), BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, emptyList(), BigInteger.ZERO
    )

    private var timestamp: Instant = Instant.parse("2007-12-03T10:15:30.00Z")

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
                toByteList(row[5]),
                toBigInteger(row[6])
            )
        }

        Given("account (0x[a-zA-Z0-9]+) has balance (.*)") { a: String, b: String ->
            val address = Address(a)
            val balance = toBigInteger(b)

            val newAccounts = worldState.accounts.updateBalance(address, balance)
            worldState = worldState.copy(accounts = newAccounts)
        }

        When("the transaction is executed") {
            val tp = TransactionProcessor(executor, coinbase)
            val (ws, tr) = tp.process(worldState, transaction, timestamp)

            worldStateResult = ws
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

        Then("transaction used (.*) gas") {g: String ->
            val gas = toBigInteger(g)

            assertThat(transactionResult!!.gasUsed).isEqualTo(gas)
        }
    }
}
