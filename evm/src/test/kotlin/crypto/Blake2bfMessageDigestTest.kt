package org.kevm.evm.crypto

import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.util.Pack
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Adapted from the (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/blob/master/crypto/src/test/java/org/hyperledger/besu/crypto/Blake2bfMessageDigestTest.java
 */
class Blake2bfMessageDigestTest {

    // output when input is all 0
    private val BLAKE2F_ALL_ZERO = byteArrayOf(
        8, -55, -68, -13, 103, -26, 9, 106, 59, -89, -54, -124, -123, -82, 103, -69, 43, -8, -108,
        -2, 114, -13, 110, 60, -15, 54, 29, 95, 58, -11, 79, -91, -47, -126, -26, -83, 127, 82, 14,
        81, 31, 108, 62, 43, -116, 104, 5, -101, 107, -67, 65, -5, -85, -39, -125, 31, 121, 33, 126,
        19, 25, -51, -32, 91
    )
    // output when input is all 0 for 4294967295 rounds
    private val BLAKE2F_ALL_ZERO_NEGATIVE_ROUNDS = byteArrayOf(
        -111, -99, -124, 115, 29, 109, 127, 118, 18, 21, 75, -89, 60, 35, 112, 81, 110, 78, -8, 40,
        -102, 19, -73, -97, 57, 69, 69, -89, 83, 66, 124, -43, -92, 78, 115, 115, 117, 123, -105,
        -25, 25, -74, -1, -94, -127, 14, 87, 123, -26, 84, -75, -82, -78, 54, 48, -125, 38, -58, 7,
        -61, 120, -93, -42, -38
    )

    private var messageDigest: Blake2bfMessageDigest = Blake2bfMessageDigest()

    @BeforeEach
    fun setUp() {
        messageDigest = Blake2bfMessageDigest()
    }

    @Test
    fun digestIfUpdatedCorrectlyWithBytes() {
        (0..212).forEach { _ ->
            messageDigest.update(0.toByte())
        }
        assertThat(messageDigest.digest()).isEqualTo(BLAKE2F_ALL_ZERO)
    }

    @Test
    fun digestIfUpdatedCorrectlyWithByteArray() {
        val update = ByteArray(213)
        messageDigest.update(update, 0, 213)
        assertThat(messageDigest.digest()).isEqualTo(BLAKE2F_ALL_ZERO)
    }

    @Test
    fun digestIfUpdatedCorrectlyMixed() {
        val update = ByteArray(213)
        messageDigest.update(0.toByte())
        messageDigest.update(update, 2, 211)
        messageDigest.update(0.toByte())
        assertThat(messageDigest.digest()).isEqualTo(BLAKE2F_ALL_ZERO)
    }

    @Test
    @Disabled("very slow")
    fun digestWithMaxRounds() { // equal to unsigned int max value (4294967295, or signed -1)
        val rounds = Pack.intToBigEndian(Int.MIN_VALUE)
        messageDigest.update(rounds, 0, 4)
        messageDigest.update(ByteArray(213), 0, 209)
        assertThat(messageDigest.digest()).isEqualTo(BLAKE2F_ALL_ZERO_NEGATIVE_ROUNDS)
    }

    @Test
    fun throwsIfBufferUpdatedWithLessThat213Bytes() {
        val e: IllegalStateException = assertThrows {
            (0..211).forEach { _ ->
                messageDigest.update(0.toByte())
            }
            messageDigest.digest()
        }
    }

    @Test
    fun throwsIfBufferUpdatedWithMoreThat213Bytes() {
        val e: IllegalArgumentException = assertThrows {
            (0..213).forEach { _ ->
                messageDigest.update(0.toByte())
            }
        }
    }

    @Test
    fun throwsIfBufferUpdatedLargeByteArray() {
        val e: IllegalArgumentException = assertThrows {
            val update = ByteArray(213)
            messageDigest.update(0.toByte())
            messageDigest.update(update, 0, 213)
        }
    }

    @Test
    fun throwsIfEmptyBufferUpdatedLargeByteArray() {
        val e: IllegalArgumentException = assertThrows {
            val update = ByteArray(214)
            messageDigest.update(update, 0, 214)
        }
    }
}
