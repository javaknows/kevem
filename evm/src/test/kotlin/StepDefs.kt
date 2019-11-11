package com.gammadex.kevin.evm

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
import java.time.ZoneId
import com.gammadex.kevin.evm.util.*

class StepDefs : En {

    private var executionContext: ExecutionContext = createBaseExecutionContext()

    private val executor = Executor(
        GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc()),
            MemoryUsageGasCostCalculator(MemoryUsageGasCalc())
        )
    )

    private var result: ExecutionContext? = null

    init {
        Given("(0x[a-zA-Z0-9]+) is pushed onto the stack") { stack: String ->
            updateLastCallContext {
                val newStack = it.stack.push(toByteList(stack))
                it.copy(stack = newStack)
            }
        }

        When("the next opcode in the context is executed") {
            executeContext()
        }

        When("the context is executed to completion") {
            executeAllContext()
        }

        When("opcode (.*) is executed") { opcode: String ->
            val code =
                if (opcode.contains("0x")) toByteList(opcode).take(1)
                else listOf(Opcode.valueOf(opcode).code, Opcode.JUMPDEST.code)

            updateLastCallContext {
                it.copy(code = code)
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
                it.copy(contractAddress = Address(address))
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

        // TODO - multi-dimensional
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

        // TODO - multi-dimensional
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

        Given("([a-zA-Z0-9]+) bytes? of memory from position ([a-zA-Z0-9]+) is (empty|0x[a-zA-Z0-9]+)") { length: String, start: String, bytes: String ->
            val expected =
                if (bytes == "empty") Byte.Zero.repeat(toInt(length))
                else toByteList(bytes)

            checkResult {
                val actual = it.memory.peek(toInt(start), toInt(length))
                assertThat(actual).isEqualTo(expected)
            }
        }

        Given("contract code is \\[([xA-Z0-9, ]+)\\]") { byteCodeNames: String ->
            val byteCode = byteCodeOrDataFromNamesOrHex(byteCodeNames)

            updateLastCallContext { callContext ->
                callContext.copy(code = byteCode)
            }
        }

        Given("contract code is (0x[a-zA-Z0-9]+)") { byteCode: String ->
            updateLastCallContext { callContext ->
                callContext.copy(code = toByteList(byteCode))
            }
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([A-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeOrDataFromNamesOrHex(byteCodeNames)
            val newAddress = Address(address)
            val newContract = Contract(byteCode)

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
                val newMemory = it.memory.write(toInt(location), toByteList(data))
                it.copy(memory = newMemory)
            }
        }

        Given("(0x[a-zA-Z0-9]+) is in storage at location (0x[a-zA-Z0-9]+) of (.*)") { data: String, location: String, contractAddress: String ->
            updateExecutionContext { ctx ->
                val address =
                    if (contractAddress == "current contract") ctx.currentCallCtx.contractAddress.toString()
                    else contractAddress.replace("contract ", "")

                ctx.copy(
                    evmState = ctx.evmState.updateStorage(
                        Address(address),
                        toInt(location),
                        Word.coerceFrom(data)
                    )
                )
            }
        }

        Then("data in storage at location (\\d+) of (.*) is now (0x[a-zA-Z0-9]+)") { location: Int, contractAddress: String, data: String ->
            checkResult {
                val address =
                    if (contractAddress == "current contract") it.currentCallCtx.contractAddress.toString()
                    else contractAddress.replace("contract ", "")

                assertThat(it.evmState.storageAt(Address(address), location)).isEqualTo(Word.coerceFrom(data))
            }
        }

        Then("the next position in code is now (\\d+)") { position: Int ->
            checkResult {
                assertThat(it.currentCallCtx.currentLocation).isEqualTo(position)
            }
        }

        Given("the code location is (\\d+)") { position: Int ->
            updateLastCallContext {
                it.copy(currentLocation = position)
            }
        }

        Given("there is ([0-9xA-Za-z]*) gas remaining") { gas: String ->
            updateLastCallContext {
                it.copy(gas = toBigInteger(gas), gasUsed = BigInteger.ZERO)
            }
        }

        Given("contract code ends with (0x[a-zA-Z0-9]+)") { data: String ->
            updateLastCallContext {
                val code = toByteList(data)
                it.copy(code = code)
            }
        }

        Given("the push opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromName(it[0])
                val expected = toByteList(it[1])

                updateLastCallContext { ctx ->
                    val code = listOf(opcode!!.code) + ctx.code
                    ctx.copy(code = code)
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
                val opcode = Opcode.fromName(it[0])
                val expected = toByteList(it[1])

                updateLastCallContext { ctx ->
                    val code = listOf(opcode!!.code) + ctx.code
                    ctx.copy(code = code)
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
                val opcode = Opcode.fromName(it[0])
                val expected = Word.coerceFrom(it[1])
                val indexOfAA = toInt(it[2])

                updateLastCallContext { ctx ->
                    val code = listOf(opcode!!.code) + ctx.code
                    ctx.copy(code = code)
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

        Then("a log has been generated with no data") { ->
            checkResult {
                assertThat(it.logs).hasSize(1)
                assertThat(it.logs[0].data).isEmpty()
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

        When("an opcode is executed it consumes gas:") { dataTable: DataTable ->
            processRows(dataTable, true) {
                val opcode = Opcode.fromName(it[0])
                val gas = it[1].toInt()

                updateLastCallContext { ctx ->
                    val code = listOf(opcode!!.code) + ctx.code
                    ctx.copy(code = code, gas = BigInteger("100000000"))
                }

                executeContext()

                checkResult { result ->
                    val gasUsed = result.currentCallCtx.gas - result.currentCallCtx.gasRemaining
                    assertThat(gasUsed).isEqualTo(gas)
                }
            }
        }

        Given("any new account gets created with address (0x[a-zA-Z0-9]+)") { address: String ->
            updateExecutionContext {
                it.copy(addressGenerator = object : AddressGenerator {
                    override fun nextAddress(): Address =
                        Address(address)
                })
            }
        }

        Then("the balance of account (0x[a-zA-Z0-9]+) is now (.*)") { address: String, amount: String ->
            checkResult {
                val balance = it.evmState.balanceOf(Address(address))
                assertThat(toBigInteger(amount)).isEqualTo(balance)
            }
        }

        Then("the code at address (0x[a-zA-Z0-9]+) is (.*)") { address: String, expectedCode: String ->
            val parsedExpectedCode = if (expectedCode == "empty") emptyList() else toByteList(expectedCode)

            checkResult {
                val code = it.evmState.codeAt(Address(address))
                assertThat(code).isEqualTo(parsedExpectedCode)
            }
        }

        Then("the call stack is now ([0-9]+) deep") { depth: Int ->
            checkResult {
                assertThat(it.callStack.size).isEqualTo(depth)
            }
        }

        Then("the current call now has the following:") { dataTable: DataTable ->
            val (type, callerAddress, callData, contractAddress, value, gas, outLocation, outSize) = dataTable.asLists()[1]

            checkResult {
                val currentCall = it.currentCallCtx

                assertThat(currentCall.type).isEqualTo(CallType.valueOf(type))
                assertThat(currentCall.caller).isEqualTo(Address(callerAddress))
                assertThat(currentCall.callData).isEqualTo(toByteList(callData))
                assertThat(currentCall.contractAddress).isEqualTo(Address(contractAddress))
                assertThat(currentCall.value).isEqualTo(toBigInteger(value))
                assertThat(currentCall.gasRemaining).isEqualTo(toBigInteger(gas))
                assertThat(currentCall.returnLocation).isEqualTo(toInt(outLocation))
                assertThat(currentCall.returnSize).isEqualTo(toInt(outSize))
            }
        }

        Then("the previous call gas remaining is now (.*)") { gas: String ->
            checkResult {
                val prevCallContext = it.callStack.takeLast(2).first()
                assertThat(prevCallContext.gasRemaining).isEqualTo(toBigInteger(gas))
            }
        }

        Then("there is now (.*) gas remaining") { gas: String ->
            checkResult {
                assertThat(it.currentCallCtx.gasRemaining).isEqualTo(toBigInteger(gas))
            }
        }

        When("the current call is:") { dataTable: DataTable ->
            val currentCallContext = executionContext.currentCallCtx

            val (newCallCtx, newExecutionCtx) = copyContextWithTableData(
                dataTable,
                currentCallContext,
                executionContext
            )

            executionContext = newExecutionCtx.copy(
                callStack = newExecutionCtx.callStack.dropLast(1) + newCallCtx
            )
        }

        When("the previous call is:") { dataTable: DataTable ->
            val (prevCallContext, lastCallContext) = with(executionContext) {
                if (callStack.size > 1) Pair(callStack[callStack.size - 2], callStack.last())
                else Pair(callStack[0].copy(), callStack[0])
            }

            val (newCallCtx, newExecutionCtx) = copyContextWithTableData(dataTable, prevCallContext, executionContext)

            executionContext = newExecutionCtx.copy(
                callStack = newExecutionCtx.callStack.dropLast(2) + newCallCtx + lastCallContext
            )
        }

        When("there is only one call on the stack") {
            executionContext = executionContext.copy(
                callStack = executionContext.callStack.takeLast(1)
            )
        }

        Then("the execution context is now marked as complete") {
            checkResult {
                assertThat(it.completed).isTrue()
            }
        }

        Then("the execution context is now marked as not complete") {
            checkResult {
                assertThat(it.completed).isFalse()
            }
        }

        Then("return data is now (empty|0x[a-zA-Z0-9]+)") { value: String ->
            val data = toByteList(value.replace("empty", "0x"))

            checkResult {
                assertThat(it.lastReturnData).isEqualTo(data)
            }
        }

        Then("the last error is now ([A-Z0-9_]+) with message \"(.*)\"") { code: String, message: String ->
            val errorCode = ErrorCode.valueOf(code)
            val expectedError = EvmError(errorCode, message)

            checkResult {
                assertThat(it.lastCallError).isEqualTo(expectedError)
            }
        }

        Then("there is no last error") {
            checkResult {
                assertThat(it.lastCallError).isEqualTo(EvmError.None)
            }
        }

        // TODO - multi-dimensional
        Given("the opcode is any of") { dataTable: DataTable ->
            dataTable.asLists().forEach {
                val opcode = Opcode.fromName(it[0])

                updateLastCallContext { ctx ->
                    val code = listOf(opcode!!.code) + ctx.code
                    ctx.copy(code = code)
                }
            }
        }
    }

    private fun copyContextWithTableData(
        dataTable: DataTable,
        currentCallContext: CallContext,
        executionContext: ExecutionContext
    ): Pair<CallContext, ExecutionContext> {
        val (type, callerAddress, callData, contractAddress, value, gas, outLocation, outSize) = dataTable.asLists()[1]

        val callCtx = currentCallContext.copy(
            type = CallType.valueOf(type),
            caller = Address(callerAddress),
            callData = toByteList(callData),
            value = toBigInteger(value),
            gas = toBigInteger(gas),
            returnLocation = toInt(outLocation),
            returnSize = toInt(outSize),
            contractAddress = Address(contractAddress)
        )

        val newEvmState = executionContext.evmState.updateContract(
            Address(contractAddress),
            Contract(emptyList())
        )

        return Pair(callCtx, executionContext.copy(evmState = newEvmState))
    }

    private fun updateExecutionContext(updateFunc: (ExecutionContext) -> ExecutionContext) {
        executionContext = updateFunc(executionContext)
    }

    private fun checkResult(checker: (ExecutionContext) -> Unit) = checker(result!!)

    private fun processRows(dataTable: DataTable, dropFirst: Boolean = false, processRow: (List<String>) -> Unit) {
        val originalContext = executionContext

        val numToDrop = if (dropFirst) 1 else 0

        dataTable.asLists().drop(numToDrop).forEach {
            executionContext = originalContext

            processRow(it)
        }
    }

    private fun executeContext() {
        result = executor.executeNextOpcode(executionContext)
    }

    private fun executeAllContext() {
        result = executor.executeAll(executionContext)
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

    private fun createBaseExecutionContext(): ExecutionContext =
        ExecutionContext(
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
                    code = listOf(Opcode.INVALID.code),
                    type = CallType.INITIAL,
                    value = BigInteger.ZERO,
                    stack = Stack(),
                    memory = Memory(),
                    contractAddress = Address("0x0"),
                    storageAddress = Address("0x0"),
                    gas = BigInteger("1000000000000000000000")
                )
            )
        )
}
