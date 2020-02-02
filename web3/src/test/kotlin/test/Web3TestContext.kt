package org.kevm.web3.test

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.rpc.AppConfig
import org.kevm.rpc.LocalAccounts
import org.kevm.rpc.StandardEvmOperations
import org.kevm.rpc.StandardRPC
import org.kevm.web3.KevmWeb3Service
import org.kevm.web3.StandardRPCProvider
import org.kevm.web3.modules.StandardRpcAdapter
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/**
 * Test web3 objects shared across web3 tests
 */
object Web3TestContext {

    val gasProvider = ConfigurableGasProvider().apply { set(BigInteger("1000000"), BigInteger.ONE) }

    val credentials = Credentials.create("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")

    fun txManager(web3: Web3j) = RawTransactionManager(web3, credentials)

    fun createTestWeb3(
        config: AppConfig = AppConfig(
            chainId = 2,
            coinbase = "0xC014BA5E"
        ),
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        blocks: List<MinedBlock> = emptyList()
    ): Web3j {
        val gasCostCalculator = GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
            MemoryUsageGasCostCalculator(
                MemoryUsageGasCalc()
            )
        )
        val executor = Executor(gasCostCalculator)
        val tp = TransactionProcessor(executor)
        val clock = Clock.fixed(Instant.parse("2006-12-05T15:15:30.00Z"), ZoneId.of("UTC"))
        val block = Block(
            number = BigInteger.ONE,
            difficulty = BigInteger.ONE,
            gasLimit = BigInteger("1000000000000000000000000000000"),
            timestamp = Instant.parse("2006-12-03T10:15:30.00Z")
        )
        val minedBlock = MinedBlock(
            block = block,
            gasUsed = BigInteger.ZERO,
            hash = listOf(Byte.One, Byte("0x2")),
            transactions = listOf(
                MinedTransaction(
                    TransactionMessage(
                        from = Address("0xAAAAA"),
                        to = Address("0xBBBBB"),
                        value = BigInteger.ONE,
                        gasPrice = BigInteger.TEN,
                        gasLimit = BigInteger.TEN,
                        nonce = BigInteger.ZERO,
                        hash = toByteList("0x112233")
                    ),
                    TransactionResult(
                        status = ResultStatus.COMPLETE,
                        gasUsed = BigInteger.TEN
                    )
                )
            )
        )

        val newAccounts = accounts.updateBalance(
            Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"),
            BigInteger("99999999999999999999")
        )

        val worldState = WorldState(listOf(minedBlock) + blocks, newAccounts, Address(config.coinbase))

        val stp = StatefulTransactionProcessor(tp, clock, worldState)
        val eth = StandardEvmOperations(stp)
        val standardRPC = StandardRPC(eth, config, localAccounts)
        val ethAdapter = StandardRpcAdapter(standardRPC)
        val rpcProviders = listOf(
            StandardRPCProvider(ethAdapter)
        )
        val kevmWeb3Service = KevmWeb3Service(rpcProviders)

        return Web3j.build(kevmWeb3Service)
    }
}