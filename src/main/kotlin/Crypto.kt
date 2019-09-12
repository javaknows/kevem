package com.gammadex.kevin

import org.bouncycastle.util.encoders.Hex
import org.bouncycastle.jcajce.provider.digest.SHA3


fun keccak256(input: List<Byte>): Word {
    val bytes: ByteArray = input.map { it.javaByte() }.toByteArray()
    val digest: ByteArray = SHA3.Digest256().digest(bytes)

    return Word.coerceFrom(Hex.toHexString(digest))
}