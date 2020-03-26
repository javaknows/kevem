package rlp


import org.kevem.rpc.rlp.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class RlpEncoderTest {

    @ParameterizedTest
    @ArgumentsSource(RlpEncodeExamples::class)
    internal fun `can encode all examples from the reference page`(examples: Pair<RlpNode, List<Any>>) {
        val (input, expected) = examples

        val result = RlpEncoder.encode(input)

        assertThat(result).isEqualTo(convert(expected))
    }
}