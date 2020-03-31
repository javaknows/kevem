package org.kevem.eth

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

class PrecompiledContractExecutorTest {

    @Test
    internal fun `check a valid private key can geneate an address`() {
        val account = privateKeyToAddress("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395")

        assertThat(account).isEqualTo("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e")
    }
}