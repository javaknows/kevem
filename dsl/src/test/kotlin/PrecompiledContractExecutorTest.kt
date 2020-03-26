
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevem.evm.PrecompiledContractExecutor
import org.kevem.evm.model.Address

class PrecompiledContractExecutorTest {

    @Test
    internal fun `check address 0 is not considered precompiled`() {
        val isPrecompiled = PrecompiledContractExecutor.isPrecompiledContractCall(Address("0x0"))

        assertThat(isPrecompiled).isEqualTo(false)
    }
}