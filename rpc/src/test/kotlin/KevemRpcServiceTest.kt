import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.kevem.common.CategorisedKevemException
import org.kevem.rpc.ErrorDTO
import org.kevem.rpc.KevemRpcService
import org.kevem.rpc.module.*
import java.lang.RuntimeException

class KevemRpcServiceTest {

    @Test
    internal fun `module chain executes only one matching request`() {
        val firstModule = mock<RpcModule> {
            on { process(any(), any()) } doReturn ClientVersionResponse(ClientVersionRequest("", "", 1), "")
        }
        val secondModule = mock<RpcModule> {
            on { process(any(), any()) } doReturn ClientVersionResponse(ClientVersionRequest("", "", 2), "")
        }
        val underTest = KevemRpcService(
            listOf(firstModule, secondModule), mock()
        )

        val customer = underTest.processRequest(ClientVersionRequest("", "", 0)) as ClientVersionResponse

        assertThat(customer.id).isEqualTo(1)
        verifyZeroInteractions(secondModule)
    }

    @Test
    internal fun `exception returns unexpected error for uncategorised exception`() =
        throwExceptionDuringProcessThenAssert(RuntimeException("foo")) {
            assertThat(it.message).isEqualTo("unexpected error: foo")
        }

    @Test
    internal fun `exception returns exact message with code for categorised response`() =
        throwExceptionDuringProcessThenAssert(CategorisedKevemException("foo", 100)) {
            assertThat(it.message).isEqualTo("foo")
            assertThat(it.code).isEqualTo(100)
        }

    @Test
    internal fun `exception returns unknown error if exception message is null`() =
        throwExceptionDuringProcessThenAssert(RuntimeException(null as String?)) {
            assertThat(it.message).isEqualTo("unknown error")
        }

    private fun throwExceptionDuringProcessThenAssert(exception: Exception, op: (error: ErrorDTO) -> Unit) {
        val errorModule = mock<RpcModule> {
            on { process(any(), any()) } doThrow exception
        }
        val underTest = KevemRpcService(
            listOf(errorModule), mock()
        )

        val response = underTest.processRequest(ClientVersionRequest("", "", 0))

        val result = response!!.result
        if (result is ErrorDTO) op(result)
        else fail("invalid response result")
    }
}