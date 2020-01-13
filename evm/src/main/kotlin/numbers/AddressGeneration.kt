package org.kevm.evm.numbers

import org.kevm.evm.crypto.keccak256
import org.kevm.evm.model.Address
import org.kevm.evm.model.Byte
import org.kevm.evm.model.Word
import java.math.BigInteger

fun generateAddress(address: List<Byte>, salt: List<Byte>, codeData: List<Byte>): Address =
    keccak256(
        listOf(Byte(0xFF)) + address + salt + keccak256(
            codeData
        ).data
    ).toAddress()


fun generateAddressFromSenderAndNonce(sender: Address, nonce: BigInteger): Address =
    keccak256(sender.toWord().data + Word.coerceFrom(nonce).data).toAddress()
