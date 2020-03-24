package org.kevm.dsl

import org.kevm.evm.model.*
import org.kevm.rpc.*
import org.kevm.web.EvmContextBuilder
import org.kevm.web.KevmWebRpcService
import org.kevm.web.module.EthModule
import org.kevm.web.module.NetModule
import org.kevm.web.module.WebModule
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
            KevmWebRpcService(
                listOf(WebModule, NetModule, EthModule), EvmContextBuilder.build(
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