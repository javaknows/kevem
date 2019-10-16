package com.gammadex.kevin

import com.gammadex.kevin.model.Byte
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import java.math.BigInteger

class ConversionsTest {

    @Test
    internal fun `check big integer to bytes`() {
        val bytes = toBytes(BigInteger.valueOf(257))

        assertThat(bytes).isEqualTo(listOf(Byte(0x01), Byte(0x01)))
    }

    @Test
    internal fun `check bytes to big integer`() {
        val bigInt = bytesToBigInteger(listOf(Byte(0x01), Byte(0x01)))

        assertThat(bigInt).isEqualTo(BigInteger.valueOf(257))
    }
}