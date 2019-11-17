package com.gammadex.kevin.evm.numbers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class LogTest {

    @Test
    internal fun `check log_256 of 256 is 1`() {
        val result = logn(BigInteger("256"), BigInteger("256"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ONE)
    }

    @Test
    internal fun `check log_256 of 1 is 0`() {
        val result = logn(BigInteger("1"), BigInteger("256"))

        Assertions.assertThat(result).isEqualTo(BigInteger.ZERO)
    }
}