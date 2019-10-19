package com.gammadex.kevin.evm.numbers

import com.gammadex.kevin.evm.model.Word
import com.gammadex.kevin.evm.numbers.fromTwosComplement
import com.gammadex.kevin.evm.numbers.toTwosComplement
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class TwosComplementTest {

    @Test
    internal fun `check from zero`() {
        val result = fromTwosComplement(Word.coerceFrom(0b0000_0000_0000_0000_0000_0000_0000_0000, 4), 32)

        assertThat(result).isEqualTo(BigInteger.ZERO)
    }

    @Test
    internal fun `check from 1`() {
        val result = fromTwosComplement(Word.coerceFrom(0b0000_0000_0000_0000_0000_0000_0000_0001, 4), 32)

        assertThat(result).isEqualTo(BigInteger("1"))
    }

    @Test
    internal fun `check from -1`() {
        val result = fromTwosComplement(Word.coerceFrom(0b1111_1111_1111_1111_1111_1111_1111_1111, 4), 32)

        assertThat(result).isEqualTo(BigInteger("-1"))
    }

    @Test
    internal fun `check from -2`() {
        val result = fromTwosComplement(Word.coerceFrom(0b1111_1111_1111_1111_1111_1111_1111_1110, 4), 32)

        assertThat(result).isEqualTo(BigInteger("-2"))
    }

    @Test
    internal fun `check to zero`() {
        val result = toTwosComplement(BigInteger.ZERO, 32)

        assertThat(result).isEqualTo(Word.coerceFrom(0b0000_0000_0000_0000_0000_0000_0000_0000, 4))
    }

    @Test
    internal fun `check to 1`() {
        val result = toTwosComplement(BigInteger("1"), 32)

        assertThat(result).isEqualTo(Word.coerceFrom(0b0000_0000_0000_0000_0000_0000_0000_0001, 4))
    }

    @Test
    internal fun `check to -1`() {
        val result = toTwosComplement(BigInteger("-1"), 32)

        assertThat(result).isEqualTo(Word.coerceFrom(0b1111_1111_1111_1111_1111_1111_1111_1111, 4))
    }

    @Test
    internal fun `check to -2`() {
        val result = toTwosComplement(BigInteger("-2"), 32)

        assertThat(result).isEqualTo(Word.coerceFrom(0b1111_1111_1111_1111_1111_1111_1111_1110, 4))
    }

}