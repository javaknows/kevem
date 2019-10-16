package com.gammadex.kevin

import com.gammadex.kevin.model.Address
import com.gammadex.kevin.model.Byte
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