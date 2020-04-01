package org.kevem.evm.util

import org.kevem.common.Byte
import org.kevem.evm.Opcode
import org.kevem.common.conversions.toByteList
import java.math.BigInteger

fun byteCodeOrDataFromNamesOrHex(byteCodeNames: String): List<Byte> =
    byteCodeNames.split("[, ]".toRegex())
        .map { it.trim() }
        .flatMap {
            if (it.startsWith("0x")) toByteList(it)
            else listOf(Opcode.fromName(it)?.code)
        }
        .filterNotNull()
