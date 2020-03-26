package org.kevem.evm.numbers

import java.math.BigInteger

fun log256(num: BigInteger): BigInteger =
    if (num == BigInteger.ZERO) {
        throw ArithmeticException("log256(0) is not defined")
    } else {
        log256Rec(num, BigInteger.ZERO)
    }

private tailrec fun log256Rec(num: BigInteger, count: BigInteger): BigInteger =
    if (num == BigInteger.ZERO) {
        count - BigInteger.ONE
    } else {
        log256Rec(num / BigInteger("256"), count + BigInteger.ONE)
    }
