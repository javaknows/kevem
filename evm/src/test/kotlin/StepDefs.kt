package com.gammadex.kevin

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock

class StepDefs : En {

    var executionContext: ExecutionContext = createBaseExecutionContext()

    var executor = Executor()

    var result: ExecutionContext? = null

    init {
        When("(0x[a-zA-Z0-9]+) is pushed onto the stack") { stack: String ->
            replaceLastCallContext { callContext ->
                val newStack = callContext.stack.push(toByteList(stack))
                callContext.copy(stack = newStack)
            }
        }

        When("opcode ([A-Z]+) is executed") { opcode: String ->
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

            Assertions.assertThat(element.dropWhile { it.value == 0 }).isEqualTo(expected)
        }

        Then("The stack is empty") {
            Assertions.assertThat(result!!.stack.size()).isEqualTo(0)
        }

        When("the contract address is (0x[a-zA-Z0-9]+)") { address: String ->
            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(address = Address(address))
                callContext.copy(contract = newContract)
            }
        }

        When("an account with address (0x[a-zA-Z0-9]+) has balance (0x[a-zA-Z0-9]+)") { address: String, balance: String ->
            val value =
                if (balance.startsWith("0x")) BigInteger(balance.replaceFirst("0x", ""), 16)
                else BigInteger(balance)
            val evmState = executionContext.evmState.updateBalance(Address(address), value)

            executionContext = executionContext.copy(evmState = evmState)
        }

        When("transaction origin is (0x[a-zA-Z0-9]+)") { address: String ->
            val currentTransaction = executionContext.currentTransaction.copy(origin = Address(address))
            executionContext = executionContext.copy(currentTransaction = currentTransaction)
        }

        When("the current caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            replaceLastCallContext {
                it.copy(caller = Address(address))
            }
        }

        When("the current call type is ([A-Z]+)") { callType: CallType ->
            replaceLastCallContext {
                it.copy(type = callType)
            }
        }

        When("the current call type is any of") { dataTable: DataTable ->
            val callType = CallType.valueOf(dataTable.asList()[0])

            replaceLastCallContext {
                it.copy(type = callType)
            }
        }

        When("the current call value is (0x[a-zA-Z0-9]+)") { value: String ->
            replaceLastCallContext {
                it.copy(value = BigInteger(value.replaceFirst("0x", ""), 16))
            }
        }

        When("the previous caller address is (0x[a-zA-Z0-9]+)") { address: String ->
            val callStack =
                if (executionContext.callStack.size > 1) executionContext.callStack
                else listOf(executionContext.callStack.last()) + executionContext.callStack

            val newCallContext = callStack.first().copy(caller = Address(address))
            val newCallStackList = listOf(newCallContext) + executionContext.callStack.drop(1)

            executionContext = executionContext.copy(callStack = newCallStackList)
        }

        When("the previous call type is ([A-Z]+)") { callType: CallType ->
            setPreviousCallType(callType)
        }

        When("the previous call type is any of") { dataTable: DataTable ->
            val callType = CallType.valueOf(dataTable.asList()[0])

            setPreviousCallType(callType)
        }

        Then("call data is (empty|0x[a-zA-Z0-9]+)") { value: String ->
            val callData = toByteList(value.replace("empty", "0x"))

            replaceLastCallContext { callContext ->
                callContext.copy(callData = callData)
            }
        }

        Then("(\\d+) bytes of memory from position (\\d+) is (empty|0x[a-zA-Z0-9]+)") { length: Int, start: Int, bytes: String ->
            val expected =
                if (bytes == "empty") Byte.Zero.repeat(length)
                else toByteList(bytes)

            val actual = result!!.memory.get(start, length)

            Assertions.assertThat(actual).isEqualTo(expected)
        }

        Given("contract code is \\[([A-Z0-9, ]+)\\]") { byteCodeNames: String ->
            val byteCode = byteCodeNames.split(",")
                .map { it.trim() }
                .mapNotNull { Opcode.fromString(it) }
                .map { it.code }

            replaceLastCallContext { callContext ->
                val newContract = callContext.contract.copy(code = byteCode)
                callContext.copy(contract = newContract)
            }
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([A-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeNames.split(",")
                .map { it.trim() }
                .mapNotNull { Opcode.fromString(it) }
                .map { it.code }

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
    }

    private fun replaceLastCallContext(updateContext: (ctx: CallContext) -> CallContext) {
        val lastCallContext: CallContext = executionContext.callStack.last()
        val newCallContext = updateContext(lastCallContext)
        val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

        executionContext = executionContext.copy(callStack = newCallStackList)
    }

    private fun toByteList(stack: String): List<Byte> {
        val noPrefixStack = stack.replaceFirst("0x", "")
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

    private fun setCurrentCallType(callType: CallType) {
        val lastCallContext: CallContext = executionContext.callStack.last()
        val newCallContext = lastCallContext.copy(type = callType)
        val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

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
