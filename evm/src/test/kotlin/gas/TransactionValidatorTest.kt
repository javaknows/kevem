package gas

import org.junit.Rule
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.gas.TransactionValidator
import org.kevm.evm.model.Address
import org.kevm.evm.model.TransactionMessage
import org.kevm.evm.model.WorldState
import test.TestObjects
import java.math.BigInteger

class TransactionValidatorTest {

    private val underTest = TransactionValidator()

    @Test
    internal fun `up front cost is valid when balance is equal`() {
        val worldState = TestObjects.worldState
        val transaction = TransactionMessage(
            Address("0x1"), Address("0x2"), BigInteger.ZERO, BigInteger.ZERO, BigInteger("21000"), emptyList(), BigInteger.ZERO
        )

        val valid = underTest.isValid(worldState, transaction)

        assertThat(valid).isTrue()
    }
}