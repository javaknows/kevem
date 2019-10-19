package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.Address
import com.gammadex.kevin.evm.model.Byte
import kotlin.random.Random

interface AddressGenerator {
    fun nextAddress(): Address
}

class DefaultAddressGenerator(private val random: Random = Random(999)) : AddressGenerator {

    override fun nextAddress() = Address(
        random.nextBytes(ByteArray(20))
            .map { 0xFF and it.toInt() }
            .map { Byte(it) }
            .map { it.toStringNoHexPrefix() }
            .joinToString("")
    )

}