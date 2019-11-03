package com.gammadex.kevin.evm.numbers

import java.math.BigInteger

object BigIntMath {
    fun divRoundUp(n: BigInteger, d: BigInteger) = if (n.mod(d) > BigInteger.ZERO) n / d else BigInteger.ONE + n / d

    fun min(a: BigInteger, b: BigInteger) = if (a < b) a else b
}
