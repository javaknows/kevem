import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevem.evm.model.*
import org.kevem.evm.toByteList
import org.kevem.evm.toStringHexPrefix
import org.kevem.rpc.*
import java.math.BigInteger
import java.time.Instant

class StandardRPCTest {

    private val config = mock<AppConfig> {}
    private val localAccounts = mock<LocalAccounts> {}
    private val transactionSigner = mock<TransactionSigner> {}

    @Test
    fun `check cumulative gas is sum of previous transactions (inclusive)`() {
        val tx0 = createTxWithHashAndGasUsed("0xA0", 10, ResultStatus.COMPLETE)
        val tx1 = createTxWithHashAndGasUsed("0xB0", 21, ResultStatus.COMPLETE)
        val block = createBlockWithTransactions(tx0, tx1)

        checkCumulativeGas("0xB0", 31, block)
    }

    @Test
    fun `check cumulative gas only includes COMPLETE transactions`() {
        val tx0 = createTxWithHashAndGasUsed("0xA0", 10, ResultStatus.FAILED)
        val tx1 = createTxWithHashAndGasUsed("0xB0", 21, ResultStatus.COMPLETE)
        val block = createBlockWithTransactions(tx0, tx1)

        checkCumulativeGas("0xB0", 21, block)
    }

    @Test
    fun `check cumulative gas does not include following transactions`() {
        val tx0 = createTxWithHashAndGasUsed("0xA0", 10, ResultStatus.COMPLETE)
        val tx1 = createTxWithHashAndGasUsed("0xB0", 21, ResultStatus.COMPLETE)
        val block = createBlockWithTransactions(tx0, tx1)

        checkCumulativeGas("0xA0", 10, block)
    }

    private fun checkCumulativeGas(txHash: String, expectedValue: Int, block: MinedBlock) {
        val standardEvmOperations = mock<StandardEvmOperations> {
            val tx = block.transactions.find { it.message.hash == toByteList(txHash) }!!
            on { getTransactionReceipt(any()) } doReturn Pair(tx, block)
        }

        val underTest = StandardRPC(standardEvmOperations, config, localAccounts, transactionSigner)
        val txReceipt = underTest.ethGetTransactionReceipt(txHash)

        assertThat(txReceipt!!.cumulativeGasUsed).isEqualTo(expectedValue.toStringHexPrefix())
    }

    private fun createTxWithHashAndGasUsed(hash: String, gasUsed: Int, status: ResultStatus): MinedTransaction =
        MinedTransaction(
            TransactionMessage(
                from = Address("0xc94770007dda54cf92009bff0de90c06f603a09f"),
                to = Address("0x0"),
                value = BigInteger.ZERO,
                gasPrice = BigInteger.ZERO,
                gasLimit = BigInteger.ZERO,
                nonce = BigInteger.ZERO,
                hash = toByteList(hash)
            ),
            TransactionResult(
                status = status,
                gasUsed = gasUsed.toBigInteger(),
                logs = emptyList()
            )
        )

    private fun createBlockWithTransactions(vararg txs: MinedTransaction): MinedBlock = MinedBlock(
        Block(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, Instant.MIN),
        BigInteger.ZERO,
        emptyList(),
        txs.toList()
    )
}