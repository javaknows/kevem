package org.kevm.evm

import org.kevm.evm.model.Byte
import org.kevm.evm.model.Word
import org.bouncycastle.util.encoders.Hex
import org.bouncycastle.jcajce.provider.digest.Keccak

fun keccak256(input: List<Byte>): Word {
    val bytes: ByteArray = input.map { it.javaByte() }.toByteArray()
    val digest: ByteArray = Keccak.Digest256().digest(bytes)

    return Word.coerceFrom(Hex.toHexString(digest))
}

// SECP-256k1

//ECDSAPUBKEY, ECDSASIGN and ECDSARECOVER