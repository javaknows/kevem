package org.kevem.evm.crypto

import org.bouncycastle.crypto.Digest
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.Pack

/**
 * Implementation of Blake2b F cryptographic hash function.
 *
 * Adapted from the (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/blob/master/crypto/src/main/java/org/hyperledger/besu/crypto/Blake2bfMessageDigest.java
 */
private val MESSAGE_LENGTH_BYTES = 213

class Blake2bfMessageDigest : BCMessageDigest(Blake2bfDigest()) {

    class Blake2bfDigest(
        private val buffer: ByteArray,
        private var bufferPos: Int,
        private val h: LongArray,
        private val m: LongArray,
        private val t: LongArray,
        private var f: Boolean,
        private var rounds: Long,
        private val v: LongArray
    ) : Digest {

        private val IV = longArrayOf(
            0x6a09e667f3bcc908L,
            -0x4498517a7b3558c5L,
            0x3c6ef372fe94f82bL,
            -0x5ab00ac5a0e2c90fL,
            0x510e527fade682d1L,
            -0x64fa9773d4c193e1L,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L
        )

        private val PRECOMPUTED = arrayOf(
            byteArrayOf(0, 2, 4, 6, 1, 3, 5, 7, 8, 10, 12, 14, 9, 11, 13, 15),
            byteArrayOf(14, 4, 9, 13, 10, 8, 15, 6, 1, 0, 11, 5, 12, 2, 7, 3),
            byteArrayOf(11, 12, 5, 15, 8, 0, 2, 13, 10, 3, 7, 9, 14, 6, 1, 4),
            byteArrayOf(7, 3, 13, 11, 9, 1, 12, 14, 2, 5, 4, 15, 6, 10, 0, 8),
            byteArrayOf(9, 5, 2, 10, 0, 7, 4, 15, 14, 11, 6, 3, 1, 12, 8, 13),
            byteArrayOf(2, 6, 0, 8, 12, 10, 11, 3, 4, 7, 15, 1, 13, 5, 14, 9),
            byteArrayOf(12, 1, 14, 4, 5, 15, 13, 10, 0, 6, 9, 8, 7, 3, 2, 11),
            byteArrayOf(13, 7, 12, 3, 11, 14, 1, 9, 5, 15, 8, 2, 0, 4, 6, 10),
            byteArrayOf(6, 14, 11, 0, 15, 9, 3, 8, 12, 13, 1, 10, 2, 7, 4, 5),
            byteArrayOf(10, 8, 7, 1, 2, 4, 6, 5, 15, 9, 3, 13, 11, 14, 12, 0)
        )

        private val DIGEST_LENGTH = 64

        constructor() : this(
            buffer = ByteArray(MESSAGE_LENGTH_BYTES),
            bufferPos = 0,
            h = LongArray(8),
            m = LongArray(16),
            t = LongArray(2),
            f = false,
            rounds = 12,
            v = LongArray(16)
        )

        override fun getAlgorithmName(): String = "BLAKE2f"

        override fun getDigestSize(): Int = DIGEST_LENGTH

        override fun update(inData: Byte) {
            require(bufferPos != MESSAGE_LENGTH_BYTES)
            buffer[bufferPos] = inData
            bufferPos++
            if (bufferPos == MESSAGE_LENGTH_BYTES) {
                initialize()
            }
        }

        override fun update(inData: ByteArray?, offset: Int, len: Int) {
            if (inData == null || len == 0) {
                return
            }
            require(len <= MESSAGE_LENGTH_BYTES - bufferPos) {
                "Attempting to update buffer with $len byte(s) but there is ${(MESSAGE_LENGTH_BYTES - bufferPos)} byte(s) left to fill"
            }
            System.arraycopy(inData, offset, buffer, bufferPos, len)
            bufferPos += len
            if (bufferPos == MESSAGE_LENGTH_BYTES) {
                initialize()
            }
        }

        override fun doFinal(out: ByteArray, offset: Int): Int {
            check(bufferPos == 213) { "The buffer must be filled with 213 bytes" }
            compress()
            for (i in h.indices) {
                System.arraycopy(Pack.longToLittleEndian(h[i]), 0, out, i * 8, 8)
            }
            reset()
            return 0
        }

        override fun reset() {
            bufferPos = 0
            Arrays.fill(buffer, 0.toByte())
            Arrays.fill(h, 0)
            Arrays.fill(m, 0.toLong())
            Arrays.fill(t, 0)
            f = false
            rounds = 12
            Arrays.fill(v, 0)
        }

        private fun initialize() {
            rounds = Integer.toUnsignedLong(bytesToInt(java.util.Arrays.copyOfRange(buffer, 0, 4)))
            for (i in h.indices) {
                val offset = 4 + i * 8
                h[i] = bytesToLong(java.util.Arrays.copyOfRange(buffer, offset, offset + 8))
            }
            for (i in 0..15) {
                val offset = 68 + i * 8
                m[i] = bytesToLong(java.util.Arrays.copyOfRange(buffer, offset, offset + 8))
            }
            t[0] = bytesToLong(java.util.Arrays.copyOfRange(buffer, 196, 204))
            t[1] = bytesToLong(java.util.Arrays.copyOfRange(buffer, 204, 212))
            f = buffer[212] != 0.toByte()
        }

        private fun bytesToInt(bytes: ByteArray): Int = Pack.bigEndianToInt(bytes, 0)

        private fun bytesToLong(bytes: ByteArray): Long = Pack.littleEndianToLong(bytes, 0)

        private fun compress() {
            val t0 = t[0]
            val t1 = t[1]
            System.arraycopy(h, 0, v, 0, 8)
            System.arraycopy(IV, 0, v, 8, 8)
            v[12] = v[12] xor t0
            v[13] = v[13] xor t1
            if (f) {
                v[14] = v[14] xor -0x1L
            }
            for (j in 0 until rounds) {
                val s = PRECOMPUTED[(j % 10).toInt()]
                mix(m[s[0].toInt()], m[s[4].toInt()], 0, 4, 8, 12)
                mix(m[s[1].toInt()], m[s[5].toInt()], 1, 5, 9, 13)
                mix(m[s[2].toInt()], m[s[6].toInt()], 2, 6, 10, 14)
                mix(m[s[3].toInt()], m[s[7].toInt()], 3, 7, 11, 15)
                mix(m[s[8].toInt()], m[s[12].toInt()], 0, 5, 10, 15)
                mix(m[s[9].toInt()], m[s[13].toInt()], 1, 6, 11, 12)
                mix(m[s[10].toInt()], m[s[14].toInt()], 2, 7, 8, 13)
                mix(m[s[11].toInt()], m[s[15].toInt()], 3, 4, 9, 14)
            }
            // update h:
            for (offset in h.indices) {
                h[offset] = h[offset] xor v[offset] xor v[offset + 8]
            }
        }

        private fun mix(a: Long, b: Long, i: Int, j: Int, k: Int, l: Int) {
            v[i] += a + v[j]
            v[l] = java.lang.Long.rotateLeft(v[l] xor v[i], -32)
            v[k] += v[l]
            v[j] = java.lang.Long.rotateLeft(v[j] xor v[k], -24)
            v[i] += b + v[j]
            v[l] = java.lang.Long.rotateLeft(v[l] xor v[i], -16)
            v[k] += v[l]
            v[j] = java.lang.Long.rotateLeft(v[j] xor v[k], -63)
        }
    }
}
