package com.gammadex.kevin

import cucumber.api.PendingException
import io.cucumber.java8.En
import org.assertj.core.api.Assertions
import java.math.BigInteger
import java.time.Clock

class StepDefs : En {

    var executionContext: ExecutionContext = createBaseExecutionContext()

    var executor = Executor()

    var result: ExecutionContext? = null

    init {
        When("(0x[a-zA-Z0-9]+) is pushed onto the stack") {
            println("Friday")
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

        Then("The stack contains (0x[a-zA-Z0-9]+)") { stack: String ->
            println("woot")
        }

        Then("The stack is empty") {
            Assertions.assertThat(result.stack.size()).isEqualTo(0)
        }
    }

    private fun createBaseExecutionContext(): ExecutionContext {
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
            clock = Clock.systemUTC()
        )
    }

}
