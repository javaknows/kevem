package org.kevem.evm.util

import org.kevem.evm.model.Byte
import org.kevem.evm.Opcode
import java.math.BigInteger

fun toInt(number: String) = toBigInteger(number).toInt()

fun toBigInteger(number: String) =
    if (number.startsWith("0x")) BigInteger(number.replaceFirst("0x", ""), 16)
    else BigInteger(number)

fun byteCodeOrDataFromNamesOrHex(byteCodeNames: String): List<Byte> =
    byteCodeNames.split("[, ]".toRegex())
        .map { it.trim() }
        .map {
            if (it.startsWith("0x")) Byte(it)
            else Opcode.fromName(it)?.code
        }
        .filterNotNull()

