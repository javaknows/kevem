package gas

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.EIP
import org.kevm.evm.Opcode
import org.kevm.evm.gas.BaseGasCostCalculator
import org.kevm.evm.gas.CallGasCostCalc
import org.kevm.evm.gas.PredefinedContractGasCostCalc
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import test.TestObjects
import java.math.BigInteger

class BaseGasCostCalculatorTest {

    private val underTest = BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc())

    @Test
    internal fun `check exp cost for byzantium hard fork`() {
        val expArgumants = Stack(
            listOf(
                toByteList("0x1"),
                toByteList("0x1"),
                toByteList("0x1")
            )
        )
        val eips = listOf(EIP.EIP160)

        val context = createExecutionContext(expArgumants, eips)

        val cost = underTest.baseCost(Opcode.EXP, context)

        assertThat(cost).isEqualTo(BigInteger("60"))
    }

    @Test
    internal fun `check exp cost for homestead hard fork`() {
        val expArgumants = Stack(
            listOf(
                toByteList("0x1"),
                toByteList("0x1"),
                toByteList("0x1")
            )
        )
        val eips = emptyList<EIP>()

        val context = createExecutionContext(expArgumants, eips)

        val cost = underTest.baseCost(Opcode.EXP, context)

        assertThat(cost).isEqualTo(BigInteger("20"))
    }

    private fun createExecutionContext(stack: Stack, eips: List<EIP>): ExecutionContext {
        val callContext = CallContext(
            Address("0x0"),
            emptyList(),
            CallType.CALL,
            BigInteger.ZERO,
            emptyList(),
            stack = stack
        )

        val context = ExecutionContext(
            currentBlock = TestObjects.block2,
            currentTransaction = Transaction(Address("0x0"), BigInteger.ONE),
            coinBase = Address("0x0"),
            callStack = listOf(callContext),
            features = Features(eips)
        )
        return context
    }
}