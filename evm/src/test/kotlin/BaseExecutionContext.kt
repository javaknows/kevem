package org.kevem.evm

import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.evm.collections.BigIntegerIndexedList.Companion.emptyByteList
import org.kevem.evm.model.*
import org.kevem.evm.model.Byte
import java.math.BigInteger
import java.time.Clock

private const val CONTRACT_ADDRESS = "0x0000000000000000000000000000000000000000000000000000000000cccccc"
private const val BALANCE_ADDRESS = "0x0000000000000000000000000000000000000000000000000000000000aaaaaa"
private val BALANCE_AMOUNT = BigInteger("1234", 16)
private const val CALLER = "0x0000000000000000000000000000000000000000000000000000000000aabbcc"
private val CALL_VALUE = BigInteger("1111", 16)
private val GAS_PRICE = BigInteger("200", 16)

internal fun baseExecutionContext(
    stack: Stack = Stack(),
    memory: Memory = Memory(),
    contractCode: List<Byte> = emptyList(),
    accounts: Accounts = Accounts()
        .updateBalance(Address(BALANCE_ADDRESS), BALANCE_AMOUNT)
        .updateContract(Address(CONTRACT_ADDRESS), Contract(BigIntegerIndexedList.fromBytes(contractCode))),
    callData: BigIntegerIndexedList<Byte> = BigIntegerIndexedList.emptyByteList(),
    lastReturnData: BigIntegerIndexedList<Byte> = emptyByteList(),
    previousBlocks: Map<BigInteger, Word> = emptyMap()
): ExecutionContext {
    val call = CallContext(
        caller = Address(CALLER),
        callData = callData,
        type = CallType.INITIAL,
        value = CALL_VALUE,
        code = BigIntegerIndexedList.fromBytes(contractCode),
        stack = stack,
        memory = memory,
        storageAddress = Address(CONTRACT_ADDRESS),
        contractAddress = Address(CONTRACT_ADDRESS),
        gas = BigInteger("99999999999999999999999")
    )

    return ExecutionContext(
        currentBlock = Block(
            number = BigInteger.ONE,
            difficulty = BigInteger.TEN,
            gasLimit = BigInteger("100"),
            timestamp = Clock.systemUTC().instant()
        ),
        currentTransaction = Transaction(
            origin = Address(CALLER),
            gasPrice = GAS_PRICE
        ),
        callStack = listOf(call),
        accounts = accounts,
        logs = emptyList(),
        completed = false,
        lastReturnData = lastReturnData,
        previousBlocks = previousBlocks,
        config = EvmConfig(
            chainId = BigInteger.TWO,
            coinbase = Address("0xFFEEDD")
        )
    )
}