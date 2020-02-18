package numbers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.kevm.evm.model.Address
import org.kevm.evm.numbers.generateAddressFromSenderAndNonce
import java.math.BigInteger

internal class AddressGenerationKtTest {

    @Test
    fun `generate address from sender and nonce`() {
        val address = Address("0xa94f5374fce5edbc8e2a8697c15331677e6ebf0b")
        val nonce = BigInteger.ZERO

        val generated = generateAddressFromSenderAndNonce(address, nonce)

        assertThat(generated).isEqualTo(Address("0x6295ee1b4f6dd65047762f924ecd367c17eabf8f"))
    }
}