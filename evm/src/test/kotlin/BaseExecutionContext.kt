package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
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
    evmState: EvmState = EvmState()
        .updateBalance(Address(BALANCE_ADDRESS), BALANCE_AMOUNT)
        .updateContract(Address(CONTRACT_ADDRESS), Contract(contractCode)),
    callData: List<Byte> = emptyList(),
    lastReturnData: List<Byte> = emptyList(),
    previousBlocks: Map<BigInteger, Word> = emptyMap()
): ExecutionContext {
    val call = CallContext(
        caller = Address(CALLER),
        callData = callData,
        type = CallType.INITIAL,
        value = CALL_VALUE,
        code = contractCode,
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