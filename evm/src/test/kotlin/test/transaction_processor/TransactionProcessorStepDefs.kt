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
    }

    private fun toCodeList(code: String): List<Byte> =
        if (code.startsWith("["))
            byteCodeOrDataFromNamesOrHex(code.replace("[\\[\\]]".toRegex(), ""))
        else
            toByteList(code)

}
