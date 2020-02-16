
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.kevm.evm.PrecompiledContractExecutor
import org.kevm.evm.model.Address

class PrecompiledContractExecutorTest {

    @Test
    internal fun `check address 0 is not considered precompiled`() {
        val isPrecompiled = PrecompiledContractExecutor.isPrecompiledContractCall(Address("0x0"))

        assertThat(isPrecompiled).isEqualTo(false)
    }
}