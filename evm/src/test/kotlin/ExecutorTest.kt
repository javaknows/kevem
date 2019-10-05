package com.gammadex.kevin

import org.junit.jupiter.api.Test

import java.math.BigInteger
import java.time.Clock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled

class ExecutorTest {

    private val underTest = Executor()

    @Test
    internal fun `check add two numbers`() =
        checkStackResult(listOf(Byte(1)), listOf(Byte(2)), Opcode.ADD, listOf(Byte(3)))

    @Test
    internal fun `check multiply two numbers`() =
        checkStackResult(listOf(Byte(3)), listOf(Byte(2)), Opcode.MUL, listOf(Byte(6)))

    @Test
    internal fun `check subtract two numbers`() =
        checkStackResult(listOf(Byte(3)), listOf(Byte(2)), Opcode.SUB, listOf(Byte(1)))

    @Test
    internal fun `check divide two numbers`() =
        checkStackResult(listOf(Byte(6)), listOf(Byte(2)), Opcode.DIV, listOf(Byte(3)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check signed divide two numbers`() =
        checkStackResult(listOf(Byte(6)), listOf(Byte(2)), Opcode.SDIV, listOf(Byte(3)))

    @Test
    internal fun `check modulo of two numbers`() =
        checkStackResult(listOf(Byte(7)), listOf(Byte(2)), Opcode.MOD, listOf(Byte(1)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check signed modulo of two numbers`() =
        checkStackResult(listOf(Byte(7)), listOf(Byte(2)), Opcode.SMOD, listOf(Byte(1)))

    @Test
    internal fun `check add modulo of two numbers`() =
        checkStackResult(listOf(listOf(Byte(6)), listOf(Byte(1)), listOf(Byte(2))), Opcode.ADDMOD, listOf(Byte(1)))

    @Test
    internal fun `check multiply modulo of two numbers`() =
        checkStackResult(listOf(listOf(Byte(7)), listOf(Byte(3)), listOf(Byte(2))), Opcode.MULMOD, listOf(Byte(1)))

    @Test
    internal fun `check exponential of two numbers`() =
        checkStackResult(listOf(Byte(2)), listOf(Byte(2)), Opcode.EXP, listOf(Byte(4)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check signed extend of two numbers`() =
        checkStackResult(listOf(Byte(7)), listOf(Byte(2)), Opcode.SIGNEXTEND, listOf(Byte(1)))

    @Test
    internal fun `check less than for two numbers`() =
        checkStackResult(listOf(Byte(1)), listOf(Byte(2)), Opcode.LT, listOf(Byte(1)))

    @Test
    internal fun `check greater than for two numbers`() =
        checkStackResult(listOf(Byte(2)), listOf(Byte(1)), Opcode.GT, listOf(Byte(1)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check signed less than for two numbers`() =
        checkStackResult(listOf(Byte(1)), listOf(Byte(2)), Opcode.LT, listOf(Byte(1)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check signed greater than for two numbers`() =
        checkStackResult(listOf(Byte(2)), listOf(Byte(1)), Opcode.GT, listOf(Byte(1)))

    @Test
    internal fun `check for equality of two numbers`() =
        checkStackResult(listOf(Byte(2)), listOf(Byte(2)), Opcode.EQ, listOf(Byte(1)))

    @Test
    internal fun `check if a number is zero`() =
        checkStackResult(listOf(listOf(Byte(0))), Opcode.ISZERO, listOf(Byte(1)))

    @Test
    internal fun `check and of two numbers`() =
        checkStackResult(listOf(Byte(1)), listOf(Byte(1)), Opcode.AND, listOf(Byte(1)))

    @Test
    internal fun `check or of two numbers`() =
        checkStackResult(listOf(Byte(0)), listOf(Byte(1)), Opcode.OR, listOf(Byte(1)))

    @Test
    internal fun `check xor of two numbers`() =
        checkStackResult(listOf(Byte(1)), listOf(Byte(1)), Opcode.XOR, listOf())

    @Test
    @Disabled("not implemented yet")
    internal fun `check not of a number`() =
        checkStackResult(listOf(listOf(Byte(1))), Opcode.NOT, listOf(Byte(0)))

    @Test
    internal fun `check byte from a number`() =
        checkStackResult(
            listOf(Byte(0)),
            Word.coerceFrom("0x62bff314b64217405db9e8200fb766263381bd0152f5186f0e87e46b2472d3ca").data,
            Opcode.BYTE,
            listOf(Byte(0x62))
        )

    @Test
    internal fun `check shift right than for two numbers`() =
        checkStackResult(listOf(Byte(0xFF), Byte(0x00)), listOf(Byte(8)), Opcode.SHR, listOf(Byte(0xFF)))

    @Test
    internal fun `check shift left than for two numbers`() =
        checkStackResult(listOf(Byte(0xFF)), listOf(Byte(8)), Opcode.SHL, listOf(Byte(0xFF), Byte(0x00)))

    @Test
    @Disabled("not implemented yet")
    internal fun `check sar of a number`(): Nothing = TODO()











    private fun checkStackResult(a: List<Byte>, b: List<Byte>, opcode: Opcode, expectedResult: List<Byte>) =
        checkStackResult(listOf(a, b), opcode, expectedResult)

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
    contractCode: List<Byte> = emptyList()
): ExecutionContext {
    val caller = Address("0xAABBCC")

    val call = CallContext(
        caller = caller,
        callData = emptyList(),
        contract = Contract(contractCode, Address("0xCCCCCC")),
        type = CallType.INITIAL,
        value = BigInteger.ZERO,
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
            origin = caller,
            gasPrice = BigInteger("200")
        ),
        coinBase = Address("0xFFEEDD"),
        callStack = listOf(call),
        evmState = EvmState(),
        logs = emptyList(),
        completed = false,
        lastReturnData = emptyList(),
        clock = Clock.systemUTC()
    )
}