package org.kevem.app

import org.kevem.evm.model.Account
import org.kevem.evm.model.Accounts
import org.kevem.evm.model.Address
import org.kevem.evm.model.EvmConfig
import org.kevem.evm.toByteList
import org.kevem.rpc.AppConfig
import org.kevem.rpc.LocalAccount
import org.kevem.rpc.LocalAccounts
import org.kevem.rpc.Mnemonic
import org.kevem.rpc.EvmContextBuilder
import org.kevem.rpc.module.EvmContext
import java.math.BigInteger

class ServerEvmContextCreator(
    private val startupSummariser: StartupSummariser,
    private val commandLineAccountsCreator: CommandLineAccountsCreator,
    private val evmContextCreator: CommandLineEvmContextBuilder
) {
    fun create(commandLine: CommandLineArguments): Pair<EvmContext, String> {
        val (accounts, localAccounts) = commandLineAccountsCreator.parseAccounts(commandLine)
        val summary = startupSummariser.summarise(commandLine, accounts, localAccounts)
        val evmContext = evmContextCreator.createEvmContext(commandLine, accounts, localAccounts)

        return Pair(evmContext, summary)
    }
}

/**
 * Create lists of Account and LocalAccount from CommandLineArguments
 */
class CommandLineAccountsCreator {
    private val weiInOneEth = BigInteger("1000000000000000000")

    fun parseAccounts(commandLine: CommandLineArguments): Pair<List<Account>, List<LocalAccount>> {
        val balance = commandLine.defaultBalanceEther.times(weiInOneEth)

        val keyPairs = Mnemonic.keyPairsFromMnemonic(commandLine.mnemonic, commandLine.numAccounts)

        return keyPairs.fold(Pair(emptyList(), emptyList())) { acc, kp ->
            val account = Account(Address(kp.address), balance)
            val localAccount = LocalAccount(Address(kp.address), toByteList(kp.privateKey), false)

            Pair(acc.first + account, acc.second + localAccount)
        }
    }
}

/**
 * Builds an EvmContext from CommandLineArguments and Account and LocalAccount lists
 */
class CommandLineEvmContextBuilder {
    fun createEvmContext(
        commandLine: CommandLineArguments,
        accounts: List<Account>,
        localAccounts: List<LocalAccount>
    ): EvmContext {
        val appConfig = buildAppConfig(commandLine)
        val evmConfig = buildEvmConfig(commandLine)

        return buildEvmContext(appConfig, localAccounts, accounts, evmConfig)
    }

    private fun buildAppConfig(commandLine: CommandLineArguments): AppConfig =
        AppConfig(
            chainId = commandLine.chainId,
            netVersion = commandLine.networkId,
            gasPrice = commandLine.gasPrice,
            blockGasLimit = commandLine.gasLimit
        )

    private fun buildEvmConfig(commandLine: CommandLineArguments): EvmConfig =
        EvmConfig(
            chainId = commandLine.chainId.toBigInteger()
        )

    private fun buildEvmContext(
        appConfig: AppConfig,
        localAccounts: List<LocalAccount>,
        accounts: List<Account>,
        evmConfig: EvmConfig
    ) = EvmContextBuilder.build(
        config = appConfig,
        localAccounts = LocalAccounts(localAccounts),
        accounts = Accounts(accounts),
        evmConfig = evmConfig
    )
}