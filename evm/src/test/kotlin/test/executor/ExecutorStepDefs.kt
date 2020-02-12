package org.kevm.evm.test.executor

import org.kevm.evm.Executor
import org.kevm.evm.Opcode
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.lang.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.EIP
import org.kevm.evm.collections.BigIntegerIndexedList
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import org.kevm.evm.util.*

class ExecutorStepDefs : En {

    private var executionContext: ExecutionContext = createBaseExecutionContext()

    private val executor = Executor(
        GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
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

        Given("the stack contains elements \\[([xA-Z0-9, ]+)\\]") { list: String ->
            val elements = list.split(",")
                .map { it.trim() }
                .map { toByteList(it) }
                .reversed()

            updateLastCallContext {
                val newStack = elements.fold(it.stack) { acc, e ->
                    acc.push(e)
                }
                it.copy(stack = newStack)
            }
        }

        Given("the stack contains ([0-9]+) elements") { num: Int ->
            val elements = Byte.Zero.repeat(num)

            updateLastCallContext {
                val newStack = elements.fold(it.stack) { acc, e ->
                    acc.push(listOf(e))
                }
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
                it.copy(code = BigIntegerIndexedList.fromBytes(code))
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
                it.copy(contractAddress = Address(address), storageAddress = Address(address))
            }
        }

        Given(".* account with address (0x[a-zA-Z0-9]+) has balance (0x[a-zA-Z0-9]+)") { address: String, balance: String ->
            val value = toBigInteger(balance)

            updateExecutionContext {
                val evmState = it.accounts.updateBalance(Address(address), value)
                it.copy(accounts = evmState)
            }
        }

        Given("there is no existing account with address (.*)") { address: String ->
            updateExecutionContext {
                val evmState = it.accounts.removeAccount(Address(address))
                it.copy(accounts = evmState)
            }
        }

        Then("there is now no account with address (.*)") { address: String ->
            checkResult {
                assertThat(it.accounts.accountExists(Address(address))).isFalse()
            }
        }

        Given("an account with address (.*) exists") { address: String ->
            updateExecutionContext {
                if (it.accounts.accountExists(Address(address))) it
                else {
                    val evmState = it.accounts.updateBalance(Address(address), BigInteger.ZERO)
                    it.copy(accounts = evmState)
                }
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

        Given("call data is (empty|0x[a-zA-Z0-9]+)") { value: String ->
            val callData = BigIntegerIndexedList.fromByteString(value.replace("empty", "0x"))

            updateLastCallContext { callContext ->
                callContext.copy(callData = callData)
            }
        }

        When("([a-zA-Z0-9]+) bytes? of memory from position ([a-zA-Z0-9]+) is (empty|0x[a-zA-Z0-9]+)") { length: String, start: String, bytes: String ->
            val expected =
                if (bytes == "empty") Byte.Zero.repeat(toInt(length))
                else toByteList(bytes)

            checkResult {
                val actual = it.memory.peek(toInt(start), toInt(length))
                assertThat(actual).isEqualTo(expected)
            }
        }

        Given("contract code is \\[([xA-Z0-9, ]+)\\]") { byteCodeNames: String ->
            val byteCode = BigIntegerIndexedList.fromBytes(byteCodeOrDataFromNamesOrHex(byteCodeNames))

            updateLastCallContext { callContext ->
                callContext.copy(code = byteCode)
            }
        }

        Given("contract code is (0x[a-zA-Z0-9]+)") { byteCode: String ->
            updateLastCallContext { callContext ->
                callContext.copy(code = BigIntegerIndexedList.fromBytes(toByteList(byteCode)))
            }
        }

        Given("contract at address (0x[a-zA-Z0-9]+) has code \\[([A-Z0-9, ]+)\\]") { address: String, byteCodeNames: String ->
            val byteCode = byteCodeOrDataFromNamesOrHex(byteCodeNames)
            val newAddress = Address(address)
            val newContract = Contract(byteCode)

            updateExecutionContext {
                val evmState = it.accounts.updateContract(newAddress, newContract)
                it.copy(accounts = evmState)
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
                it.copy(config = it.config.copy(coinbase = Address(address)))
            }
        }

        Given("time is \"(.*)\"") { date: String ->
            updateExecutionContext {
                it.copy(currentBlock = it.currentBlock.copy(timestamp = Instant.parse(date)))
            }
        }

        Then("the stack contains a timestamp of \"(.*)\"") { date: String ->
            val expected = Instant.parse(date)

            checkResult {
                assertThat(it.currentBlock.timestamp).isEqualTo(expected)
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

        Given("(.*) is stored in memory at location (0x[a-zA-Z0-9]+)") { data: String, location: String ->
            val bytes =
                if (data == "some data" || data == "a word of data")
                    "0x1234567890123456789012345678901234567890123456789012345678901234"
                else data

            updateLastCallContext {
                val newMemory = it.memory.write(toInt(location), toByteList(bytes))
                it.copy(memory = newMemory)
            }
        }

        Given("(0x[a-zA-Z0-9]+) is in storage at location (0x[a-zA-Z0-9]+) of (.*)") { data: String, location: String, contractAddress: String ->
            updateExecutionContext { ctx ->
                val address =
                    if (contractAddress == "current contract") ctx.currentCallCtx.contractAddress.toString()
                    else contractAddress.replace("contract ", "")

                ctx.copy(
                    accounts = ctx.accounts.updateStorage(
                        Address(address),
                        toBigInteger(location),
                        Word.coerceFrom(data)
                    )
                )
            }
        }

        Then("data in storage at location (.*) of (.*) is now (0x[a-zA-Z0-9]+)") { location: String, contractAddress: String, data: String ->
            checkResult {
                val address =
                    if (contractAddress == "current contract") it.currentCallCtx.contractAddress.toString()
                    else contractAddress.replace("contract ", "")

                assertThat(it.accounts.storageAt(Address(address), toBigInteger(location))).isEqualTo(
                    Word.coerceFrom(
                        data
                    )
                )
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

        Given("there is ([0-9xA-Za-z]*) gas used") { gas: String ->
            updateLastCallContext {
                it.copy(gasUsed = toBigInteger(gas))
            }
        }

        Given("EIP ([0-9A-Za-z]+) is enabled") { eipName: String ->
            val eip = EIP.valueOf(eipName)
            updateExecutionContext {
                it.copy(features = Features(it.features.eips + listOf(eip)))
            }
        }

        Given("contract code ends with (0x[a-zA-Z0-9]+)") { data: String ->
            updateLastCallContext {
                val code = BigIntegerIndexedList.fromByteString(data)
                it.copy(code = code)
            }
        }

        Given("the push opcode is executed it will have data on stack") { dataTable: DataTable ->
            processRows(dataTable) {
                val opcode = Opcode.fromName(it[0])
                val expected = toByteList(it[1])

                updateLastCallContext { ctx ->
                    val code = BigIntegerIndexedList.fromBytes(listOf(opcode!!.code)) + ctx.code
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
                    val code = BigIntegerIndexedList.fromBytes(listOf(opcode!!.code)) + ctx.code
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
                    val code = BigIntegerIndexedList.fromBytes(listOf(opcode!!.code)) + ctx.code
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

        Given("the chain ID is ([a-zA-Z0-9]+)") { chainId: String ->
            updateExecutionContext {
                it.copy(config = it.config.copy(chainId = toBigInteger(chainId)))
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
                    val code = BigIntegerIndexedList.fromBytes(listOf(opcode!!.code)) + ctx.code
                    ctx.copy(code = code, gas = BigInteger("100000000"))
                }

                executeContext()

                checkResult { result ->
                    val gasUsed = result.currentCallCtx.gas - result.currentCallCtx.gasRemaining
                    assertThat(gasUsed).isEqualTo(gas)
                }
            }
        }

        Then("the balance of account (0x[a-zA-Z0-9]+) is now (.*)") { address: String, amount: String ->
            checkResult {
                val balance = it.accounts.balanceOf(Address(address))
                assertThat(toBigInteger(amount)).isEqualTo(balance)
            }
        }

        Then("the code at address (0x[a-zA-Z0-9]+) is (.*)") { address: String, expectedCode: String ->
            val parsedExpectedCode = if (expectedCode == "empty") emptyList() else toByteList(expectedCode)

            checkResult {
                val code = it.accounts.codeAt(Address(address))
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
                assertThat(currentCall.callData).isEqualTo(BigIntegerIndexedList.fromByteString(callData))
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

        Then("(.*) gas is now used(.*)") { gas: String, by: String ->
            checkResult {
                val ctx =
                    if (by == " by the previous call context") it.callStack.takeLast(2).first()
                    else it.currentCallCtx

                assertThat(ctx.gasUsed).isEqualTo(toBigInteger(gas))
            }
        }

        Then("the transaction has now used (.*) gas") { gas: String ->
            checkResult {
                assertThat(it.gasUsed).isEqualTo(toBigInteger(gas))
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

        Then("account (.*) has a refund of (.*)") { ac: String, am: String ->
            val account = Address(ac)
            val amount = toBigInteger(am)

            checkResult {
                val refund: BigInteger = it.refunds.getOrDefault(account, BigInteger.ZERO)
                assertThat(refund).isEqualTo(amount)
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
            callData = BigIntegerIndexedList.fromByteString(callData),
            value = toBigInteger(value),
            gas = toBigInteger(gas),
            returnLocation = toInt(outLocation),
            returnSize = toInt(outSize),
            contractAddress = Address(contractAddress)
        )

        val newEvmState = executionContext.accounts.updateContract(
            Address(contractAddress),
            Contract(emptyList())
        )

        return Pair(callCtx, executionContext.copy(accounts = newEvmState))
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
                gasLimit = BigInteger("100"),
                timestamp = Clock.systemUTC().instant()
            ),
            currentTransaction = Transaction(
                origin = Address("0xFFEEDD"),
                gasPrice = BigInteger.ONE
            ),
            logs = emptyList(),
            completed = false,
            callStack = listOf(
                CallContext(
                    caller = Address("0x0"),
                    callData = BigIntegerIndexedList.emptyByteList(),
                    code = BigIntegerIndexedList.fromBytes(listOf(Opcode.INVALID.code)),
                    type = CallType.INITIAL,
                    value = BigInteger.ZERO,
                    stack = Stack(),
                    memory = Memory(),
                    contractAddress = Address("0x0"),
                    storageAddress = Address("0x0"),
                    gas = BigInteger("1000000000000000000000")
                )
            ),
            config = EvmConfig(
                chainId = BigInteger.TWO,
                coinbase = Address("0xFFEEDD")
            )
        )
}
