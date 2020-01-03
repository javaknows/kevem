package org.kevm.dsl

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.rpc.AppConfig
import org.kevm.rpc.LocalAccounts
import org.kevm.rpc.StandardEvmOperations
import org.kevm.rpc.StandardRPC
import org.kevm.web3.KevmWeb3Service
import org.kevm.web3.StandardRPCProvider
import org.kevm.web3.modules.EthAdapter
import org.web3j.protocol.Web3j
import java.math.BigInteger
import java.time.Clock

object Web3ServiceCreator {

    fun createWeb3(
        config: AppConfig,
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        clock: Clock
    ): Web3j {
        val tp = TransactionProcessor(
            Executor(
                GasCostCalculator(
                    BaseGasCostCalculator(CallGasCostCalc()),
                    MemoryUsageGasCostCalculator(
                        MemoryUsageGasCalc()
                    )
                )
            )
        )

        val providers = listOf(
            StandardRPCProvider(
                EthAdapter(
                    StandardRPC(
                        StandardEvmOperations(
                            StatefulTransactionProcessor(
                                tp,
                                clock,
                                WorldState(
                                    listOf(createGenisisBlock(config)),
                                    accounts,
                                    Address(config.coinbase)
                                )
                            )
                        ),
                        config,
                        localAccounts
                    )
                )
            )
        )

        return Web3j.build(KevmWeb3Service(providers))
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