package com.gammadex.kevin

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class StepDefs : En {

    var executionContext: ExecutionContext = createBaseExecutionContext()

    var executor = Executor()

    var result: ExecutionContext? = null

    init {
        Given("(0x[a-zA-Z0-9]+) is pushed onto the stack") { stack: String ->
            replaceLastCallContext { callContext ->
                val newStack = callContext.stack.push(toByteList(stack))
                callContext.copy(stack = newStack)
            }
        }

        When("opcode ([A-Z0-9]+) is executed") { opcode: String ->
            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(code = listOf(Opcode.valueOf(opcode).code))
                callContext.copy(contract = newContract)
            }

            result = executor.execute(executionContext, executionContext)
        }

        When("the context is executed") {
            result = executor.execute(executionContext, executionContext)
        }

        Then("the stack contains (0x[a-zA-Z0-9]+)") { stack: String ->
            val element = result!!.stack.pop().first
            val expected = Word.coerceFrom(stack).data.dropWhile { it.value == 0 }

            assertThat(element.dropWhile { it.value == 0 }).isEqualTo(expected)
        }

        Then("the stack is empty") {
            assertThat(result!!.stack.size()).isEqualTo(0)
        }

        Given("the contract address is (0x[a-zA-Z0-9]+)") { address: String ->
            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(address = Address(address))
                callContext.copy(contract = newContract)
            }
        }

        Given("an account with address (0x[a-zA-Z0-9]+) has balance (0x[a-zA-Z0-9]+)") { address: String, balance: String ->
            val value = toBigInteger(balance)
            val evmState = executionContext.evmState.updateBalance(Address(address), value)

            executionContext = executionContext.copy(evmState = evmState)
        }

        Given("transaction origin is (0x[a-zA-Z0-9]+)") { address: String ->
            val currentTransaction = executionContext.currentTransaction.copy(origin = Address(address))
            executionContext = executionContext.copy(currentTransaction = currentTransaction)
        }

        Given("the current caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            replaceLastCallContext {
                it.copy(caller = Address(address))
            }
        }

        Given("the current call type is ([A-Z]+)") { callType: CallType ->
            replaceLastCallContext {
                it.copy(type = callType)
            }
        }

        Given("the current call type is any of") { dataTable: DataTable ->
            val originalContext = executionContext

            dataTable.asLists().forEach {
                executionContext = originalContext

                val callType = CallType.valueOf(it[0])

                replaceLastCallContext {
                    it.copy(type = callType)
                }
            }
        }

        Given("the current call value is (0x[a-zA-Z0-9]+)") { value: String ->
            replaceLastCallContext {
                it.copy(value = BigInteger(value.replaceFirst("0x", ""), 16))
            }
        }

        Given("the previous caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            val callStack =
                if (executionContext.callStack.size > 1) executionContext.callStack
                else listOf(executionContext.callStack.last()) + executionContext.callStack

            val newCallContext = callStack.first().copy(caller = Address(address))
            val newCallStackList = listOf(newCallContext) + executionContext.callStack.drop(1)

            executionContext = executionContext.copy(callStack = newCallStackList)
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

            replaceLastCallContext { callContext ->
                callContext.copy(callData = callData)
            }
        }

        Given("(\\d+) bytes? of memory from position (\\d+) is (empty|0x[a-zA-Z0-9]+)") { length: Int, start: Int, bytes: String ->
            val expected =
                if (bytes == "empty") Byte.Zero.repeat(length)
                else toByteList(bytes)

            val actual = result!!.memory.get(start, length)

            assertThat(actual).isEqualTo(expected)
        }

        Given("contract code is \\[([A-Z0-9, ]+)\\]") { byteCodeNames: String ->
            val byteCode = byteCodeFromNames(byteCodeNames)

            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(code = byteCode)
                callContext.copy(contract = newContract)
            }
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([A-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeFromNames(byteCodeNames)

            val newContract = Contract(byteCode, Address(address))
            val evmState = executionContext.evmState.updateContract(Address(address), newContract)

            executionContext = executionContext.copy(evmState = evmState)
        }

        Given("return data is (0x[a-zA-Z0-9]+)") { data: String ->
            executionContext = executionContext.copy(lastReturnData = toByteList(data))
        }

        Given("recent block ([0-9]+) has hash (0x[a-zA-Z0-9]+)") { block: String, hash: String ->
            val newBlocks = executionContext.previousBlocks + Pair(BigInteger(block), Word.coerceFrom(hash))
            executionContext = executionContext.copy(previousBlocks = newBlocks)
        }

        Given("coinbase is (0x[a-zA-Z0-9]+)") { address: String ->
            executionContext = executionContext.copy(coinBase = Address(address))
        }


        Given("time is \"(.*)\"") { date: String ->
            val clock = Clock.fixed(Instant.parse(date), ZoneId.systemDefault())
            executionContext = executionContext.copy(clock = clock)
        }

        Then("the stack contains a timestamp of \"(.*)\"") { date: String ->
            val expected = Instant.parse(date)
            assertThat(result!!.clock.instant()).isEqualTo(expected)
        }

        Given("current block number is (.*)") { block: String ->
            replaceCurrentBlock {
                it.copy(number = toBigInteger(block))
            }
        }

        Given("current block difficulty is (.*)") { difficulty: String ->
            replaceCurrentBlock {
                it.copy(difficulty = toBigInteger(difficulty))
            }
        }

        Given("current block gas limit is (.*)") { gasLimit: String ->
            replaceCurrentBlock {
                it.copy(gasLimit = toBigInteger(gasLimit))
            }
        }

        Given("(0x[a-zA-Z0-9]+) is stored in memory at location (0x[a-zA-Z0-9]+)") { data: String, location: String ->
            replaceLastCallContext { callContext ->
                val newMemory = executionContext.memory.set(toInt(location), toByteList(data))
                callContext.copy(memory = newMemory)
            }
        }

        Given("(0x[a-zA-Z0-9]+) is in storage at location (0x[a-zA-Z0-9]+)") { data: String, location: String ->
            replaceLastCallContext { callContext ->
                val newStorage = callContext.storage.set(toInt(location), Word.coerceFrom(data))
                callContext.copy(storage = newStorage)
            }
        }

        Then("data in storage at location (\\d+) is (0x[a-zA-Z0-9]+)") { location: Int, data: String ->
            assertThat(result!!.storage.get(location)).isEqualTo(Word.coerceFrom(data))
        }

        Then("the position in code is (\\d+)") { position: Int ->
            assertThat(result!!.currentCallContext.currentLocation).isEqualTo(position)
        }

        Given("contract position is (\\d+)") { position: Int ->
            replaceLastCallContext {
                it.copy(currentLocation = position)
            }
        }

        Given("there is (\\d+) gas remaining") { gas: Integer ->
            replaceLastCallContext {
                it.copy(gasRemaining = BigInteger.valueOf(gas.toLong()))
            }
        }

        Given("contract code ends with (0x[a-zA-Z0-9]+)") { data: String ->
            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(code = toByteList(data))
                callContext.copy(contract = newContract)
            }
        }

        Given("the push opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = toByteList(it[1])

                replaceLastCallContext { callContext ->
                    val newContractCode = listOf(opcode!!.code) + callContext.contract.code
                    val newContract = callContext.contract.copy(code = newContractCode)
                    callContext.copy(contract = newContract)
                }

                result = executor.execute(executionContext, executionContext)
                val element = result!!.stack.pop().first

                assertThat(element).isEqualTo(expected)
            }
        }

        Given("the DUP opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = toByteList(it[1])

                replaceLastCallContext { callContext ->
                    val newContract = callContext.contract.copy(code = listOf(opcode!!.code))
                    callContext.copy(contract = newContract)
                }

                result = executor.execute(executionContext, executionContext)

                val element = result!!.stack.pop().first
                assertThat(element).isEqualTo(expected)
            }
        }

        Given("the SWAP opcode is executed it will have data on top of stack and 0xAA at index") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromString(it[0])
                val expected = Word.coerceFrom(it[1])
                val indexOfAA = toInt(it[2])

                replaceLastCallContext { callContext ->
                    val newContract = callContext.contract.copy(code = listOf(opcode!!.code))
                    callContext.copy(contract = newContract)
                }

                result = executor.execute(executionContext, executionContext)

                val element = result!!.stack.peekWord()
                assertThat(element).isEqualTo(expected)

                assertThat(result!!.stack.peekWord(indexOfAA)).isEqualTo(Word.coerceFrom("0xAA"))
            }
        }

        Then("a log has been generated with data (0x[a-zA-Z0-9]+)") { data: String ->
            val logs = result!!.logs

            assertThat(logs).hasSize(1)
            assertThat(logs[0].data).isEqualTo(toByteList(data))
        }

        Then("the log has no topics") {
            val log = result!!.logs[0]
            assertThat(log.topics).isEmpty()
        }

        Then("the log has topic data") { dataTable: DataTable ->
            val log = result!!.logs[0]

            val expectedTopics = dataTable.asList().map { Word.coerceFrom(it) }
            assertThat(log.topics).isEqualTo(expectedTopics)
        }
    }

    private fun processRows(dataTable: DataTable, processRow: (List<String>) -> Unit) {
        val originalContext = executionContext

        dataTable.asLists().forEach {
            executionContext = originalContext

            processRow(it)
        }
    }

    private fun replaceCurrentBlock(updateBlock: (ctx: Block) -> Block) {
        executionContext = executionContext.copy(currentBlock = updateBlock(executionContext.currentBlock))
    }

    private fun toInt(location: String) = toBigInteger(location).toInt()

    private fun toBigInteger(balance: String) =
        if (balance.startsWith("0x")) BigInteger(balance.replaceFirst("0x", ""), 16)
        else BigInteger(balance)

    private fun byteCodeFromNames(byteCodeNames: String): List<Byte> =
        byteCodeNames.split(",")
            .map { it.trim() }
            .mapNotNull { Opcode.fromString(it) }
            .map { it.code }

    private fun replaceLastCallContext(updateContext: (ctx: CallContext) -> CallContext) {
        val lastCallContext: CallContext = executionContext.callStack.last()
        val newCallContext = updateContext(lastCallContext)
        val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

        executionContext = executionContext.copy(callStack = newCallStackList)
    }

    private fun toByteList(bytes: String): List<Byte> {
        val noPrefixStack = bytes.replaceFirst("0x", "")
        val cleanStack = if (noPrefixStack.length % 2 == 0) noPrefixStack else "0$noPrefixStack"

        return cleanStack.chunked(2).map { Byte(it) }
    }

    private fun setPreviousCallType(callType: CallType) {
        val callStack =
            if (executionContext.callStack.size > 1) executionContext.callStack
            else listOf(executionContext.callStack.last()) + executionContext.callStack

        val newCallContext = callStack.first().copy(type = callType)
        val newCallStackList = listOf(newCallContext) + executionContext.callStack.drop(1)

        executionContext = executionContext.copy(callStack = newCallStackList)
    }

    private fun createBaseExecutionContext(): ExecutionContext {
        val call = CallContext(
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


        return ExecutionContext(
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
            callStack = listOf(call)
        )
    }
}
