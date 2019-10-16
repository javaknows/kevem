package com.gammadex.kevin.lang

import com.gammadex.kevin.model.Byte
import com.gammadex.kevin.model.Word
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import java.math.BigInteger

class WordTest {

    @Test
    internal fun `check word can't be over 32 bytes long`() {
        val tooManyBytes = Byte(0xFF).repeat(33)

        assertThrows(IllegalArgumentException::class.java) { Word(tooManyBytes) }
    }

    @Test
    internal fun `check string representation is correct`() {
        val data = (0..31).map{ Byte(it) }

        val string = Word(data).toString()

        assertThat(string).isEqualTo("0x000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")
    }

    @Test
    internal fun `check bytes that are too long can be coerced to Word`() {
        val data = (0..33).map{ Byte(it) }

        val word = Word.coerceFrom(data)

        assertThat(word).isEqualTo(Word((2..33).map { Byte(it) }))
    }

    @Test
    internal fun `check bytes that are too short can be coerced to Word`() {
        val data = Byte(0x01).repeat(2)

        val word = Word.coerceFrom(data)

        assertThat(word).isEqualTo(
            Word(
                Byte.Zero.repeat(30) + Byte(
                    0x01
                ).repeat(2)
            )
        )
    }

    @Test
    internal fun `check conversion to big integer`() {
        val word = Word.coerceFrom(listOf(Byte(0x01)))

        val bigInteger = word.toBigInt()

        assertThat(bigInteger).isEqualTo(BigInteger.ONE)
    }
}