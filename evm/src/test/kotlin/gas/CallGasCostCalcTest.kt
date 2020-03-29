package gas

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevem.evm.collections.BigIntegerIndexedList

import org.kevem.evm.gas.CallGasCostCalc
import org.kevem.evm.model.*
import java.math.BigInteger
import java.time.Instant

class CallGasCostCalcTest {

    private val underTest = CallGasCostCalc()

    private val anyBlock = Block(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, Instant.MIN)

    private val anyTransaction = Transaction(Address("0x0"), BigInteger.ONE)

    @Test
    internal fun `call with non-zero gas provided for call receives 2300 call stipend`() {
        val executionContext = ExecutionContext(anyBlock, anyTransaction, callStack = listOf(
            CallContext(Address("0x1"), BigIntegerIndexedList.emptyByteList(), CallType.CALL, BigInteger.ZERO, BigIntegerIndexedList.emptyByteList())
        ))

        val (_, callGas) = underTest.calcCallCostAndCallGas(
            BigInteger.ONE,
            Address("0xAAAAAA"),
            BigInteger.ZERO,
            executionContext
        )

        assertThat(callGas).isEqualTo(BigInteger("2300"))
    }
}