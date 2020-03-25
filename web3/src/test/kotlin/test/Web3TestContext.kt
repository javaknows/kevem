package org.kevm.web3.test

import org.kevm.evm.Executor
import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.rpc.*
import org.kevm.rpc.module.EthModule
import org.kevm.rpc.module.EvmContext
import org.kevm.rpc.module.NetModule
import org.kevm.rpc.module.WebModule
import org.kevm.web3.AdapterKevmWeb3jService
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
            netVersion = 5,
            coinbase = "0xC014BA5E"
        ),
        localAccounts: LocalAccounts = LocalAccounts(),
        accounts: Accounts = Accounts(),
        blocks: List<MinedBlock> = emptyList(),
        evmConfig: EvmConfig = EvmConfig(
            chainId = BigInteger.TWO,
            coinbase = Address("0xC014BA5E")
        )
    ): Web3j {
        val gasCostCalculator = GasCostCalculator(
            BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
            MemoryUsageGasCostCalculator(
                MemoryUsageGasCalc()
            )
        )
        val executor = Executor(gasCostCalculator)
        val tp = TransactionProcessor(executor, config = evmConfig)
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

        val worldState = WorldState(listOf(minedBlock) + blocks, newAccounts)

        val stp = StatefulTransactionProcessor(tp, clock, worldState)
        val eth = StandardEvmOperations(stp, evmConfig)
        val standardRPC = StandardRPC(eth, config, localAccounts)

        val testRpc = TestRPC(
            stp
        )

        val evmContext =  EvmContext(standardRPC, testRpc)

        return Web3j.build(
            AdapterKevmWeb3jService(
                KevmRpcService(listOf(WebModule, NetModule, EthModule), evmContext)
            )
        )
    }
}