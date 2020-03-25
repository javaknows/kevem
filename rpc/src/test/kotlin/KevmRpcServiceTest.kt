import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevm.rpc.KevmRpcService
import org.kevm.rpc.module.*

class KevmRpcServiceTest {

    @Test
    internal fun `module chain executes only one matching request`() {
        val firstModule = mock<RpcModule> {
            on { process(any(), any()) } doReturn  ClientVersionResponse(ClientVersionRequest("", "", 1), "")
        }
        val secondModule = mock<RpcModule> {
            on { process(any(), any()) } doReturn  ClientVersionResponse(ClientVersionRequest("", "", 2), "")
        }
        val underTest = KevmRpcService(
            listOf(firstModule, secondModule), mock()
        )

        val customer = underTest.processRequest(ClientVersionRequest("", "", 0)) as ClientVersionResponse

        assertThat(customer.id).isEqualTo(1)
        verifyZeroInteractions(secondModule)
    }
}