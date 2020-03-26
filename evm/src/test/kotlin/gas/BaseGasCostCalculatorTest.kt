package gas

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevem.evm.EIP
import org.kevem.evm.Opcode
import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.evm.gas.BaseGasCostCalculator
import org.kevem.evm.gas.CallGasCostCalc
import org.kevem.evm.gas.PredefinedContractGasCostCalc
import org.kevem.evm.model.*
import org.kevem.evm.toByteList
import test.TestObjects
import java.math.BigInteger

class BaseGasCostCalculatorTest {

    private val underTest = BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc())

    @Test
    internal fun `check exp cost for byzantium hard fork`() {
        val expArgs = Stack(listOf(toByteList("0x1"), toByteList("0x1"), toByteList("0x1")))
        val eips = listOf(EIP.EIP160)
        val context = createExecutionContext(expArgs, eips)

        val cost = underTest.baseCost(Opcode.EXP, context)

        assertThat(cost).isEqualTo(BigInteger("60"))
    }

    @Test
    internal fun `check exp cost for homestead hard fork`() {
        val expArgumants = Stack(listOf(toByteList("0x1"), toByteList("0x1"), toByteList("0x1")))
        val eips = emptyList<EIP>()
        val context = createExecutionContext(expArgumants, eips)

        val cost = underTest.baseCost(Opcode.EXP, context)

        assertThat(cost).isEqualTo(BigInteger("20"))
    }

    @Test
    internal fun `check suicide cost with EIP150 when account exists`() {
        val expArgs = Stack(listOf(toByteList("0x1")))
        val eips = listOf(EIP.EIP150)
        val context = createExecutionContext(expArgs, eips, Accounts(listOf(Account(Address("0x1")))))

        val cost = underTest.baseCost(Opcode.SUICIDE, context)

        assertThat(cost).isEqualTo(BigInteger("5000"))
    }

    @Test
    internal fun `check suicide cost with EIP150 when account does not exists`() {
        val expArgs = Stack(listOf(toByteList("0x1")))
        val eips = listOf(EIP.EIP150)
        val context = createExecutionContext(expArgs, eips, Accounts(emptyList()))

        val cost = underTest.baseCost(Opcode.SUICIDE, context)

        assertThat(cost).isEqualTo(BigInteger("30000"))
    }

    @Test
    internal fun `check suicide cost for homestead hard fork when account exists`() {
        val expArgs = Stack(listOf(toByteList("0x1")))
        val eips = emptyList<EIP>()
        val context = createExecutionContext(expArgs, eips, Accounts(listOf(Account(Address("0x1")))))

        val cost = underTest.baseCost(Opcode.SUICIDE, context)

        assertThat(cost).isEqualTo(BigInteger("0"))
    }

    @Test
    internal fun `check suicide cost for homestead hard fork when account does not exists`() {
        val expArgs = Stack(listOf(toByteList("0x1")))
        val eips = emptyList<EIP>()
        val context = createExecutionContext(expArgs, eips, Accounts(emptyList()))

        val cost = underTest.baseCost(Opcode.SUICIDE, context)

        assertThat(cost).isEqualTo(BigInteger("0"))
    }

    private fun createExecutionContext(
        stack: Stack = Stack(),
        eips: List<EIP> = emptyList(),
        accounts: Accounts = Accounts()
    ): ExecutionContext {
        val callContext = CallContext(
            Address("0x0"),
            BigIntegerIndexedList.emptyByteList(),
            CallType.CALL,
            BigInteger.ZERO,
            BigIntegerIndexedList.emptyByteList(),
            stack = stack
        )

        val context = ExecutionContext(
            currentBlock = TestObjects.block2,
            currentTransaction = Transaction(Address("0x0"), BigInteger.ONE),
            callStack = listOf(callContext),
            accounts = accounts,
            features = Features(eips),
            config = EvmConfig(
                chainId = BigInteger.TWO,
                coinbase = Address("0xFFEEDD")
            )
        )
        return context
    }
}