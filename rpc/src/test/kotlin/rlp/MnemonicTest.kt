package rlp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kevm.common.KevmException
import org.kevm.rpc.KeyPair
import org.kevm.rpc.Mnemonic


class MnemonicTest {

    @Test
    internal fun check_accounts_generated_for_valid_mnemonic() {
        val mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
        val numAccounts = 2

        val generated = Mnemonic.keyPairsFromMnemonic(mnemonic, numAccounts)

        assertThat(generated).isEqualTo(
            listOf(
                KeyPair(
                    address = "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
                    privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
                ),
                KeyPair(
                    address = "0x6e69847df277bb9c3e88f170be883d4a6195f180",
                    privateKey = "0xc2a4b649d0516ea346b41e6524b4cc43d87f14f9eb33a63469bba75a10842147"
                )
            )
        )
    }

    @Test
    internal fun check_exception_thrown_for_valid_mnemonic() {
        val mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother"
        val numAccounts = 2

        val exception: KevmException = assertThrows {
            Mnemonic.keyPairsFromMnemonic(mnemonic, numAccounts)
        }

        assertThat(exception.message).contains("invalid mnemonic")
    }
}