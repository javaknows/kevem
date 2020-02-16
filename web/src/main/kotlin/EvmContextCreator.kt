package org.kevm.web

import org.kevm.web.module.EvmContext

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.collections.BigIntegerIndexedList
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
            listOf(
                LocalAccount(Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), emptyList(), false),
                LocalAccount(
                    Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"),
                    toByteList("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"),
                    false
                )
            )
        ),
        accounts: Accounts = Accounts(
            mapOf(
                Pair(
                    Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"),
                    Account(Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f"), toBigInteger("0x234c8a3397aab58"))
                ),
                Pair(
                    Address("0x295a70b2de5e3953354a6a8344e616ed314d7251"),
                    Account(
                        address = Address("0x295a70b2de5e3953354a6a8344e616ed314d7251"),
                        contract = Contract(
                            storage = Storage(
                                mapOf(
                                    Pair(BigInteger.ZERO, Word.coerceFrom("0x4d2"))
                                )
                            ),
                            code = BigIntegerIndexedList.fromByteString("0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056")
                        )
                    )
                )
            )
        ),
        clock: Clock = Clock.systemUTC(),
        evmConfig: EvmConfig = EvmConfig(
            chainId = BigInteger.TWO,
            coinbase = Address("0xc94770007dda54cF92009BFF0dE90c06F603a09f")
        )
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
                listOf(createGenisisBlock(config)),
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
            transactions = listOf(
                MinedTransaction(
                    TransactionMessage(
                        from = Address("0xc94770007dda54cf92009bff0de90c06f603a09f"),
                        to = Address("0x0"),
                        value = BigInteger.ZERO,
                        gasPrice = BigInteger.ZERO,
                        gasLimit = BigInteger.ZERO,
                        nonce = BigInteger.ZERO,
                        hash = toByteList("0xb903239f8543d04b5dc1ba6579132b143087c68db1b2168786408fcbce568238")
                    ),
                    TransactionResult(
                        status = ResultStatus.COMPLETE,
                        gasUsed = BigInteger.ONE,
                        logs = listOf(
                            Log(
                                emptyList(),
                                listOf(Word.coerceFrom("0x000000000000000000000000a94f5374fce5edbc8e2a8697c15331677e6ebf0b"))
                            )
                        )
                    )
                )
            )
        )
    }
}