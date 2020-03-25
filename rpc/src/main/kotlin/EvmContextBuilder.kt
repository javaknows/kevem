package org.kevm.rpc

import org.kevm.rpc.module.EvmContext

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.collections.BigIntegerIndexedList
import org.kevm.evm.crypto.keccak256
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.rpc.*
import java.math.BigInteger
import java.time.Clock

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