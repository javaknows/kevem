package com.gammadex.kevin

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import com.gammadex.kevin.util.*

class StepDefs : En {

    private var executionContext: ExecutionContext = createBaseExecutionContext()

    private var executor = Executor()

    private var result: ExecutionContext? = null

    init {
        Given("(0x[a-zA-Z0-9]+) is pushed onto the stack") { stack: String ->
            updateLastCallContext {
                val newStack = it.stack.push(toByteList(stack))
                it.copy(stack = newStack)
            }
        }

        When("the context is executed") {
            executeContext()
        }

        When("opcode ([A-Z0-9]+) is executed") { opcode: String ->
            updateLastCallContext {
                val newContract = it.contract.copy(code = listOf(Opcode.valueOf(opcode).code))
                it.copy(contract = newContract)
            }

            executeContext()
        }

        Then("the stack contains (0x[a-zA-Z0-9]+)") { stack: String ->
            checkResult { result ->
                val element = result.stack.pop().first
                val expected = Word.coerceFrom(stack).data.dropWhile { it.value == 0 }

                assertThat(element.dropWhile { it.value == 0 }).isEqualTo(expected)
            }
        }

        Then("the stack is empty") {
            checkResult {
                assertThat(it.stack.size()).isEqualTo(0)
            }
        }

        Given("the contract address is (0x[a-zA-Z0-9]+)") { address: String ->
            updateLastCallContext {
                val newContract = it.contract.copy(address = Address(address))
                it.copy(contract = newContract)
            }
        }

        Given(".* account with address (0x[a-zA-Z0-9]+) has balance (0x[a-zA-Z0-9]+)") { address: String, balance: String ->
            val value = toBigInteger(balance)

            updateExecutionContext {
                val evmState = it.evmState.updateBalance(Address(address), value)
                it.copy(evmState = evmState)
            }
        }

        Given("transaction origin is (0x[a-zA-Z0-9]+)") { address: String ->
            updateExecutionContext {
                val currentTransaction = it.currentTransaction.copy(origin = Address(address))
                it.copy(currentTransaction = currentTransaction)
            }
        }

        Given("the current caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            updateLastCallContext {
                it.copy(caller = Address(address))
            }
        }

        Given("the current call type is ([A-Z]+)") { callType: CallType ->
            updateLastCallContext {
                it.copy(type = callType)
            }
        }

        Given("the current call type is any of") { dataTable: DataTable ->
            val originalContext = executionContext

            dataTable.asLists().forEach {
                executionContext = originalContext

                val callType = CallType.valueOf(it[0])

                updateLastCallContext {
                    it.copy(type = callType)
                }
            }
        }

        Given("the current call value is (0x[a-zA-Z0-9]+)") { value: String ->
            updateLastCallContext {
                it.copy(value = BigInteger(value.replaceFirst("0x", ""), 16))
            }
        }

        Given("the previous caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            updateExecutionContext { executionContext ->
                val callStack =
                    if (executionContext.callStack.size > 1) executionContext.callStack
                    else listOf(executionContext.callStack.last()) + executionContext.callStack

                val newCallContext = callStack.first().copy(caller = Address(address))
                val newCallStackList = listOf(newCallContext) + executionContext.callStack.drop(1)

                executionContext.copy(callStack = newCallStackList)
            }
        }

        Given("the previous call type is ([A-Z]+)") { callType: CallType ->
            setPreviousCallType(callType)
        }

        Given("the previous call type is any of") { dataTable: DataTable ->
            val callType = CallType.valueOf(dataTable.asList()[0])

            setPreviousCallType(callType)
        }

        Given("call data is (empty|0x[a-zA-Z0-9]+)") { value: String ->
            val callData = toByteList(value.replace("empty", "0x"))

            updateLastCallContext { callContext ->
                callContext.copy(callData = callData)
            }
        }

        Given("(\\d+) bytes? of memory from position (\\d+) is (empty|0x[a-zA-Z0-9]+)") { length: Int, start: Int, bytes: String ->
            val expected =
                if (bytes == "empty") Byte.Zero.repeat(length)
                else toByteList(bytes)

            checkResult {
                val actual = it.memory.get(start, length)
                assertThat(actual).isEqualTo(expected)
            }
        }

        Given("contract code is \\[([A-Z0-9, ]+)\\]") { byteCodeNames: String ->
            val byteCode = byteCodeFromNames(byteCodeNames)

            updateLastCallContext { callContext ->
                val newContract = callContext.contract.copy(code = byteCode)
                callContext.copy(contract = newContract)
            }
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([A-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeFromNames(byteCodeNames)
            val newAddress = Address(address)
            val newContract = Contract(byteCode, newAddress)

            updateExecutionContext {
                val evmState = it.evmState.updateContract(newAddress, newContract)
                it.copy(evmState = evmState)
            }
        }

        Given("return data is (0x[a-zA-Z0-9]+)") { data: String ->
            updateExecutionContext {
                it.copy(lastReturnData = toByteList(data))
            }
        }

        Given("recent block ([0-9]+) has hash (0x[a-zA-Z0-9]+)") { block: String, hash: String ->
            updateExecutionContext {
                val newBlocks = it.previousBlocks + Pair(BigInteger(block), Word.coerceFrom(hash))
                it.copy(previousBlocks = newBlocks)
            }
        }

        Given("coinbase is (0x[a-zA-Z0-9]+)") { address: String ->
            updateExecutionContext {
                it.copy(coinBase = Address(address))
            }
        }

        Given("time is \"(.*)\"") { date: String ->
            val clock = Clock.fixed(Instant.parse(date), ZoneId.systemDefault())

            updateExecutionContext {
                it.copy(clock = clock)
            }
        }

        Then("the stack contains a timestamp of \"(.*)\"") { date: String ->
            val expected = Instant.parse(date)

            checkResult {
                assertThat(it.clock.instant()).isEqualTo(expected)
            }
        }

        Given("current block number is (.*)") { block: String ->
            updateCurrentBlock {
                it.copy(number = toBigInteger(block))
            }
        }

        Given("current block difficulty is (.*)") { difficulty: String ->
            updateCurrentBlock {
                it.copy(difficulty = toBigInteger(difficulty))
            }
        }

        Given("current block gas limit is (.*)") { gasLimit: String ->
            updateCurrentBlock {
                it.copy(gasLimit = toBigInteger(gasLimit))
            }
        }

        Given("(0x[a-zA-Z0-9]+) is stored in memory at location (0x[a-zA-Z0-9]+)") { data: String, location: String ->
            updateLastCallContext {
                val newMemory = it.memory.set(toInt(location), toByteList(data))
                it.copy(memory = newMemory)
            }
        }

        Given("(0x[a-zA-Z0-9]+) is in storage at location (0x[a-zA-Z0-9]+)") { data: String, location: String ->
            updateLastCallContext {
                val newStorage = it.storage.set(toInt(location), Word.coerceFrom(data))
                it.copy(storage = newStorage)
            }
        }

        Then("data in storage at location (\\d+) is (0x[a-zA-Z0-9]+)") { location: Int, data: String ->
            checkResult {
                assertThat(it.storage[location]).isEqualTo(Word.coerceFrom(data))
            }
        }

        Then("the position in code is (\\d+)") { position: Int ->
            checkResult {
                assertThat(it.currentCallContext.currentLocation).isEqualTo(position)
            }
        }

        Given("contract position is (\\d+)") { position: Int ->
            updateLastCallContext {
                it.copy(currentLocation = position)
            }
        }

        Given("there is (\\d+) gas remaining") { gas: Int ->
            updateLastCallContext {
                it.copy(gasRemaining = BigInteger.valueOf(gas.toLong()))
            }
        }

        Given("contract code ends with (0x[a-zA-Z0-9]+)") { data: String ->
            updateLastCallContext {
                val newContract = it.contract.copy(code = toByteList(data))
                it.copy(contract = newContract)
            }
        }

        Given("the push opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = toByteList(it[1])

                updateLastCallContext { ctx ->
                    val newContractCode = listOf(opcode!!.code) + ctx.contract.code
                    val newContract = ctx.contract.copy(code = newContractCode)
                    ctx.copy(contract = newContract)
                }

                executeContext()

                checkResult { result ->
                    val element = result.stack.pop().first
                    assertThat(element).isEqualTo(expected)
                }
            }
        }

        Given("the DUP opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = toByteList(it[1])

                updateLastCallContext { ctx ->
                    val newContract = ctx.contract.copy(code = listOf(opcode!!.code))
                    ctx.copy(contract = newContract)
                }

                executeContext()

                checkResult { result ->
                    val element = result.stack.pop().first
                    assertThat(element).isEqualTo(expected)
                }
            }
        }

        Given("the SWAP opcode is executed it will have data on top of stack and 0xAA at index") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = Word.coerceFrom(it[1])
                val indexOfAA = toInt(it[2])

                updateLastCallContext { ctx ->
                    val newContract = ctx.contract.copy(code = listOf(opcode!!.code))
                    ctx.copy(contract = newContract)
                }

                executeContext()

                checkResult { result ->
                    val element = result.stack.peekWord()

                    assertThat(element).isEqualTo(expected)
                    assertThat(result.stack.peekWord(indexOfAA)).isEqualTo(Word.coerceFrom("0xAA"))
                }
            }
        }

        Then("a log has been generated with data (0x[a-zA-Z0-9]+)") { data: String ->
            checkResult {
                assertThat(it.logs).hasSize(1)
                assertThat(it.logs[0].data).isEqualTo(toByteList(data))
            }
        }

        Then("the log has no topics") {
            checkResult {
                assertThat(it.logs[0].topics).isEmpty()
            }
        }

        Then("the log has topic data") { dataTable: DataTable ->
            checkResult { result ->
                val expectedTopics = dataTable.asList().map { Word.coerceFrom(it) }
                assertThat(result.logs[0].topics).isEqualTo(expectedTopics)
            }
        }
    }

    private fun updateExecutionContext(updateFunc: (ExecutionContext) -> ExecutionContext) {
        executionContext = updateFunc(executionContext)
    }

    private fun checkResult(checker: (ExecutionContext) -> Unit) = checker(result!!)

    private fun processRows(dataTable: DataTable, processRow: (List<String>) -> Unit) {
        val originalContext = executionContext

        dataTable.asLists().forEach {
            executionContext = originalContext

            processRow(it)
        }
    }

    private fun executeContext() {
        result = executor.execute(executionContext, executionContext)
    }

    private fun updateCurrentBlock(updateBlock: (ctx: Block) -> Block) {
        updateExecutionContext {
            it.copy(currentBlock = updateBlock(it.currentBlock))
        }
    }

    private fun updateLastCallContext(updateContext: (ctx: CallContext) -> CallContext) {
        updateExecutionContext { executionContext ->
            val lastCallContext: CallContext = executionContext.callStack.last()
            val newCallContext = updateContext(lastCallContext)
            val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

            executionContext.copy(callStack = newCallStackList)
        }
    }

    private fun setPreviousCallType(callType: CallType) {
        updateExecutionContext { executionContext ->
            val callStack =
                if (executionContext.callStack.size > 1) executionContext.callStack
                else listOf(executionContext.callStack.last()) + executionContext.callStack

            val newCallContext = callStack.first().copy(type = callType)
            val newCallStackList = listOf(newCallContext) + executionContext.callStack.drop(1)

            executionContext.copy(callStack = newCallStackList)
        }
    }

    private fun createBaseExecutionContext(): ExecutionContext = ExecutionContext(
        currentBlock = Block(
            number = BigInteger.ONE,
            difficulty = BigInteger.TEN,
            gasLimit = BigInteger("100")
        ),
        currentTransaction = Transaction(
            origin = Address("0xFFEEDD"),
            gasPrice = BigInteger.ONE
        ),
        coinBase = Address("0xFFEEDD"),
        logs = emptyList(),
        completed = false,
        clock = Clock.systemUTC(),
        callStack = listOf(
            CallContext(
                caller = Address("0x0"),
                callData = emptyList(),
                contract = Contract(listOf(Opcode.INVALID.code), Address("0x0")),
                type = CallType.INITIAL,
                value = BigInteger.ZERO,
                valueRemaining = BigInteger.ZERO,
                stack = Stack(),
                memory = Memory(),
                storage = Storage()
            )
        )
    )
}
