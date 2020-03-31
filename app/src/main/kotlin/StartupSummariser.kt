package org.kevem.app

import org.kevem.evm.bytesToString
import org.kevem.evm.model.Account
import org.kevem.rpc.AppConfig
import org.kevem.rpc.LocalAccount
import org.kevem.eth.Mnemonic
import java.math.BigInteger

/**
 * Generates the output when server is started listing generated accounts / balances etc.
 */
class StartupSummariser {

    fun summarise(
        commandLine: CommandLineArguments,
        accounts: List<Account>,
        localAccounts: List<LocalAccount>
    ): String = StringBuilder().apply {
        appendLine("Kevem")
        appendLine("")

        appendSection("Available Accounts",
            accounts.zip(accounts.indices).map { (account, index) ->
                val ethBalance = account.balance.div(BigInteger("1000000000000000000"))
                "($index) ${account.address} ($ethBalance ETH)"
            }
        )

        appendSection("Private Keys",
            localAccounts.zip(localAccounts.indices).map { (localAccount, index) ->
                val privateKey = bytesToString(localAccount.privateKey)
                "($index) $privateKey"
            }
        )

        appendSection(
            "Wallet", listOf(
                "Mnemonic: ${commandLine.mnemonic ?: defaultMnemonic}",
                "Path:     ${Mnemonic.path}/{account_index}"
            )
        )

        appendSection("Gas Price", listOf(commandLine.gasPrice.toString()))

        appendSection("Gas Limit", listOf(commandLine.gasLimit.toString()))

        appendLine("Listening on ${commandLine.host}:${commandLine.port}")
    }.toString()
}

private fun java.lang.StringBuilder.appendSection(title: String, lines: List<String>) {
    appendLine(title)
    appendLine(title.replace(".".toRegex(), "="))
    lines.forEach { appendLine(it) }
    appendLine("")
}

private fun java.lang.StringBuilder.appendLine(s: String) {
    append(s)
    append(System.lineSeparator())
}