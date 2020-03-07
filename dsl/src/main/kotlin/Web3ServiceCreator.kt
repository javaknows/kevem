package org.kevm.dsl

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.rpc.*
import org.kevm.web.KevmWebRpcService
import org.kevm.web.module.EthModule
import org.kevm.web.module.EvmContext
import org.kevm.web.module.NetModule
import org.kevm.web.module.WebModule
import org.kevm.web3.AdapterKevmWeb3jService
import org.web3j.protocol.Web3j
import java.math.BigInteger
import java.time.Clock

object Web3ServiceCreator {

    fun createWeb3(
        config: AppConfig,
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        clock: Clock,
        evmConfig: EvmConfig
    ): Web3j {
        val tp = TransactionProcessor(
            Executor(
                GasCostCalculator(
                    BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
                    MemoryUsageGasCostCalculator(
                        MemoryUsageGasCalc()
                    )
                )
            ),
            // TODO - pass in EIP / features here too
            config = evmConfig
        )

        val stp = StatefulTransactionProcessor(
            tp, clock, WorldState(
                listOf(createGenisisBlock(config)),
                accounts
            )
        )

        val evmContext = EvmContext(
            StandardRPC(StandardEvmOperations(stp, evmConfig), config, localAccounts), TestRPC(
                stp
            )
        )

        return Web3j.build(
            AdapterKevmWeb3jService(
                KevmWebRpcService(listOf(WebModule, NetModule, EthModule), evmContext)
            )
        )
    }

    private fun createGenisisBlock(config: AppConfig): MinedBlock {
        return MinedBlock(
            block = Block(
                number = BigInteger.ONE,
                difficulty = config.difficulty,
                gasLimit = config.blockGasLimit,
                timestamp = config.genesisBlockTimestamp
            ),
            gasUsed = BigInteger.ZERO,
            hash = toByteList("0x88e96d4537bea4d9c05d12549907b32561d3bf31f45aae734cdc119f13406cb6"),
            transactions = emptyList()
        )
    }
}