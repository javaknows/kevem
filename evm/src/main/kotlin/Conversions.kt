package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.Byte
import java.math.BigInteger

fun toBytes(num: BigInteger): List<Byte> = ("0" + num.toString(16))
    .reversed()
    .chunked(2)
    .map { it.reversed() }
    .reversed()
    .dropWhile { it == "0" }
    .map { Byte(it) }

fun bytesToBigInteger(bytes: List<Byte>) = BigInteger(bytes.joinToString("") { it.toStringNoHexPrefix() }, 16)

fun stripHexPrefix(num: String) = num.replaceFirst("0x", "")

fun hexPrefix(num: String) = "0x$num"
