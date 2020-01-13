package org.kevm.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevm.evm.crypto.bn128.FieldElement
import org.kevm.evm.crypto.bn128.Fq2
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class Fq2Test {
    @Test
    fun shouldBeTheSumWhenAdded() {
        val x = Fq2(1, 0)
        val f = Fq2(1, 2)
        val fpx = Fq2(2, 2)
        assertThat(x + f).isEqualTo(fpx)
    }

    @Test
    fun shouldBeOneWhenPointIsDividedByItself() {
        val f = Fq2(1, 2)
        val one = Fq2(1, 0)
        assertThat(f / f).isEqualTo(one)
    }

    @Test
    fun shouldBeALinearDivide() {
        val x = Fq2(1, 0)
        val f = Fq2(1, 2)
        val one = Fq2(1, 0)
        assertThat((one / f) + (x /f))
            .isEqualTo((one + x) / f)
    }

    @Test
    fun shouldBeALinearMultiply() {
        val x = Fq2(1, 0)
        val f = Fq2(1, 2)
        val one = Fq2(1, 0)
        assertThat(((one * f) + (x * f)))
            .isEqualTo((one + x) * f)
    }

    @Test
    fun shouldEqualOneWhenRaisedToFieldModulus() {
        val x = Fq2(1, 0)
        val one = Fq2(1, 0)
        assertThat(x.power(FieldElement.FIELD_MODULUS.pow(2).subtract(BigInteger.ONE))).isEqualTo(one)
    }

    @Test
    fun shouldEqualsOneWhenPowerIsZero() {
        val x = Fq2(2, 0)

        assertThat(x.power(0)).isEqualTo(Fq2(1, 0))
    }
}


