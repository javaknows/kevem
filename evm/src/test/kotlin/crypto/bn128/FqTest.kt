package org.kevem.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevem.evm.crypto.bn128.FieldElement
import org.kevem.evm.crypto.bn128.Fq
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class FqTest {

    @Test
    fun shouldBeValidWhenLessThanFieldModulus() {
        val fq = Fq(
            FieldElement.FIELD_MODULUS.subtract(BigInteger.ONE)
        )
        assertThat(fq.isValid).isTrue()
    }

    @Test
    fun shouldBeInvalidWhenEqualToFieldModulus() {
        val fq =
            Fq(FieldElement.FIELD_MODULUS)
        assertThat(fq.isValid).isFalse()
    }

    @Test
    fun shouldBeInvalidWhenGreaterThanFieldModulus() {
        val fq = Fq(
            FieldElement.FIELD_MODULUS.add(BigInteger.ONE)
        )
        assertThat(fq.isValid).isFalse()
    }

    @Test
    fun shouldBeAbleToAddNumbersWithoutOverflow() {
        val a = Fq(1)
        val b = Fq(1)
        val c: Fq = a + b
        assertThat(c).isEqualTo(Fq(2))
    }

    @Test
    fun shouldBeAbleToAddNumbersWithOverflow() {
        val a = Fq(
            FieldElement.FIELD_MODULUS.subtract(BigInteger.ONE)
        )
        val b = Fq(2)
        val c: Fq = a + b
        assertThat(c).isEqualTo(Fq.one())
    }

    @Test
    fun shouldBeAbleToSubtractNumbers() {
        val a = Fq(5)
        val b = Fq(3)
        val c: Fq = a - b
        assertThat(c).isEqualTo(Fq(2))
    }

    @Test
    fun shouldBeAbleToMultiplyNumbersWithoutOverflow() {
        val a = Fq(2)
        val b = Fq(3)
        val c: Fq = a * b
        assertThat(c).isEqualTo(Fq(6))
    }

    @Test
    fun shouldBeAbleToMultiplyNumbersWithOverflow() { // FIELD_MODULUS is odd so (FIELD_MODULUS + 1) / 2 => FIELD_MODULUS / 2 + 1 (with int types).
        val a: Fq =
            Fq(
                FieldElement.FIELD_MODULUS.add(
                    BigInteger.ONE
                ).divide(BigInteger.valueOf(2))
            )
        val b = Fq(2)
        val c: Fq = a * b
        assertThat(c).isEqualTo(Fq.one())
    }

    @Test
    fun shouldNegatePositiveNumberToNegative() {
        val a = Fq(1)
        assertThat(-a).isEqualTo(Fq(-1))
    }

    @Test
    fun shouldNegateNegativeNumberToPositive() {
        val a = Fq(-1)
        assertThat(-a).isEqualTo(Fq(1))
    }

    @Test
    fun shouldBeProductWhenMultiplied() {
        val fq2 = Fq(2)
        val fq4 = Fq(4)
        assertThat((fq2 * fq2)).isEqualTo(fq4)
    }

    @Test
    fun shouldBeALinearDivide() {
        val fq2 = Fq(2)
        val fq7 = Fq(7)
        val fq9 = Fq(9)
        val fq11 = Fq(11)
        assertThat((fq2 / fq7) + (fq9 / fq7)).isEqualTo(fq11 / fq7)
    }

    @Test
    fun shouldBeALinearMultiply() {
        val fq2 = Fq(2)
        val fq7 = Fq(7)
        val fq9 = Fq(9)
        val fq11 = Fq(11)
        assertThat(((fq2 * fq7) + (fq9 * fq7))).isEqualTo(fq11 * fq7)
    }

    @Test
    fun shouldEqualItselWhenRaisedToFieldModulus() {
        val fq9 = Fq(9)
        assertThat(fq9.power(FieldElement.FIELD_MODULUS)).isEqualTo(fq9)
    }
}
