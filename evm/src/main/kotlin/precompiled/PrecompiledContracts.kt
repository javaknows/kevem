package org.kevem.evm.precompiled

import org.kevem.evm.*
import org.kevem.evm.model.Byte
import org.kevem.evm.model.Byte.Companion.padRightToSize

import java.math.BigInteger

fun expmod(input: List<Byte>): List<Byte> {
    val sizeBytesLength = 32 * 3

    val safeSizeInput = coerceByteListToSizePre(input.take(sizeBytesLength), sizeBytesLength)
    val (bSize, eSize, nSize) = safeSizeInput.chunked(32).map { bytesToInt(it) }

    val b = bytesToBigInteger(padRightToSize(input.drop(sizeBytesLength).take(bSize), bSize))
    val e = bytesToBigInteger(padRightToSize(input.drop(bSize + sizeBytesLength).take(eSize), eSize))
    val n = bytesToBigInteger(padRightToSize(input.drop(bSize + eSize + sizeBytesLength).take(nSize), nSize))

    val result = when {
        b == BigInteger.ZERO && e == BigInteger.ZERO -> BigInteger.ONE
        n == BigInteger.ZERO -> BigInteger.ZERO
        else -> b.modPow(e, n)
    }

    return toBytes(result)
}

