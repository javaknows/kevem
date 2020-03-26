package org.kevem.app

import org.apache.commons.cli.*
import java.io.PrintWriter
import java.io.StringWriter
import java.math.BigInteger
import org.apache.commons.cli.CommandLine as ApacheCommandLine
import org.apache.commons.cli.CommandLineParser as ApacheCommandLineParser

data class CommandLineArguments(
    val port: Int = 8545,
    val host: String = "localhost",
    val numAccounts: Int = 10,
    val mnemonic: String = "spell novel real kidney pride thank dust another despair consider donate festival",
    val defaultBalanceEther: BigInteger = BigInteger("100"),
    val gasPrice: BigInteger = BigInteger("20000000000"),
    val gasLimit: BigInteger = BigInteger("1000000000000000000000000000000"), // ganache is 6721975
    val verbose: Boolean = false,
    val networkId: Int = 1,
    val chainId: Int = 0,
    val version: Boolean = false,
    val help: Boolean = false
)

data class CommandLineParseResult(
    val commandLine: CommandLineArguments? = null,
    val error: String? = null
)

interface CommandLineParser {
    fun parseCommandLine(args: Array<String>): CommandLineParseResult

    fun help(): String
}

class ApacheCommonsCliCommandLineParser : CommandLineParser {
    private val parser: ApacheCommandLineParser = DefaultParser()

    private val options = Options().apply {
        addOption(Option("p", "port", true, "port web RPC service listens on"))
        addOption(Option("h", "host", true, "hostname web RPC service listens on"))
        addOption(Option("n", "numAccounts", true, "number of accounts to create when using mnemonic"))
        addOption(Option("m", "mnemonic", true, "bip39 mnemonic phrase for account generation"))
        addOption(Option("e", "defaultBalanceEther", true, "balance for each generated account"))
        addOption(Option("g", "gasPrice", true, "block gas price in wei"))
        addOption(Option("l", "gasLimit", true, "block gas limit in wei"))
        addOption(Option("i", "networkId", true, "network ID"))
        addOption(Option("c", "chainId", true, "chain ID"))
        addOption(Option("v", "verbose", false, "print extra output including stack traces for startup errors"))
        addOption(Option(null, "version", false, "display version then exit"))
        addOption(Option(null, "help", false, "display help then exit"))
    }

    override fun parseCommandLine(args: Array<String>): CommandLineParseResult {
        return try { // parse the command line arguments
            val parsed = parser.parse(options, args)
            val defaults = CommandLineArguments()

            val cmdLine = defaults.copy(
                port = intValue("port", parsed, defaults.port),
                host = stringValue("host", parsed, defaults.host),
                numAccounts = intValue("numAccounts", parsed, defaults.numAccounts),
                mnemonic = stringValue("mnemonic", parsed, defaults.mnemonic),
                defaultBalanceEther = bigIntValue("defaultBalanceEther", parsed, defaults.defaultBalanceEther),
                gasPrice = bigIntValue("gasPrice", parsed, defaults.gasPrice),
                gasLimit = bigIntValue("gasLimit", parsed, defaults.gasLimit),
                networkId = intValue("networkId", parsed, defaults.networkId),
                chainId = intValue("chainId", parsed, defaults.chainId),
                verbose = booleanValue("verbose", parsed, defaults.verbose),
                version = booleanValue("version", parsed, defaults.version),
                help = booleanValue("help", parsed, defaults.help)
            )

            CommandLineParseResult(cmdLine, null)
        } catch (e: ParseException) {
            CommandLineParseResult(null, e.message)
        }
    }

    private fun stringValue(argName: String, parsedCommandLine: ApacheCommandLine, default: String) =
        parsedCommandLine.getOptionValue(argName) ?: default

    private fun intValue(argName: String, parsedCommandLine: ApacheCommandLine, default: Int) =
        parsedCommandLine.getOptionValue(argName)?.toInt() ?: default

    private fun bigIntValue(argName: String, parsedCommandLine: ApacheCommandLine, default: BigInteger) =
        parsedCommandLine.getOptionValue(argName)?.toBigInteger() ?: default

    private fun booleanValue(argName: String, parsedCommandLine: ApacheCommandLine, default: Boolean) =
        parsedCommandLine.hasOption(argName)

    override fun help(): String {
        val writer = StringWriter()

        HelpFormatter().printHelp(
            PrintWriter(writer),
            HelpFormatter.DEFAULT_WIDTH,
            "kevem",
            "",
            options,
            HelpFormatter.DEFAULT_LEFT_PAD,
            HelpFormatter.DEFAULT_DESC_PAD,
            ""
        )

        return writer.toString()
    }
}
