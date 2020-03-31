import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevem.app.CommandLineAccountsCreator
import org.kevem.app.CommandLineArguments
import org.kevem.evm.model.Account
import org.kevem.evm.model.Address
import org.kevem.rpc.LocalAccount
import java.math.BigInteger

class CommandLineAccountsCreatorTest {

    private val underTest = CommandLineAccountsCreator()

    @Test
    internal fun `check accounts are created for mnemonic`() {
        val (accounts, localAccounts) = underTest.parseAccounts(
            CommandLineArguments(
                mnemonic = "spell novel real kidney pride thank dust another despair consider donate festival",
                numAccounts = 2,
                defaultBalanceEther = BigInteger("200")
            )
        )

        assertThat(accounts).isEqualTo(
            listOf(
                Account(Address("0x47d9e03fbe9c4787fb926f591fe11463d866c737"), BigInteger("200000000000000000000")),
                Account(Address("0x063a9e0f3ce69768b5f2744e98680588c001c905"), BigInteger("200000000000000000000"))
            )
        )

        assertThat(localAccounts).isEqualTo(
            listOf(
                LocalAccount(
                    "0x47d9e03fbe9c4787fb926f591fe11463d866c737",
                    "0x1a5749a255f4a74f4aeb4666d096bfa67feddfa78d8574ba2b7c856bba9e451d",
                    false
                ),
                LocalAccount(
                    "0x063a9e0f3ce69768b5f2744e98680588c001c905",
                    "0xf12a295f93af59d0559984c9569c150fbe109004ed63a425d5536d03397b17f0",
                    false
                )
            )
        )
    }

    @Test
    internal fun `check accounts are created for private key`() {
        val (accounts, localAccounts) = underTest.parseAccounts(
            CommandLineArguments(
                accounts = listOf(
                    "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395",
                    "0xc2a4b649d0516ea346b41e6524b4cc43d87f14f9eb33a63469bba75a10842147"
                ),
                defaultBalanceEther = BigInteger("200")
            )
        )

        assertThat(accounts).isEqualTo(
            listOf(
                Account(Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"), BigInteger("200000000000000000000")),
                Account(Address("0x6e69847df277bb9c3e88f170be883d4a6195f180"), BigInteger("200000000000000000000"))
            )
        )

        assertThat(localAccounts).isEqualTo(
            listOf(
                LocalAccount(
                    "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
                    "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395",
                    false
                ),
                LocalAccount(
                    "0x6e69847df277bb9c3e88f170be883d4a6195f180",
                    "0xc2a4b649d0516ea346b41e6524b4cc43d87f14f9eb33a63469bba75a10842147",
                    false
                )
            )
        )
    }

    @Test
    internal fun `accounts are created for if no address or primary keys supplied`() {
        val (accounts, localAccounts) = underTest.parseAccounts(
            CommandLineArguments()
        )

        assertThat(accounts).hasSize(10)
        assertThat(localAccounts).hasSize(10)
    }
}