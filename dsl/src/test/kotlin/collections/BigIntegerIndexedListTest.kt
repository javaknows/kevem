package collections

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.collections.BigIntegerIndexedByteList
import org.kevm.evm.model.Byte
import java.math.BigInteger

class BigIntegerIndexedListTest {

    @Test
    internal fun `write a byte then read a byte`() {
        val original = BigIntegerIndexedByteList()

        val written = original.write(BigInteger.ONE, Byte.One)

        assertThat(written[BigInteger.ONE]).isEqualTo(Byte.One)
    }

    @Test
    internal fun `write a byte then read some`() {
        val original = BigIntegerIndexedByteList()

        val written = original.write(BigInteger.ONE, Byte.One)

        assertThat(written.read(BigInteger.ZERO, 2)).isEqualTo(listOf(Byte.Zero, Byte.One))
    }

    @Test
    internal fun `write some bytes then read some`() {
        val original = BigIntegerIndexedByteList()

        val written = original.write(BigInteger.TWO, listOf(Byte.One, Byte("0x2"), Byte("0x3")))

        assertThat(written.read(BigInteger.ZERO, 5)).isEqualTo(listOf(Byte.Zero, Byte.Zero,Byte.One, Byte("0x2"), Byte("0x3") ))
    }

    @Test
    internal fun `check original and written are different`() {
        val original = BigIntegerIndexedByteList()

        val written = original.write(BigInteger.TWO, listOf(Byte.One, Byte("0x2"), Byte("0x3")))

        assertThat(System.identityHashCode(original)).isNotEqualTo(written)
    }
}