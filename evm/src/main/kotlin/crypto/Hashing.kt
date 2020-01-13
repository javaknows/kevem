package org.kevm.evm.crypto

import org.kevm.evm.model.Byte
import org.kevm.evm.model.Word
import org.bouncycastle.util.encoders.Hex
import org.bouncycastle.jcajce.provider.digest.Keccak
import org.bouncycastle.jcajce.provider.digest.RIPEMD160
import org.bouncycastle.jcajce.provider.digest.SHA256
import java.security.MessageDigest

fun keccak256(input: List<Byte>): Word = hash(input, Keccak.Digest256())

fun sha256(input: List<Byte>): Word = hash(input, SHA256.Digest())

fun ripemd160(input: List<Byte>): Word = hash(input, RIPEMD160.Digest())

private fun hash(input: List<Byte>, messageDigest: MessageDigest): Word {
    val bytes: ByteArray = input.map { it.javaByte() }.toByteArray()
    val digest: ByteArray = messageDigest.digest(bytes)

    return Word.coerceFrom(Hex.toHexString(digest))
}