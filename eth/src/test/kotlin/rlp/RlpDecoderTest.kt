package org.kevem.eth.rlp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

/**
 * Test examples from https://github.com/ethereum/wiki/wiki/RLP
 */
class RlpDecoderTest {

    @ParameterizedTest
    @ArgumentsSource(RlpEncodeExamples::class)
    internal fun `can decode all examples from the reference page`(examples: Pair<RlpNode, List<Any>>) {
        val (expected, input) = examples

        val result = RlpDecoder.decode(convert(input))

        assertThat(result).isEqualTo(expected)
    }
}
