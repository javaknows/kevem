package org.kevm.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevm.evm.crypto.bn128.Fq12

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class Fq12Test {

    @Test
    fun shouldBeTheSumWhenAdded() {
        val x= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val f= Fq12(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val fpx= Fq12(2, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        assertThat(x + f).isEqualTo(fpx)
    }

    @Test
    fun shouldBeOneWhenPointIsDividedByItself() {
        val f= Fq12(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val one= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertThat(f / f).isEqualTo(one)
    }

    @Test
    fun shouldBeALinearDivide() {
        val x= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val f= Fq12(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val one= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertThat((one / f) + (x / f)).isEqualTo((one + x) / f)
    }

    @Test
    fun shouldBeALinearMultiply() {
        val x= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val f= Fq12(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val one= Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertThat(((one * f) + (x * f))).isEqualTo(((one + x) * f))
    }
}
