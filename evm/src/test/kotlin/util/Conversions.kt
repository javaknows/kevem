package org.kevem.evm.util

import org.kevem.common.Byte
import org.kevem.evm.Opcode
import org.kevem.evm.toByteList
import java.math.BigInteger

fun toInt(number: String) = toBigInteger(number).toInt()

fun toBigInteger(number: String) =
    if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

fun byteCodeOrDataFromNamesOrHex(byteCodeNames: String): List<Byte> =
    byteCodeNames.split("[, ]".toRegex())
        .map { it.trim() }
        .flatMap {
            if (it.startsWith("0x")) toByteList(it)
            else listOf(Opcode.fromName(it)?.code)
        }
        .filterNotNull()

