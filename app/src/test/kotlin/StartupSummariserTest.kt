import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevem.app.CommandLineArguments
import org.kevem.app.StartupSummariser
import org.kevem.evm.model.Account
import org.kevem.evm.model.Address
import org.kevem.rpc.LocalAccount
import java.math.BigInteger

internal class StartupSummariserTest {

    private val underTest = StartupSummariser()

    @Test
    fun `check startup summary is generated`() {
        val summary = underTest.summarise(
            CommandLineArguments(
                port = 8546,
                host = "example.com",
                gasPrice = BigInteger("3"),
                gasLimit = BigInteger("4")
            ),
            listOf(
                Account(Address("0x47d9e03fbe9c4787fb926f591fe11463d866c737"), BigInteger("100000000000000000000")),
                Account(Address("0x063a9e0f3ce69768b5f2744e98680588c001c905"), BigInteger("100000000000000000000"))
            ),
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

        assertThat(summary).isEqualTo(
            """Kevem

Available Accounts
==================
(0) 0x47d9e03fbe9c4787fb926f591fe11463d866c737 (100 ETH)
(1) 0x063a9e0f3ce69768b5f2744e98680588c001c905 (100 ETH)

Private Keys
============
(0) 0x1a5749a255f4a74f4aeb4666d096bfa67feddfa78d8574ba2b7c856bba9e451d
(1) 0xf12a295f93af59d0559984c9569c150fbe109004ed63a425d5536d03397b17f0

Wallet
======
Mnemonic: spell novel real kidney pride thank dust another despair consider donate festival
Path:     m/44'/60'/0'/0/{account_index}

Gas Price
=========
3

Gas Limit
=========
4

Listening on example.com:8546

""".trimIndent()
        )
    }
}