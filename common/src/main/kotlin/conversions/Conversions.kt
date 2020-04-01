package org.kevem.common.conversions

import org.kevem.common.Byte
import java.math.BigInteger

fun toBytes(num: BigInteger): List<Byte> = ("0" + num.toString(16))
    .reversed()
    .chunked(2)
    .map { it.reversed() }
    .reversed()
    .dropWhile { it == "0" }
    .map { Byte(it) }

fun bytesToBigInteger(bytes: List<Byte>) =
    if (bytes.isEmpty()) BigInteger.ZERO
    else BigInteger(bytes.joinToString("") { it.toStringNoHexPrefix() }, 16)

fun bytesToInt(bytes: List<Byte>) = BigInteger(bytes.joinToString("") { it.toStringNoHexPrefix() }, 16).toInt()

fun bytesToString(bytes: List<Byte>) =
    hexPrefix(bytes.joinToString("") { it.toStringNoHexPrefix() })

fun Int.toStringHexPrefix() = hexPrefix(toString(16))

fun Long.toStringHexPrefix() = hexPrefix(toString(16))

fun stripHexPrefix(num: String) = num.replaceFirst("0x", "")

fun hexPrefix(num: String) = "0x$num"

fun coerceByteListToSize(data: List<Byte>, size: Int) = data.take(size) + Byte.Zero.repeat(size - data.size)

fun coerceByteListToSizePre(data: List<Byte>, size: Int) = Byte.Zero.repeat(size - data.size) + data.take(size)

fun BigInteger.toStringHexPrefix() = hexPrefix(toString(16))

fun toByteList(bytes: String?): List<Byte> =
    if (bytes != null) {
        val noPrefixStack = bytes.replaceFirst("0x", "")
        val cleanStack = if (noPrefixStack.length % 2 == 0) noPrefixStack else "0$noPrefixStack"

        cleanStack.chunked(2).map { Byte(it) }
    } else emptyList()

fun isEmptyHex(to: String?): Boolean = to == null || to == "0x" || to == ""

fun toBigInteger(number: String) =
    if (number.startsWith("0x")) BigInteger(cleanHexNumber(number), 16)
    else BigInteger(number)

fun toBigIntegerOr(number: String?, default: BigInteger) = when {
    number == null -> default
    number.startsWith("0x") -> BigInteger(cleanHexNumber(number), 16)
    else -> BigInteger(number)
}

fun toBigIntegerOrZero(number: String?) = toBigIntegerOr(number, BigInteger.ZERO)

fun toBigIntegerOrNull(number: String?) = when {
    number == null -> null
    number.startsWith("0x") -> BigInteger(cleanHexNumber(number), 16)
    else -> BigInteger(number)
}

fun toInt(number: String) = toBigInteger(number).toInt()

private fun cleanHexNumber(number: String) = number.replaceFirst("0x0+", "0x0").replaceFirst("0x", "")
