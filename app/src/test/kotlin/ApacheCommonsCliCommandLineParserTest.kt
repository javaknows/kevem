import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail
import org.kevem.app.ApacheCommonsCliCommandLineParser
import org.kevem.app.CommandLineArguments
import org.kevem.evm.HardFork
import java.math.BigInteger

class ApacheCommonsCliCommandLineParserTest {

    private val underTest = ApacheCommonsCliCommandLineParser()

    @Test
    internal fun `check default are set when no arguments`() {
        val commandLineResult = underTest.parseCommandLine(emptyArray())

        assertThat(commandLineResult.error).isNull()

        val commandLine = commandLineResult.commandLine
        if (commandLine != null) {
            assertThat(commandLine.port).isEqualTo(8545)
            assertThat(commandLine.host).isEqualTo("localhost")
            assertThat(commandLine.numAccounts).isEqualTo(10)
            assertThat(commandLine.mnemonic).isNull()
            assertThat(commandLine.defaultBalanceEther).isEqualTo(BigInteger("100"))
            assertThat(commandLine.gasPrice).isEqualTo(BigInteger("20000000000"))
            assertThat(commandLine.gasLimit).isEqualTo(BigInteger("1000000000000000000000000000000"))
            assertThat(commandLine.hardFork).isEqualTo(HardFork.Istanbul)
            assertThat(commandLine.networkId).isEqualTo(1)
            assertThat(commandLine.chainId).isEqualTo(0)
            assertThat(commandLine.verbose).isEqualTo(false)
            assertThat(commandLine.version).isEqualTo(false)
            assertThat(commandLine.help).isEqualTo(false)
        } else {
            fail("commandLine is null")
        }
    }

    @Test
    internal fun `check long opt verbose flag is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--verbose"), true) { it.verbose }

    @Test
    internal fun `check verbose flag is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-v"), true) { it.verbose }

    @Test
    internal fun `check long opt port is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--port", "6666"), 6666) { it.port }

    @Test
    internal fun `check port is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-p", "6666"), 6666) { it.port }

    @Test
    internal fun `check long opt host is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--host", "example.com"), "example.com") { it.host }

    @Test
    internal fun `check host is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-h", "example.com"), "example.com") { it.host }

    @Test
    internal fun `check long opt numAccounts is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--numAccounts", "10"), 10) { it.numAccounts }

    @Test
    internal fun `check numAccounts is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-n", "10"), 10) { it.numAccounts }

    @Test
    internal fun `check long opt mnemonic is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--mnemonic", "foo"), "foo") { it.mnemonic }

    @Test
    internal fun `check mnemonic is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-mnemonic", "foo"), "foo") { it.mnemonic }

    @Test
    internal fun `check long opt defaultBalanceEther is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("--defaultBalanceEther", "100"),
            BigInteger("100")
        ) { it.defaultBalanceEther }

    @Test
    internal fun `check defaultBalanceEther is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("-defaultBalanceEther", "100"),
            BigInteger("100")
        ) { it.defaultBalanceEther }


    @Test
    internal fun `check long opt gasPrice is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("--gasPrice", "100"),
            BigInteger("100")
        ) { it.gasPrice }

    @Test
    internal fun `check gasPrice is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("-gasPrice", "100"),
            BigInteger("100")
        ) { it.gasPrice }

    @Test
    internal fun `check long opt gasLimit is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("--gasLimit", "100"),
            BigInteger("100")
        ) { it.gasLimit }

    @Test
    internal fun `check gasLimit is parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("-gasLimit", "100"),
            BigInteger("100")
        ) { it.gasLimit }

    @Test
    internal fun `check long opt networkId is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--networkId", "10"), 10) { it.networkId }

    @Test
    internal fun `check networkId is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-i", "10"), 10) { it.networkId }

    @Test
    internal fun `check long opt chainId is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--chainId", "10"), 10) { it.chainId }

    @Test
    internal fun `check chainId is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-c", "10"), 10) { it.chainId }

    @Test
    internal fun `check long opt version flag is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--version"), true) { it.version }

    @Test
    internal fun `check long opt help flag is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--help"), true) { it.help }

    internal fun <T> assertParsedArgumentValueMatches(
        args: Array<String>,
        value: T,
        check: (cmd: CommandLineArguments) -> T
    ) {
        val commandLine = underTest.parseCommandLine(args).commandLine!!
        val actual = check(commandLine)

        assertThat(actual).isEqualTo(value)
    }

    @Test
    internal fun `check long opt hard fork is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("--hardFork", "Petersburg"), HardFork.Petersburg) { it.hardFork }

    @Test
    internal fun `check hard fork is parsed`() =
        assertParsedArgumentValueMatches(arrayOf("-k", "Petersburg"), HardFork.Petersburg) { it.hardFork }

    @Test
    internal fun `check accounts are parsed`() =
        assertParsedArgumentValueMatches(
            arrayOf("-a", "account1", "-a", "account2"),
            listOf("account1", "account2")
        ) { it.accounts }

    @Test
    internal fun `check accounts are parsed with long opt`() =
        assertParsedArgumentValueMatches(
            arrayOf("-account", "account1", "-account", "account2"),
            listOf("account1", "account2")
        ) { it.accounts }

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
            .contains("--hardFork")
            .contains("--mnemonic")
            .contains("--numAccounts")
            .contains("--port")
            .contains("--version")
            .contains("--verbose")
    }
}