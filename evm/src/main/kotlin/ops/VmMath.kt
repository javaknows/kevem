package org.kevem.evm.ops

import org.kevem.evm.model.Byte
import org.kevem.evm.model.Word
import org.kevem.evm.model.Word.Companion.coerceFrom
import org.kevem.evm.numbers.fromTwosComplement
import org.kevem.evm.numbers.toTwosComplement
import java.math.BigInteger

object VmMath {
    fun add(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() + w2.toBigInt())

    fun mul(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() * w2.toBigInt())

    fun sub(w1: Word, w2: Word): Word {
        val neg = w1.toBigInt() - w2.toBigInt()
        val large = Word.max().toBigInt() + BigInteger.ONE
        val over = (neg + large).mod(large)

        return coerceFrom(over)
    }

    fun div(w1: Word, w2: Word) =
        if (w2 == Word.Zero) Word.Zero
        else coerceFrom(w1.toBigInt() / w2.toBigInt())

    fun sdiv(w1: Word, w2: Word): Word =
        if (w2 == Word.Zero) Word.Zero
        else toTwosComplement(fromTwosComplement(w1).div(fromTwosComplement(w2)))


    fun mod(w1: Word, w2: Word) =
        if (w2 == Word.Zero) Word.Zero
        else coerceFrom(w1.toBigInt().mod(w2.toBigInt()))

    fun smod(w1: Word, w2: Word): Word =
        if (fromTwosComplement(w2) == BigInteger.ZERO) Word.Zero
        else {
            val a = fromTwosComplement(w1)
            val b = fromTwosComplement(w2)
            val c = a.abs().mod(b.abs())

            val result =
                if (a < BigInteger.ZERO) c.negate()
                else c

            toTwosComplement(result)
        }

    fun addMod(w1: Word, w2: Word, w3: Word): Word =
        if (w3.toBigInt() == BigInteger.ZERO) Word.Zero
        else {
            val sum = w1.toBigInt().add(w2.toBigInt())
            coerceFrom(sum.mod(w3.toBigInt()))
        }

    fun mulMod(w1: Word, w2: Word, w3: Word): Word =
        if (w3.toBigInt() == BigInteger.ZERO) Word.Zero
        else {
            val mul = w1.toBigInt().multiply(w2.toBigInt())
            coerceFrom(mul.mod(w3.toBigInt()))
        }

    fun exp(w1: Word, w2: Word): Word = coerceFrom(w1.toBigInt().modPow(w2.toBigInt(), BigInteger.valueOf(2).pow(256)))

    fun signExtend(w1: Word, w2: Word): Word =
        if (w1.toBigInt() > BigInteger("31")) w2
        else {

            val signBitOffSet = w1.toBigInt().toInt() * 8 + 7
            val signMask = BigInteger.ONE.shiftLeft(signBitOffSet)
            val mask = signMask.subtract(BigInteger.ONE)
            val isPositive = (signMask and w2.toBigInt()) == BigInteger.ZERO

            when {
                isPositive -> coerceFrom(mask).and(w2)
                else -> coerceFrom(mask).not().or(w2)
            }
        }

    fun lt(w1: Word, w2: Word): Word = coerceFrom(w1.toBigInt() < w2.toBigInt())

    fun gt(w1: Word, w2: Word): Word = coerceFrom(w1.toBigInt() > w2.toBigInt())

    fun slt(w1: Word, w2: Word): Word = coerceFrom(fromTwosComplement(w1) < (fromTwosComplement(w2)))

    fun sgt(w1: Word, w2: Word): Word = coerceFrom(fromTwosComplement(w1) > (fromTwosComplement(w2)))

    fun eq(w1: Word, w2: Word): Word = coerceFrom(w1 == w2)

    fun isZero(w1: Word): Word = coerceFrom(w1.data.none { it != Byte.Zero })

    fun and(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() and w2.toBigInt())

    fun or(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() or w2.toBigInt())

    fun xor(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() xor w2.toBigInt())

    fun not(w1: Word): Word = w1.not()

    fun byte(a: Word, b: Word): Word {
        val location = a.toBigInt()

        return if (location > BigInteger("31")) Word.Zero
        else coerceFrom(b.data[location.toInt()])
    }

    fun shl(w1: Word, w2: Word) = coerceFrom(w2.toBigInt() shl w1.toBigInt().toInt())

    fun shr(w1: Word, w2: Word) = coerceFrom(w2.toBigInt() shr w1.toBigInt().toInt())

    fun sar(w1: Word, w2: Word): Word {
        val shift = w1.toBigInt()
        val sign = (w2.data[0] and Byte(0b10000000)) != Byte.Zero
        val value =  w2.toBigInt()

        return if (shift > BigInteger("256")) {
            if (sign) Word.max()
            else Word.Zero
        } else {
            val intShift = shift.toInt()
            val shifted = value shr intShift

            if (sign) coerceFrom(shifted.or(Word.max().toBigInt() shl (256 - intShift)))
            else coerceFrom(shifted)
        }
    }
}
