package org.kevm.evm

import org.kevm.evm.model.Byte
import org.kevm.evm.model.Memory
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import java.math.BigInteger

class MemoryTest {

    @Test
    internal fun `check list of bytes can be set and retrieved`() {
        val memory = Memory()

        val updated = memory.write(BigInteger.ZERO, listOf(
            Byte(0),
            Byte(1),
            Byte(2)
        ))
        val bytes = updated.peek(BigInteger.ZERO, 3)

        assertThat(bytes).isEqualTo(listOf(
            Byte(0),
            Byte(1),
            Byte(2)
        ))
    }

    @Test
    internal fun `check retrieving 0 bytes returns empty list`() {
        val memory = Memory()

        val updated = memory.write(BigInteger.ZERO, listOf(
            Byte(0),
            Byte(1),
            Byte(2)
        ))
        val bytes = updated.peek(BigInteger.ZERO, 0)

        assertThat(bytes).isEmpty()
    }

    @Test
    internal fun `check arbitrary address can be set and retrieved and retrieved with array syntax`() {
        val memory = Memory()

        val updated = memory.write(BigInteger("1000"), listOf(Byte(0xFF)))

        assertThat(updated.peek(BigInteger("1000"))).isEqualTo(Byte(0xFF))
    }
}