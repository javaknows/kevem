package com.gammadex.kevin

import com.gammadex.kevin.model.*
import com.gammadex.kevin.model.Byte
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
    storage: Storage = Storage(),
    contractCode: List<Byte> = emptyList(),
    evmState: EvmState = EvmState().updateBalance(
        Address(
            BALANCE_ADDRESS
        ), BALANCE_AMOUNT),
    callData: List<Byte> = emptyList(),
    lastReturnData: List<Byte> = emptyList(),
    previousBlocks: Map<BigInteger, Word> = emptyMap()
): ExecutionContext {
    val call = CallContext(
        caller = Address(CALLER),
        callData = callData,
        contract = Contract(contractCode, Address(CONTRACT_ADDRESS)),
        type = CallType.INITIAL,
        value = CALL_VALUE,
        valueRemaining = BigInteger.ZERO,
        stack = stack,
        memory = memory,
        storage = storage
    )

    return ExecutionContext(
        currentBlock = Block(
            number = BigInteger.ONE,
            difficulty = BigInteger.TEN,
            gasLimit = BigInteger("100")
        ),
        currentTransaction = Transaction(
            origin = Address(CALLER),
            gasPrice = GAS_PRICE
        ),
        coinBase = Address("0xFFEEDD"),
        callStack = listOf(call),
        evmState = evmState,
        logs = emptyList(),
        completed = false,
        lastReturnData = lastReturnData,
        clock = Clock.systemUTC(),
        previousBlocks = previousBlocks
    )
}