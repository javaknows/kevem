package org.kevm.evm.crypto

import org.kevm.evm.bytesToBigInteger
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.web3j.crypto.ECDSASignature
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import java.math.BigInteger

fun ecdsarecover(input: List<Byte>): List<Byte> {
    val safe = (input + Byte.Zero.repeat(128)).take(128)
    val (hIn, vIn, rIn, sIn) = safe.chunked(32)

    val r = bytesToBigInteger(rIn)
    val s = bytesToBigInteger(sIn)
    val v = ((vIn.last()).javaByte().toInt() - 27)
    val h = hIn.map { it.javaByte() }

    return try {
        recover(v, r, s, h)
    } catch (e: Exception) {
        emptyList()
    }
}

private fun recover(v: Int, r: BigInteger, s: BigInteger, h: List<kotlin.Byte>): List<Byte> {
    val recovered: BigInteger? = Sign.recoverFromSignature(v, ECDSASignature(r, s), h.toByteArray())

    return recovered?.let {
        toByteList(Keys.getAddress(it))
    } ?: emptyList()
}