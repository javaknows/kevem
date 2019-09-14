package com.gammadex.kevin

import com.gammadex.kevin.Word.Companion.coerceFrom

// TODO - make operations overflow like a real EVM
object VmMath {
    fun add(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() + w2.toBigInt())

    fun mul(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() * w2.toBigInt())

    fun sub(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() - w2.toBigInt())

    fun div(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() / w2.toBigInt())

    fun sdiv(w1: Word, w2: Word): Word = TODO()

    fun mod(w1: Word, w2: Word) = coerceFrom(w1.toBigInt().mod(w2.toBigInt()))

    fun smod(w1: Word, w2: Word): Word = TODO()

    fun addMod(w1: Word, w2: Word, w3: Word): Word {
        val sum = w1.toBigInt().add(w2.toBigInt())
        val mod = sum.mod(w3.toBigInt())

        return coerceFrom(mod)
    }

    fun mulMod(w1: Word, w2: Word, w3: Word): Word {
        val sum = w1.toBigInt().multiply(w2.toBigInt())
        val mod = sum.mod(w3.toBigInt())

        return coerceFrom(mod)
    }

    fun exp(w1: Word, w2: Word) = coerceFrom(w1.toBigInt().pow(w2.toBigInt().toInt()))

    fun signExtend(w1: Word, w2: Word): Word = TODO()

    fun lt(w1: Word, w2: Word): Word = coerceFrom(w1.toBigInt() < w2.toBigInt())

    fun gt(w1: Word, w2: Word): Word = coerceFrom(w1.toBigInt() > w2.toBigInt())

    fun slt(w1: Word, w2: Word): Word = TODO()

    fun sgt(w1: Word, w2: Word): Word = TODO()

    fun and(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() and w2.toBigInt())

    fun or(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() or w2.toBigInt())

    fun xor(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() xor w2.toBigInt())

    fun not(w1: Word): Word = TODO()

    fun shl(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() shl w2.toBigInt().toInt())

    fun shr(w1: Word, w2: Word) = coerceFrom(w1.toBigInt() shr w2.toBigInt().toInt())

    fun sar(w1: Word, w2: Word): Word =  TODO()




}