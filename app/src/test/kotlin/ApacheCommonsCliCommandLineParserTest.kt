import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail
import org.kevm.app.ApacheCommonsCliCommandLineParser
import java.math.BigInteger

class ApacheCommonsCliCommandLineParserTest {

    val underTest = ApacheCommonsCliCommandLineParser()

    @Test
    internal fun `check default are set when no arguments`() {
        val commandLineResult = underTest.parseCommandLine(emptyArray())

        assertThat(commandLineResult.error).isNull()

        val commandLine = commandLineResult.commandLine
        if (commandLine != null) {
            assertThat(commandLine.port).isEqualTo(8545)
            assertThat(commandLine.host).isEqualTo("localhost")
            assertThat(commandLine.numAccounts).isEqualTo(10)
            assertThat(commandLine.mnemonic).isEqualTo("spell novel real kidney pride thank dust another despair consider donate festival")
            assertThat(commandLine.defaultBalanceEther).isEqualTo(BigInteger("100"))
            assertThat(commandLine.gasPrice).isEqualTo(BigInteger("20000000000"))
            assertThat(commandLine.gasLimit).isEqualTo(BigInteger("1000000000000000000000000000000"))
            assertThat(commandLine.networkId).isEqualTo(1)
            assertThat(commandLine.chainId).isEqualTo(0)
            assertThat(commandLine.version).isEqualTo(false)
            assertThat(commandLine.help).isEqualTo(false)
        } else {
            fail("commandLine is null")
        }
    }

    @Test
    internal fun `check help has all options`() {
        val value = underTest.help()

        assertThat(value)
            .contains("--chainId")
            .contains("--gasPrice")
            .contains("--host")
            .contains("--help")
            .contains("--networkId")
            .contains("--gasLimit")
            .contains("--mnemonic")
            .contains("--numAccounts")
            .contains("--port")
            .contains("--version")
    }
}