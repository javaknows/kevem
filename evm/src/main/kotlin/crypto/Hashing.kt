package org.kevm.evm.crypto

import org.bouncycastle.jcajce.provider.digest.*
import org.kevm.evm.model.Byte
import org.bouncycastle.util.encoders.Hex
import org.kevm.evm.toByteList
import java.security.MessageDigest

fun keccak256(input: List<Byte>): List<Byte> = hash(input, Keccak.Digest256())

fun sha256(input: List<Byte>): List<Byte> = hash(input, SHA256.Digest())

fun ripemd160(input: List<Byte>): List<Byte> = hash(input, RIPEMD160.Digest())

fun blake2(input: List<Byte>): List<Byte> = hash(input, Blake2bfMessageDigest())

fun keccak256(bytes: ByteArray): String = Hex.toHexString(Keccak.Digest256().digest(bytes))

private fun hash(input: List<Byte>, messageDigest: MessageDigest): List<Byte> {
    val bytes: ByteArray = input.map { it.javaByte() }.toByteArray()
    val digest: ByteArray = messageDigest.digest(bytes)

    return toByteList(Hex.toHexString(digest))
}