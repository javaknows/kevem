import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.app.CommandLineAccountsCreator
import org.kevm.app.CommandLineArguments
import org.kevm.evm.model.Account
import org.kevm.evm.model.Address
import org.kevm.rpc.LocalAccount
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
}