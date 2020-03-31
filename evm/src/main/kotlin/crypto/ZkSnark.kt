package org.kevem.evm.crypto

import org.kevem.evm.bytesToBigInteger
import org.kevem.evm.crypto.bn128.*
import org.kevem.evm.lang.component6
import org.kevem.common.Byte
import org.kevem.common.Byte.Companion.padRightToMultipleOf
import org.kevem.common.Byte.Companion.padRightToSize
import org.kevem.evm.model.Word

fun bnAdd(input: List<Byte>): List<Byte> {
    val safeInput = padRightToSize(input, 128, Byte.Zero)
    val (x1, y1, x2, y2) = safeInput.chunked(32).map { bytesToBigInteger(it) }

    val p1 = AltBn128Point(Fq(x1), Fq(y1))
    val p2 = AltBn128Point(Fq(x2), Fq(y2))

    return if (p1.isOnCurve && p2.isOnCurve) {
        val sum = p1 + p2
        Word.coerceFrom(sum.x.n).data + Word.coerceFrom(sum.y.n).data
    } else {
        emptyList()
    }
}

fun bnMul(input: List<Byte>): List<Byte> {
    val safeInput = padRightToSize(input, 96, Byte.Zero)
    val (x, y, n) = safeInput.chunked(32).map { bytesToBigInteger(it) }

    val p = AltBn128Point(Fq(x), Fq(y))

    return if (p.isOnCurve) {
        val product = p * n
        Word.coerceFrom(product.x.n).data + Word.coerceFrom(product.y.n).data
    } else {
        emptyList()
    }
}

fun snarkV(input: List<Byte>): List<Byte> {
    val safeInput = padRightToMultipleOf(input, 192, Byte.Zero)

    val (points, fq2Points) = safeInput
        .chunked(192)
        .fold(Pair(emptyList<AltBn128Point>(), emptyList<AltBn128Fq2Point>())) { acc, bytes ->
            val (p1X, p1Y, p2Xi, p2Xr, p2Yi, p2Yr) = bytes.chunked(32).map { bytesToBigInteger(it) }

            val p1 = AltBn128Point(Fq(p1X), Fq(p1Y))
            val p2 = AltBn128Fq2Point(Fq2(p2Xr, p2Xi), Fq2(p2Yr, p2Yi))

            acc.copy(
                first = acc.first + p1,
                second = acc.second + p2
            )
        }

    val valid = points.all { it.isOnCurve } && fq2Points.all { it.isOnCurve && it.isInGroup }

    return if (!valid) {
        emptyList()
    } else {
        val product = (points zip fq2Points).fold(Fq12.one()) { acc, pair ->
            val (p, fq2) = pair
            acc * AltBn128Fq12Pairer.pair(p, fq2)
        }

        if (AltBn128Fq12Pairer.finalize(product) == Fq12.one())
            Word.One.data
        else
            Word.Zero.data
    }
}