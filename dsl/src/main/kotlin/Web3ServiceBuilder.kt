package org.kevem.dsl

import org.kevem.evm.model.*
import org.kevem.rpc.*
import org.kevem.rpc.EvmContextBuilder
import org.kevem.rpc.module.EthModule
import org.kevem.rpc.module.NetModule
import org.kevem.rpc.module.TestModule
import org.kevem.rpc.module.WebModule
import org.kevem.rpc.KevemRpcService
import org.kevem.web3.AdapterKevemWeb3jService
import org.web3j.protocol.Web3j
import java.time.Clock

object Web3ServiceBuilder {

    fun buildWeb3(
        config: AppConfig,
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        clock: Clock,
        evmConfig: EvmConfig
    ): Web3j = Web3j.build(
        AdapterKevemWeb3jService(
            KevemRpcService(
                listOf(WebModule, NetModule, EthModule, TestModule), EvmContextBuilder.build(
                    config = config,
                    localAccounts = localAccounts,
                    accounts = accounts,
                    clock = clock,
                    evmConfig = evmConfig
                )
            )
        )
    )
}