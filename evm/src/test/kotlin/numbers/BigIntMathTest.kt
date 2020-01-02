package org.kevm.evm.numbers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class BigIntMathTest {

    @Test
    internal fun `check 1 divided by 32 is rounded up to 1`() {
        val result = BigIntMath.divRoundUp(BigInteger.ONE, BigInteger("32"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ONE)
    }

    @Test
    internal fun `check 0 divided by 32 is 0`() {
        val result = BigIntMath.divRoundUp(BigInteger.ZERO, BigInteger("32"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ZERO)
    }

    @Test
    internal fun `check 32 divided by 32 is 1`() {
        val result = BigIntMath.divRoundUp(BigInteger("32"), BigInteger("32"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ONE)
    }

    @Test
    internal fun `check 33 divided by 32 is 2`() {
        val result = BigIntMath.divRoundUp(BigInteger("33"), BigInteger("32"))

        Assertions.assertThat(result).isEqualTo(BigInteger("2"))
    }
}