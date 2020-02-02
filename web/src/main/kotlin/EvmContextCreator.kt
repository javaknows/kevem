package org.kevm.web

import org.kevm.web.module.EvmContext

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.rpc.*
import java.math.BigInteger
import java.time.Clock

object EvmContextCreator {

    fun create(
        config: AppConfig = AppConfig(
            coinbase = "0xc94770007dda54cF92009BFF0dE90c06F603a09f",
            hashRate = toBigInteger("0x123")
        ),
        localAccounts: LocalAccounts = LocalAccounts(
            listOf(LocalAccount(Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), emptyList(), true))
        ),
        accounts: Accounts = Accounts(),
        clock: Clock = Clock.systemUTC()
    ): EvmContext {
        val tp = TransactionProcessor(
            Executor(
                GasCostCalculator(
                    BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
                    MemoryUsageGasCostCalculator(
                        MemoryUsageGasCalc()
                    )
                )
            )
        )

        val standardRPC = StandardRPC(
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

        return EvmContext(standardRPC)
    }

    // TODO - copy / paste job
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