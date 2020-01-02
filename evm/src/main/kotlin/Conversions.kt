package org.kevm.evm

import org.kevm.evm.model.Byte
import java.math.BigInteger

fun toBytes(num: BigInteger): List<Byte> = ("0" + num.toString(16))
    .reversed()
    .chunked(2)
    .map { it.reversed() }
    .reversed()
    .dropWhile { it == "0" }
    .map { Byte(it) }

fun bytesToBigInteger(bytes: List<Byte>) = BigInteger(bytes.joinToString("") { it.toStringNoHexPrefix() }, 16)

fun bytesToString(bytes: List<Byte>) = hexPrefix(bytes.joinToString("") { it.toStringNoHexPrefix() })

fun Int.toStringHexPrefix() = hexPrefix(toString(16))

fun Long.toStringHexPrefix() = hexPrefix(toString(16))

fun stripHexPrefix(num: String) = num.replaceFirst("0x", "")

fun hexPrefix(num: String) = "0x$num"

fun coerceByteListToSize(data: List<Byte>, size: Int) = data.take(size) + Byte.Zero.repeat(size - data.size)

fun BigInteger.toStringHexPrefix() = hexPrefix(toString(16))

fun toByteList(bytes: String?): List<Byte> =
    if (bytes != null) {
        val noPrefixStack = bytes.replaceFirst("0x", "")
        val cleanStack = if (noPrefixStack.length % 2 == 0) noPrefixStack else "0$noPrefixStack"

        cleanStack.chunked(2).map { Byte(it) }
    } else emptyList()

