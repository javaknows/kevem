package com.gammadex.kevin.evm.util

import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.Opcode
import java.math.BigInteger

internal fun toInt(number: String) = toBigInteger(number).toInt()

internal fun toBigInteger(number: String) =
    if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

internal fun byteCodeFromNames(byteCodeNames: String): List<Byte> =
    byteCodeNames.split(",")
        .map { it.trim() }
        .mapNotNull { Opcode.fromString(it) }
        .map { it.code }

internal fun toByteList(bytes: String): List<Byte> {
    val noPrefixStack = bytes.replaceFirst("0x", "")
    val cleanStack = if (noPrefixStack.length % 2 == 0) noPrefixStack else "0$noPrefixStack"

    return cleanStack.chunked(2).map { Byte(it) }
}
