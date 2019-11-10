package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.model.Memory
import com.gammadex.kevin.evm.model.Stack
import com.gammadex.kevin.evm.model.Word
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import kotlin.test.junit5.JUnit5Asserter.fail

class ExecutorTestPackTest {

    private val underTest = Executor()

    @ParameterizedTest
    @CsvFileSource(resources = ["/numeric_test_pack.tsv"], delimiter='\t')
    fun pack(function: String, expectedResult: String, arg0: String, arg1: String?, arg2: String?) {
        if (function.startsWith("#")) return

        val opcode = Opcode.fromName(function) ?: fail("no matching opcode for '${function}'")

        val args = listOf(arg0, arg1, arg2)
            .filterNotNull()
            .map {  Word.coerceFrom(it).data }

        val context = baseExecutionContext(
            stack = Stack(args.reversed()),
            contractCode = listOf(opcode.code)
        )

        val result = underTest.executeNextOpcode(context)

        Assertions.assertThat(result.stack.size()).isEqualTo(1)
        val output = Word.coerceFrom(result.stack.peek(0)).toString()

        Assertions.assertThat(output).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/sha3_test_pack.tsv"], delimiter='\t')
    fun `check sha3 matches ganache output`(input: String, expectedResult: String) {
        val data = Word.coerceFrom(input).data

        val context = baseExecutionContext(
            stack = Stack(
                listOf(
                    listOf(Byte(0x20)),
                    listOf(Byte(0))
                )
            ),
            contractCode = listOf(Opcode.SHA3.code),
            memory = Memory().write(0, data)
        )

        val result = underTest.executeNextOpcode(context)

        Assertions.assertThat(result.stack.size()).isEqualTo(1)
        val output = Word.coerceFrom(result.stack.peek(0)).toString()

        Assertions.assertThat(output).isEqualTo(expectedResult)
    }
}
