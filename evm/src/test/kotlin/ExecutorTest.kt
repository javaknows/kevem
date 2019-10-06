package com.gammadex.kevin

import org.junit.jupiter.api.Test

import java.math.BigInteger
import java.time.Clock
import org.assertj.core.api.Assertions.assertThat

private const val CONTRACT_ADDRESS = "0x0000000000000000000000000000000000000000000000000000000000cccccc"
private const val BALANCE_ADDRESS = "0x0000000000000000000000000000000000000000000000000000000000aaaaaa"
private val BALANCE_AMOUNT = BigInteger("1234", 16)
private const val CALLER = "0x0000000000000000000000000000000000000000000000000000000000aabbcc"
private val CALL_VALUE = BigInteger("1111", 16)
private val GAS_PRICE = BigInteger("200", 16)

class ExecutorTest {

    private val underTest = Executor()

    @Test
    internal fun `check address is that of current contract`() =
        checkStackResult(emptyList(), Opcode.ADDRESS, CONTRACT_ADDRESS)

    @Test
    internal fun `check balance of address comes from evm state`() =
        checkStackResult(listOf(Word.coerceFrom(BALANCE_ADDRESS).data), Opcode.BALANCE, "0x1234")

    @Test
    internal fun `check origin is original caller`() =
        checkStackResult(emptyList(), Opcode.ORIGIN, CALLER)

    @Test
    internal fun `check caller is current caller`() =
        checkStackResult(emptyList(), Opcode.CALLER, CALLER)

    @Test
    internal fun `check call value `() =
        checkStackResult(emptyList(), Opcode.CALLVALUE, "0x1111")

    @Test
    internal fun `check call data load `() {
        val callData = Word.coerceFrom("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe").data

        val context = baseExecutionContext(
            stack = Stack(listOf(listOf(Byte(0)))),
            contractCode = listOf(Opcode.CALLDATALOAD.code),
            callData = callData
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(callData)
    }

    @Test
    internal fun `check call data size `() {
        val callData = Word.coerceFrom("0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe").data

        val context = baseExecutionContext(
            stack = Stack(),
            contractCode = listOf(Opcode.CALLDATASIZE.code),
            callData = callData
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(Word.coerceFrom(0x20).data.dropWhile { it == Byte.Zero })
    }

    @Test
    internal fun `check call data copy `() {
        val callData = Word.coerceFrom("0xffeefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe").data

        val context = baseExecutionContext(
            stack = Stack(
                listOf(
                    listOf(Byte(0x3)), // to
                    listOf(Byte(0)),   // from
                    listOf(Byte(2))    // size
                )
            ),
            contractCode = listOf(Opcode.CALLDATACOPY.code),
            callData = callData
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(0)
        assertThat(result.memory.get(3, 2)).isEqualTo(listOf(Byte(0xff), Byte(0xee)))
        assertThat(result.memory.get(2, 1)).isEqualTo(listOf(Byte(0)))
        assertThat(result.memory.get(5, 1)).isEqualTo(listOf(Byte(0)))
    }

    @Test
    internal fun `check call code size`() {
        val context = baseExecutionContext(
            contractCode = listOf(Opcode.CODESIZE.code, Opcode.DUP1.code, Opcode.DUP2.code)
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(listOf(Byte(3)))
    }

    @Test
    internal fun `check code copy `() {
        val context = baseExecutionContext(
            stack = Stack(
                listOf(
                    listOf(Byte(0x3)), // to
                    listOf(Byte(1)),   // from
                    listOf(Byte(2))    // size
                )
            ),
            contractCode = listOf(Opcode.CODECOPY.code, Opcode.DUP1.code, Opcode.DUP2.code)
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(0)
        assertThat(result.memory.get(3, 2)).isEqualTo(listOf(Opcode.DUP1.code, Opcode.DUP2.code))
        assertThat(result.memory.get(2, 1)).isEqualTo(listOf(Byte(0)))
        assertThat(result.memory.get(5, 1)).isEqualTo(listOf(Byte(0)))
    }

    @Test
    internal fun `check gas price`() = checkStackResult(Opcode.GASPRICE, "0x200")

    @Test
    internal fun `check ext code copy`() {
        val externaLaddress = Address("0xeefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe")
        val externalCode = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { Byte(it) }

        val context = baseExecutionContext(
            contractCode = listOf(Opcode.EXTCODECOPY.code),
            evmState = EvmState().updateBalanceAndContract(
                    externaLaddress,
                    BigInteger.ZERO,
                    Contract(
                        externalCode,
                        externaLaddress
                    )
                ),
            stack = Stack(
                listOf(
                    Word.coerceFrom(externaLaddress.value).data, // a
                    listOf(Byte(2)), // t
                    listOf(Byte(3)), // f
                    listOf(Byte(2))  // s
                )
            )
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(0)
        assertThat(result.memory.get(2, 2)).isEqualTo(listOf(Byte(4), Byte(5)))
    }

    @Test
    internal fun `check ext code size`() {
        val externaLaddress = Address("0xeefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe")
        val externalCode = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { Byte(it) }

        val context = baseExecutionContext(
            contractCode = listOf(Opcode.EXTCODESIZE.code),
            evmState = EvmState().updateBalanceAndContract(
                externaLaddress,
                BigInteger.ZERO,
                Contract(
                    externalCode,
                    externaLaddress
                )
            ),
            stack = Stack(listOf(Word.coerceFrom(externaLaddress.value).data))
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(listOf(Byte(10)))
    }

    @Test
    internal fun `check return data size`() {
        val lastReturnData = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { Byte(it) }

        val context = baseExecutionContext(
            contractCode = listOf(Opcode.RETURNDATASIZE.code),
            lastReturnData = lastReturnData
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(listOf(Byte(10)))
    }

    @Test
    internal fun `check return data copy`() {
        val lastReturnData = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { Byte(it) }

        val context = baseExecutionContext(
            contractCode = listOf(Opcode.RETURNDATACOPY.code),
            lastReturnData = lastReturnData,
            stack = Stack(
                listOf(
                    listOf(Byte(3)), // t
                    listOf(Byte(4)), // f
                    listOf(Byte(3))  // s
                )
            )
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(0)
        assertThat(result.memory.get(3, 3)).isEqualTo(listOf(Byte(5), Byte(6), Byte(7)))
    }

    @Test
    internal fun `check blockhash`() {
        val lastReturnData = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map { Byte(it) }

        val context = baseExecutionContext(
            contractCode = listOf(Opcode.BLOCKHASH.code),
            lastReturnData = lastReturnData,
            previousBlocks = mapOf(Pair(BigInteger.TEN, Word.coerceFrom(2))),
            stack = Stack(listOf(listOf(Byte(10))))
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(listOf(Byte(2)))
    }








    private fun checkStackResult(a: List<Byte>, b: List<Byte>, opcode: Opcode, expectedResult: List<Byte>) =
        checkStackResult(listOf(a, b), opcode, expectedResult)

    private fun checkStackResult(opcode: Opcode, expectedResult: String) =
        checkStackResult(emptyList(), opcode, expectedResult)

    private fun checkStackResult(stackContents: List<List<Byte>>, opcode: Opcode, expectedResult: String) =
        checkStackResult(stackContents, opcode, Word.coerceFrom(expectedResult).data.dropWhile { it.value == 0 })

    private fun checkStackResult(stackContents: List<List<Byte>>, opcode: Opcode, expectedResult: List<Byte>) {
        val context = baseExecutionContext(
            stack = Stack(stackContents),
            contractCode = listOf(opcode.code)
        )

        val result = underTest.execute(context, context)

        assertThat(result.stack.size()).isEqualTo(1)
        assertThat(result.stack.peek(0)).isEqualTo(expectedResult)
    }
}


internal fun baseExecutionContext(
    stack: Stack = Stack(),
    memory: Memory = Memory(),
    storage: Storage = Storage(),
    contractCode: List<Byte> = emptyList(),
    evmState: EvmState = EvmState().updateBalance(Address(BALANCE_ADDRESS), BALANCE_AMOUNT),
    callData: List<Byte> = emptyList(),
    lastReturnData: List<Byte> = emptyList(),
    previousBlocks: Map<BigInteger,Word> = emptyMap()
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