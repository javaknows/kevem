package org.kevm.evm.numbers

import org.kevm.evm.crypto.keccak256
import org.kevm.evm.model.Address
import org.kevm.evm.model.Byte
import org.kevm.evm.model.Word
import org.kevm.evm.toByteList
import org.web3j.rlp.RlpEncoder
import org.web3j.rlp.RlpList
import org.web3j.rlp.RlpString
import java.math.BigInteger

fun generateAddress(address: List<Byte>, salt: List<Byte>, codeData: List<Byte>): Address =
    Word(
        keccak256(
            listOf(Byte(0xFF)) + address + salt + keccak256(
                codeData
            )
        )
    ).toAddress()


fun generateAddressFromSenderAndNonce(sender: Address, nonce: BigInteger): Address {
    val addressBytes = toByteList(sender.toString()).map { it.javaByte() }.toByteArray()

    val encoded = RlpEncoder.encode(RlpList(RlpString.create(addressBytes), RlpString.create(nonce)))

    return Address("0x" + keccak256(encoded).takeLast(40))
}
