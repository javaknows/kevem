package org.kevm.evm.numbers

import java.math.BigInteger

object BigIntMath {
    fun divRoundUp(n: BigInteger, d: BigInteger) = when {
        n == BigInteger.ZERO -> BigInteger.ZERO
        n.mod(d) == BigInteger.ZERO -> n / d
        else -> BigInteger.ONE + n / d
    }

    fun min(a: BigInteger, b: BigInteger) = if (a < b) a else b

    fun max(a: BigInteger, b: BigInteger) = if (a > b) a else b
}
