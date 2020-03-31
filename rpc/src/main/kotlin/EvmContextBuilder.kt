package org.kevem.rpc

import org.kevem.rpc.module.EvmContext

import org.kevem.evm.Executor
import org.kevem.evm.StatefulTransactionProcessor
import org.kevem.evm.TransactionProcessor
import org.kevem.evm.crypto.keccak256
import org.kevem.evm.gas.*
import org.kevem.evm.model.*
import java.math.BigInteger
import java.time.Clock
import org.kevem.common.Byte

object EvmContextBuilder {

    fun build(
        config: AppConfig = AppConfig(),
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        clock: Clock = Clock.systemUTC(),
        evmConfig: EvmConfig = EvmConfig(),
        genesisBlock: MinedBlock = createGenisisBlock(config) // tests can override the genesis block
    ): EvmContext {
        val tp = TransactionProcessor(
            Executor(
                GasCostCalculator(
                    BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
                    MemoryUsageGasCostCalculator(
                        MemoryUsageGasCalc()
                    )
                )
            ),
            config = evmConfig
        )

        val statefulTransactionProcessor = StatefulTransactionProcessor(
            tp, clock, WorldState(
                listOf(genesisBlock),
                accounts
            )
        )

        val standardRpc = StandardRPC(
            StandardEvmOperations(
                statefulTransactionProcessor,
                evmConfig
            ),
            config,
            localAccounts
        )

        val testRpc = TestRPC(
            statefulTransactionProcessor
        )

        return EvmContext(standardRpc, testRpc)
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
            hash = keccak256(listOf(Byte(1)))
        )
    }
}