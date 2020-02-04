package org.kevm.web3

import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.rpc.AppConfig
import org.kevm.rpc.LocalAccount
import org.kevm.rpc.LocalAccounts
import org.kevm.rpc.toBigInteger
import org.kevm.web3.test.Web3TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kevm.web.EvmContextCreator
import org.kevm.web.KevmWebRpcService
import org.kevm.web.module.EthModule
import org.kevm.web.module.NetModule
import org.kevm.web.module.WebModule
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import java.math.BigInteger
import java.time.Instant
import org.web3j.protocol.core.methods.request.Transaction as Web3RequestTransaction
import org.web3j.protocol.core.methods.response.Transaction as Web3ResponseTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt as Web3TransactionReceipt

/**
 * Call supported RPC methods to make sure we get a result
 */
class AdapterKevmWeb3jServiceCallsTest {

    var web3 = Web3j.build(
        AdapterKevmWeb3jService(
            KevmWebRpcService(listOf(WebModule, NetModule, EthModule), EvmContextCreator.create())
        )
    )

    //var web3 = Web3TestContext.createTestWeb3()
    var txManager = Web3TestContext.txManager(web3)

    val baseAppConfig = AppConfig(
        chainId = 2,
        coinbase = "0xC014BA5E"
    )

    val sampleTransaction = MinedTransaction(
        TransactionMessage(
            Address("0x1"),
            Address("0x2"),
            BigInteger.ONE,
            BigInteger.ONE,
            BigInteger.ONE,
            emptyList(),
            BigInteger.ONE
        ),
        TransactionResult(
            ResultStatus.COMPLETE,
            BigInteger.ONE
        )
    )

    val sampleBlock = MinedBlock(
        Block(BigInteger.TWO, BigInteger.ZERO, BigInteger.TEN, Instant.MIN),
        BigInteger("1000"),
        Word.coerceFrom("0x123").data,
        listOf(sampleTransaction)
    )

    @BeforeEach
    fun setUp() {
        web3 = Web3j.build(AdapterKevmWeb3jService(KevmWebRpcService(listOf(WebModule, NetModule, EthModule), EvmContextCreator.create())))
        txManager = Web3TestContext.txManager(web3)
    }

    @Test
    fun `can calculate sha3`() {
        val result = web3.web3Sha3("0x68656c6c6f20776f726c64").send().result

        assertThat(result).isEqualTo("0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad")
    }

    @Test
    fun `can get net version`() {
        val result = web3.netVersion().send().result

        assertThat(result).isEqualTo("0x2")
    }

    @Test
    fun `net peer count is zero`() {
        val result = web3.netPeerCount().send().result

        assertThat(result).isEqualTo("0x0")
    }

    @Test
    fun `eth protocol version is 63`() {
        val result = web3.ethProtocolVersion().send().result

        assertThat(result).isEqualTo("63")
    }

    @Test
    fun `eth syncing is false`() {
        val result = web3.ethSyncing().send().result

        assertThat(result.isSyncing).isEqualTo(false)
    }

    @Test
    fun `can retrieve eth coinbase`() {
        val result = web3.ethCoinbase().send().result

        assertThat(result).isEqualTo("0xC014BA5E")
    }

    @Test
    fun `eth mining is true`() {
        val result = web3.ethMining().send().result

        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `eth hash rate is zero by default`() {
        val result = web3.ethHashrate().send().result

        assertThat(result).isEqualTo("0x0")
    }

    @Test
    fun `eth hash rate is as set in config`() {
        val web3 = Web3TestContext.createTestWeb3(config = baseAppConfig.copy(hashRate = BigInteger.TEN))

        val result = web3.ethHashrate().send().result

        assertThat(result).isEqualToIgnoringCase("0xA")
    }

    @Test
    fun `gas price is as set in config`() {
        val web3 = Web3TestContext.createTestWeb3(config = baseAppConfig.copy(gasPrice = BigInteger("69", 16)))

        val result = web3.ethGasPrice().send().result

        assertThat(result).isEqualToIgnoringCase("0x69")
    }

    @Test
    fun `can get a list of local accounts`() {
        val web3 = Web3TestContext.createTestWeb3(
            localAccounts = LocalAccounts(
                listOf(
                    LocalAccount(
                        Address("0xA"), emptyList(), true
                    ),
                    LocalAccount(
                        Address("0xB"), emptyList(), true
                    )
                )
            )
        )

        val result = web3.ethAccounts().send().result

        assertThat(result.map { it.toLowerCase() })
            .isEqualTo(
                listOf(
                    "0x000000000000000000000000000000000000000a",
                    "0x000000000000000000000000000000000000000b"
                )
            )
    }

    @Test
    fun `block number returns number of most recent block`() {
        val web3 = Web3TestContext.createTestWeb3()

        val result = web3.ethBlockNumber().send().result

        assertThat(result).isEqualToIgnoringCase("0x1")
    }

    @Test
    fun `can get net balance of account`() {
        val result = web3.ethGetBalance(
            "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
            DefaultBlockParameterName.LATEST
        ).send().result

        assertThat(result).isEqualTo("0x56bc75e2d630fffff")
    }

    @Test
    fun `can retrieve a storage element`() {
        val web3 = Web3TestContext.createTestWeb3(
            accounts = Accounts().updateContract(
                Address("0xABCDEF"),
                Contract(
                    emptyList(),
                    Storage().set(BigInteger.ONE, Word.coerceFrom(1))
                )
            )
        )

        val result = web3.ethGetStorageAt("0xABCDEF", BigInteger.ONE, DefaultBlockParameterName.LATEST).send().result

        assertThat(result).isEqualToIgnoringCase("0x0000000000000000000000000000000000000000000000000000000000000001")
    }

    @Test
    fun `can get transaction count for latest block`() {
        val tx = sampleTransaction.copy(message = sampleTransaction.message.copy(from = Address("0xAD7E55")))
        val block = sampleBlock.copy(transactions = listOf(tx, tx, tx))

        val web3 = Web3TestContext.createTestWeb3(blocks = listOf(block))

        val result = web3.ethGetTransactionCount("0xAD7E55", DefaultBlockParameterName.LATEST).send().result

        assertThat(result).isEqualToIgnoringCase("0x3")
    }

    @Test
    fun `transaction count includes previous blocks block`() {
        val tx = sampleTransaction.copy(message = sampleTransaction.message.copy(from = Address("0xAD7E55")))
        val block = sampleBlock.copy(transactions = listOf(tx, tx, tx))
        val block2 = sampleBlock.copy(transactions = listOf(tx, tx))

        val web3 = Web3TestContext.createTestWeb3(blocks = listOf(block, block2))

        val result = web3.ethGetTransactionCount("0xAD7E55", DefaultBlockParameterName.LATEST).send().result

        assertThat(result).isEqualToIgnoringCase("0x5")
    }

    @Test
    fun `transaction count for address can be retrieved by block number`() {
        val tx = sampleTransaction.copy(message = sampleTransaction.message.copy(from = Address("0xAD7E55")))
        val block = sampleBlock.copy(
            transactions = listOf(tx, tx, tx),
            block = sampleBlock.block.copy(number = BigInteger("2"))
        )
        val block2 = sampleBlock.copy(
            transactions = listOf(tx, tx),
            block = sampleBlock.block.copy(number = BigInteger("3"))
        )

        val web3 = Web3TestContext.createTestWeb3(blocks = listOf(block, block2))

        val result = web3.ethGetTransactionCount("0xAD7E55", DefaultBlockParameterNumber(BigInteger("2"))).send().result

        assertThat(result).isEqualToIgnoringCase("0x3")
    }

    @Test
    fun `check can get block transaction count by hash`() {
        val tx = sampleTransaction.copy(message = sampleTransaction.message.copy(from = Address("0xAD7E55")))
        val block = sampleBlock.copy(
            transactions = listOf(tx, tx, tx),
            hash = listOf(Byte("0x12"))
        )
        val block2 = sampleBlock.copy(
            transactions = listOf(tx, tx),
            hash = listOf(Byte("0x23"))
        )

        val web3 = Web3TestContext.createTestWeb3(blocks = listOf(block, block2))

        val result = web3.ethGetBlockTransactionCountByHash("0x23").send().result

        assertThat(result).isEqualToIgnoringCase("0x2")
    }

    @Test
    fun `check can get block transaction count by block number`() {
        val tx = sampleTransaction.copy(message = sampleTransaction.message.copy(from = Address("0xAD7E55")))
        val block = sampleBlock.copy(
            transactions = listOf(tx, tx, tx),
            block = sampleBlock.block.copy(number = BigInteger("2"))
        )
        val block2 = sampleBlock.copy(
            transactions = listOf(tx, tx),
            block = sampleBlock.block.copy(number = BigInteger("3"))
        )

        val web3 = Web3TestContext.createTestWeb3(blocks = listOf(block, block2))

        val result = web3.ethGetBlockTransactionCountByNumber(
            DefaultBlockParameterNumber(BigInteger("2"))
        ).send().result

        assertThat(result).isEqualToIgnoringCase("0x3")
    }

    //@Test
    // TODO - should have a failed result here rather than an exception
    fun `uncle count by block hash is not implemented`() {
        val result = web3.ethGetUncleCountByBlockHash("0x1").send().result

        assertThat(result).isEqualToIgnoringCase("0x1")
    }

    @Test
    fun `check a transaction can be sent`() {
        val transaction = Web3RequestTransaction(
            "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
            BigInteger.ZERO,
            BigInteger("9000"),
            BigInteger("21000"),
            "0xADD9E55",
            BigInteger("100"),
            null
        )

        val web3 = Web3TestContext.createTestWeb3(
            localAccounts = LocalAccounts(
                listOf(
                    LocalAccount(
                        Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"),
                        toByteList("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"),
                        false
                    )
                )
            )
        )

        val result = web3.ethSendTransaction(transaction).send().result

        assertThat(result).isEqualToIgnoringCase("0x9e581048b3b0d1f9c8486efa3cd6d16d75a9308a5db5d82d110bce92b4880865")
    }

    @Test
    fun `check a call can be sent`() {
        val transaction = Web3RequestTransaction(
            "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
            BigInteger.ZERO,
            BigInteger("9000"),
            BigInteger("21000"),
            "0xADD9E55",
            BigInteger.ZERO,
            null
        )

        val result = web3.ethCall(transaction, DefaultBlockParameterName.LATEST).send().result

        assertThat(result).isEqualTo("0x")
    }

    @Test
    fun `check a gas estimate for a simple balance tansfer`() {
        val transaction = Web3RequestTransaction(
            "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
            BigInteger.ZERO,
            BigInteger("9000"),
            BigInteger("21000"),
            "0xADD9E55",
            BigInteger.ZERO,
            null
        )

        val result = web3.ethEstimateGas(transaction).send().result

        assertThat(toBigInteger(result)).isEqualTo("21000")
    }

    @Test
    fun `check a block can be retrieved by hash without full transactions`() {
        val result = web3.ethGetBlockByHash("0x0102", false).send().result

        assertThat(result.number).isEqualTo(BigInteger.ONE)
        assertThat(result.hash).isEqualTo("0x0102")
        assertThat(result.transactions.map { it.get().toString() }).isEqualTo(listOf("0x112233"))
    }

    @Test
    fun `check a block can be retrieved by hash with full transactions`() {
        val result = web3.ethGetBlockByHash("0x0102", true).send().result

        assertThat(result.number).isEqualTo(BigInteger.ONE)
        assertThat(result.hash).isEqualTo("0x0102")

        val expected = responseTransaction()

        assertThat(result.transactions).isEqualTo(listOf(expected))
    }

    @Test
    fun `check a block can be retrieved by block number without full transactions`() {
        val result = web3.ethGetBlockByNumber(DefaultBlockParameterNumber(BigInteger.ONE), false).send().result

        assertThat(result.number).isEqualTo(BigInteger.ONE)
        assertThat(result.hash).isEqualTo("0x0102")

        assertThat(result.transactions.map { it.get().toString() }).isEqualTo(listOf("0x112233"))
    }

    @Test
    fun `check a block can be retrieved by block number with full transactions`() {
        val result = web3.ethGetBlockByNumber(DefaultBlockParameterNumber(BigInteger.ONE), true).send().result

        assertThat(result.number).isEqualTo(BigInteger.ONE)
        assertThat(result.hash).isEqualTo("0x0102")

        val expected = responseTransaction()

        assertThat(result.transactions).isEqualTo(listOf(expected))
    }

    @Test
    fun `check a transaction can be retrieved by hash`() {
        val result = web3.ethGetTransactionByHash("0x112233").send().result

        val expected = responseTransaction()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `check a transaction can be retrieved by block hash and index`() {
        val result = web3.ethGetTransactionByBlockHashAndIndex("0x0102", BigInteger.ZERO).send().result

        val expected = responseTransaction()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `check a transaction can be retrieved by block number and index`() {
        val result = web3.ethGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameterNumber(BigInteger.ONE),
            BigInteger.ZERO
        ).send().result

        val expected = responseTransaction()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `check a transaction receipt can be retrieved`() {
        val result = web3.ethGetTransactionReceipt("0x112233").send().result

        val expected = Web3TransactionReceipt(
            "0x112233",
            "0x0",
            "0x0102",
            "0x1",
            "0x0",
            "0x0",
            null,
            "0x0",
            "0x1",
            "0x00000000000000000000000000000000000aaaaa",
            "0x00000000000000000000000000000000000bbbbb",
            emptyList(),
            "0x0"
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `check an empty set of pending transactions can be received`() {
        //val result = web3.ethPendingTransactionHashFlowable()
        //web3.pen
    }

    private fun responseTransaction(): org.web3j.protocol.core.methods.response.Transaction {
        return Web3ResponseTransaction(
            "0x112233",
            "0x0",
            "0x0102",
            "0x1",
            "0x0",
            "0x00000000000000000000000000000000000aaaaa",
            "0x00000000000000000000000000000000000bbbbb",
            "0x1",
            "0xa",
            "0xa",
            "0x",
            null,
            null,
            null,
            "0x0",
            "0x0",
            0
        )
    }


}