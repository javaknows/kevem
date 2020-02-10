package collections

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.collections.BigIntegerIndexedList
import org.kevm.evm.model.Byte
import java.math.BigInteger

class BigIntegerIndexedListTest {

    @Test
    internal fun `write a byte then read a byte`() {
        val original = BigIntegerIndexedList(Byte.Zero)

        val written = original.write(BigInteger.ONE, Byte.One)

        assertThat(written[BigInteger.ONE]).isEqualTo(Byte.One)
    }

    @Test
    internal fun `write a byte then read some`() {
        val original = BigIntegerIndexedList(Byte.Zero)

        val written = original.write(BigInteger.ONE, Byte.One)

        assertThat(written.read(BigInteger.ZERO, 2)).isEqualTo(listOf(Byte.Zero, Byte.One))
    }

    @Test
    internal fun `write some bytes then read some`() {
        val original = BigIntegerIndexedList(Byte.Zero)

        val written = original.write(BigInteger.TWO, listOf(Byte.One, Byte("0x2"), Byte("0x3")))

        assertThat(written.read(BigInteger.ZERO, 5)).isEqualTo(listOf(Byte.Zero, Byte.Zero,Byte.One, Byte("0x2"), Byte("0x3") ))
    }

    @Test
    internal fun `check original and written are different`() {
        val original = BigIntegerIndexedList(Byte.Zero)

        val written = original.write(BigInteger.TWO, listOf(Byte.One, Byte("0x2"), Byte("0x3")))

        assertThat(System.identityHashCode(original)).isNotEqualTo(written)
    }

    @Test
    internal fun `check fromByteString creates correct map`() {
        val test = BigIntegerIndexedList.fromByteString("0x0102")

        assertThat( test ).isEqualTo(
            BigIntegerIndexedList(Byte.Zero).write(BigInteger.ZERO, listOf(Byte("0x01"), Byte("0x02")))
        )
    }

    @Test
    internal fun `check size is correct for empty list`() {
        val test = BigIntegerIndexedList.emptyByteList()

        assertThat( test.size() ).isEqualTo(BigInteger.ZERO)
    }

    @Test
    internal fun `check size is correct for list with one element`() {
        val test = BigIntegerIndexedList.fromBytes(listOf(Byte.Zero))

        assertThat( test.size() ).isEqualTo(BigInteger.ONE)
    }
}