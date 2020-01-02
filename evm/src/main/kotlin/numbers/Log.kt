package org.kevm.evm.numbers

import java.math.BigInteger

// TODO - get this working with BigIntegers without downcasting to Int
fun logn(num: BigInteger, base: BigInteger): BigInteger = (Math.log(num.toDouble()) / Math.log(base.toDouble()))
    .toInt()
    .toBigInteger()