package org.kevm.evm.numbers

import org.kevm.evm.model.Byte
import org.kevm.evm.model.Word
import java.math.BigInteger


fun fromTwosComplement(from: Word, size: Int = 256): BigInteger {
    val isPositive = (from.data[0].value and 0b10000000) == 0
    val firstByte = Byte(from.data[0].value and 0b01111111)

    val positiveQuantity = Word.coerceFrom(listOf(firstByte) + from.data.drop(1), 256 / 8).toBigInt()

    return when {
        isPositive -> positiveQuantity
        else -> (BigInteger("2").pow(size - 1) - positiveQuantity).negate()
    }
}

fun toTwosComplement(from: BigInteger, size: Int = 256): Word {
    val bitPow = BigInteger("2").pow(size)

    return when {
        from.signum() >= 0 -> Word.coerceFrom(from.abs(), size / 8)
        else -> Word.coerceFrom(bitPow + from, size / 8)
    }
}