package com.gammadex.kevin

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
            val word = Word.coerceFrom(stack)
            val lastCallContext: CallContext = executionContext.callStack.last()
            val newStack = lastCallContext.stack.push(word.data)
            val newCallContext = lastCallContext.copy(stack = newStack)
            val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

            executionContext = executionContext.copy(callStack = newCallStackList)
        }

        When("opcode ([A-Z]+) is executed") { opcode: String ->
            val contract = executionContext.currentCallContext.contract.copy(code = listOf(Opcode.valueOf(opcode).code))
            val callContext = executionContext.currentCallContext.copy(contract = contract)
            val callStack = executionContext.callStack.dropLast(1) + callContext

            executionContext = executionContext.copy(callStack = callStack)

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
            val lastCallContext: CallContext = executionContext.callStack.last()
            val newContract = lastCallContext.contract.copy(address = Address(address))
            val newCallContext = lastCallContext.copy(contract = newContract)
            val newCallStackList = executionContext.callStack.dropLast(1) + newCallContext

            executionContext = executionContext.copy(callStack = newCallStackList)
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
