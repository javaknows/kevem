import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevem.evm.collections.BigIntegerIndexedList

import org.kevem.evm.model.*
import org.kevem.rpc.*
import test.TestObjects
import java.math.BigInteger

class StandardEvmOperationsTest {

    @Test
    fun `getBalance works all block reference types`() {
        prepareAndCheckForAllBlockTypes({ ws ->
            ws.copy(accounts = ws.accounts.updateBalance(Address("0xABC"), BigInteger.TEN))
        }, { underTest, blockReference ->
            val balance = underTest.getBalance(Address("0xABC"), blockReference)
            assertThat(balance).isEqualTo(BigInteger.TEN)
        })
    }

    @Test
    fun `getStorageAt works all block reference types`() {
        prepareAndCheckForAllBlockTypes({ ws ->
            ws.copy(accounts = ws.accounts.updateStorage(Address("0xABC"), BigInteger.ZERO, Word.coerceFrom("0x1234")))
        }, { underTest, blockReference ->
            val storage = underTest.getStorageAt(Address("0xABC"), BigInteger.ZERO, blockReference)
            assertThat(storage).isEqualTo(Word.coerceFrom("0x1234"))
        })
    }

    @Test
    fun `getCode works all block reference types`() {
        prepareAndCheckForAllBlockTypes({ ws ->
            ws.copy(accounts = ws.accounts.updateContract(Address("0xABC"), Contract(BigIntegerIndexedList.fromByteString("0x1234"))))
        }, { underTest, blockReference ->
            val code = underTest.getCode(Address("0xABC"), blockReference)
            assertThat(code).isEqualTo(BigIntegerIndexedList.fromByteString("0x1234").toList())
        })
    }

    private fun prepareAndCheckForAllBlockTypes(
        prepareWorldState: (ws: WorldState) -> WorldState,
        executeCheckForBlock: (StandardEvmOperations, br: BlockReference) -> Unit
    ) {
        executeCheckForBlock(pendingWorldState(op = prepareWorldState), PendingBlock)
        executeCheckForBlock(latestWorldState(op = prepareWorldState), LatestBlock)
        executeCheckForBlock(earliestWorldState(op = prepareWorldState), EarliestBlock)
        executeCheckForBlock(byNumberWorldState(op = prepareWorldState), NumericBlock(BigInteger.ONE))
    }

    private fun earliestWorldState(
        c: EvmConfig = EvmConfig(),
        op: (ws: WorldState) -> WorldState
    ): StandardEvmOperations =
        StandardEvmOperations(mock { on { getEarliestWorldState() } doReturn op(TestObjects.worldState) }, c)

    private fun latestWorldState(
        c: EvmConfig = EvmConfig(),
        op: (ws: WorldState) -> WorldState
    ): StandardEvmOperations =
        StandardEvmOperations(mock { on { getWorldState() } doReturn op(TestObjects.worldState) }, c)

    private fun pendingWorldState(
        c: EvmConfig = EvmConfig(),
        op: (ws: WorldState) -> WorldState
    ): StandardEvmOperations =
        StandardEvmOperations(mock { on { getPendingWorldState() } doReturn op(TestObjects.worldState) }, c)


    private fun byNumberWorldState(
        c: EvmConfig = EvmConfig(),
        op: (ws: WorldState) -> WorldState
    ): StandardEvmOperations =
        StandardEvmOperations(
            mock { on { findWorldStateAtBlock(BigInteger.ONE) } doReturn op(TestObjects.worldState) },
            c
        )
}