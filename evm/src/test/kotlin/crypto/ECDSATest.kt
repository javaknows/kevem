package org.kevem.evm.crypto

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevem.evm.bytesToString
import org.kevem.evm.crypto.ecdsarecover
import org.kevem.evm.toByteList
import org.kevem.common.Byte

class ECDSATest {

    @Test
    internal fun `ecdsarecover can recover valid signature`() {
        val data = toByteList("0x0049872459827432342344987245982743234234498724598274323423429943000000000000000000000000000000000000000000000000000000000000001be8359c341771db7f9ea3a662a1741d27775ce277961470028e054ed3285aab8e31f63eaac35c4e6178abbc2a1073040ac9bbb0b67f2bc89a2e9593ba9abe8c53")

        val result = bytesToString(ecdsarecover(data))

        assertThat(result).isEqualTo("0x0c65a9d9ffc02c7c99e36e32ce0f950c7804ceda")
    }

    @Test
    internal fun `ecdsarecover is empty list for invalid signature`() {
        val data = toByteList("0x0049872459827432342344987245982743234234498724598274323423429943000000000000000000000000000000000000000000000000000000000000000be8359c341771db7f9ea3a662a1741d27775ce277961470028e054ed3285aab8e31f63eaac35c4e6178abbc2a1073040ac9bbb0b67f2bc89a2e9593ba9abe8c53")

        val result = ecdsarecover(data)

        assertThat(result).isEqualTo(emptyList<Byte>())
    }
}