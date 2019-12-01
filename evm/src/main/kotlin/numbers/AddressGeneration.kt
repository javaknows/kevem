package com.gammadex.kevin.evm.numbers

import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.Address
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.model.Word
import java.math.BigInteger

fun generateAddress(address: List<Byte>, salt: List<Byte>, codeData: List<Byte>): Address =
    keccak256(listOf(Byte(0xFF)) + address + salt + keccak256(codeData).data).toAddress()


fun generateAddressFromSenderAndNonce(sender: Address, nonce: BigInteger): Address =
    keccak256(sender.toWord().data + Word.coerceFrom(nonce).data).toAddress()
