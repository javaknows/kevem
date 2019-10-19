package com.gammadex.kevin.evm

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AddressGeneratorTest {

    private val underTest = DefaultAddressGenerator()

    @Test
    fun `next address is generated`() {
        val generated = underTest.nextAddress()

        val addressString = generated.toString()
        assertThat(addressString.length).isEqualTo(42)
        assertThat(addressString).startsWith("0x")
    }

    @Test
    fun `two sequential addresses are different`() {
        val generated = underTest.nextAddress()
        val generated2 = underTest.nextAddress()

        assertThat(generated).isNotEqualTo(generated2)
    }
}