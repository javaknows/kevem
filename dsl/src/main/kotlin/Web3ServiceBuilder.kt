package org.kevm.dsl

import org.kevm.evm.model.*
import org.kevm.rpc.*
import org.kevm.rpc.EvmContextBuilder
import org.kevm.rpc.module.EthModule
import org.kevm.rpc.module.NetModule
import org.kevm.rpc.module.TestModule
import org.kevm.rpc.module.WebModule
import org.kevm.rpc.KevmRpcService
import org.kevm.web3.AdapterKevmWeb3jService
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
        AdapterKevmWeb3jService(
            KevmRpcService(
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