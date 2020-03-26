package org.kevem.evm.numbers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class LogTest {

    @Test
    internal fun `check log256(256) is 1`() {
        val result = log256(BigInteger("256"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ONE)
    }

    @Test
    internal fun `check log256(65536) is 2`() {
        val result = log256(BigInteger("65536"))

        Assertions.assertThat(result).isEqualTo(BigInteger("2"))
    }

    @Test
    internal fun `check log256(65537) is 2`() {
        val result = log256(BigInteger("65537"))

        Assertions.assertThat(result).isEqualTo(BigInteger("2"))
    }

    @Test
    internal fun `check log256(1) is 0`() {
        val result = log256(BigInteger("1"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ZERO)
    }
}