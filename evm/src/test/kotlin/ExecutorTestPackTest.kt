package com.gammadex.kevin

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import kotlin.test.junit5.JUnit5Asserter.fail

private val underTest = Executor()

class ExecutorTestPackTest {

    @ParameterizedTest
    @CsvFileSource(resources = ["/test_pack.tsv"], delimiter='\t')
    fun pack(function: String, expectedResult: String, arg0: String, arg1: String?, arg2: String?) {
        val opcode = Opcode.fromString(function) ?: fail("no matching opcode for '${function}'")

        val args = listOf(arg0, arg1, arg2)
            .filterNotNull()
            .map {  Word.coerceFrom(it).data }

        val context = baseExecutionContext(
            stack = Stack(args),
            contractCode = listOf(opcode.code)
        )

        val result = underTest.execute(context, context)

        Assertions.assertThat(result.stack.size()).isEqualTo(1)
        val output = Word.coerceFrom(result.stack.peek(0)).toString()

        Assertions.assertThat(output).isEqualTo(expectedResult)
    }
}
