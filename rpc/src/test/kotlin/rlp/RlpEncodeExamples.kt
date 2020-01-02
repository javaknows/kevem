package rlp

import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import org.kevm.evm.toStringHexPrefix
import org.kevm.rpc.rlp.RlpList
import org.kevm.rpc.rlp.RlpString
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.lang.RuntimeException
import java.util.stream.Stream

fun convert(x: List<Any>): List<kotlin.Byte> = x.map {
    when (it) {
        is Int -> it
        is Char -> it.toInt()
        else -> throw RuntimeException("unecpected type")
    }
}.map { Byte(it).javaByte() }

/**
 * Test examples from https://github.com/ethereum/wiki/wiki/RLP
 */
class RlpEncodeExamples : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = argumentsStream(
        Pair(
            RlpString("dog".toByteArray().asList()),
            listOf(0x83, 'd', 'o', 'g')
        ),
        Pair(
            RlpString("".toByteArray().asList()),
            listOf(0x80)
        ),
        Pair(
            RlpList(emptyList()),
            listOf(0xc0)
        ),
        Pair(
            RlpString(listOf(0x00.toByte())),
            listOf(0x00)
        ),
        Pair(
            RlpString(listOf(0x0f.toByte())),
            listOf(0x0f)
        ),
        Pair(
            RlpString(toByteList(1024.toStringHexPrefix()).map { it.javaByte() }),
            listOf(0x82, 0x04, 0x00)
        ),
        Pair(
            RlpList(
                listOf(
                    RlpList(listOf()),
                    RlpList(listOf(RlpList(listOf()))),
                    RlpList(
                        listOf(
                            RlpList(listOf()),
                            RlpList(listOf(RlpList(listOf())))
                        )
                    )
                )
            ),
            listOf(0xc7, 0xc0, 0xc1, 0xc0, 0xc3, 0xc0, 0xc1, 0xc0)
        )
        ,
        Pair(
            RlpString("Lorem ipsum dolor sit amet, consectetur adipisicing elit".toByteArray().asList()),
            listOf(
                0xb8,
                0x38,
                'L',
                'o',
                'r',
                'e',
                'm',
                ' ',
                'i',
                'p',
                's',
                'u',
                'm',
                ' ',
                'd',
                'o',
                'l',
                'o',
                'r',
                ' ',
                's',
                'i',
                't',
                ' ',
                'a',
                'm',
                'e',
                't',
                ',',
                ' ',
                'c',
                'o',
                'n',
                's',
                'e',
                'c',
                't',
                'e',
                't',
                'u',
                'r',
                ' ',
                'a',
                'd',
                'i',
                'p',
                'i',
                's',
                'i',
                'c',
                'i',
                'n',
                'g',
                ' ',
                'e',
                'l',
                'i',
                't'
            )
        )
    )
}

private fun argumentsStream(vararg args: Any): Stream<out Arguments> =
    Stream.of(* args.map { arguments(it) }.toTypedArray())
