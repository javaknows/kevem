package com.gammadex.kevin

import java.math.BigInteger

fun toBytes(num: BigInteger): List<Byte> = ("0" + num.toString(16))
    .reversed()
    .chunked(2)
    .map { it.reversed() }
    .reversed()
    .dropWhile { it == "0" }
    .map { Byte(it) }

fun toBigInteger(bytes: List<Byte>) = BigInteger(bytes.joinToString("") { it.toStringNoHexPrefix() }, 16)

