package org.kevem.eth.rlp

import java.lang.RuntimeException

sealed class RlpNode

data class RlpString(val bytes: List<Byte>) : RlpNode() {
    override fun toString() = bytes.map { it.toInt() and 0xFF }.map { it.toString(16) }.joinToString()
}

data class RlpList(val elements: List<RlpNode>) : RlpNode() {
    override fun toString() = elements.toString()
}

class RlpDecodingException(message: String) : RuntimeException(message)

internal enum class NodeType { String, List }

internal const val longStringSize = 56
internal const val singleByteStringUpper = 0x7f
internal const val mediumStringUpper = 0xb7
internal const val mediumStringOffset = 0x80
internal const val longStringUpper = 0xbf
internal const val shortListUpper = 0xf7
internal const val shortListOffset = 0xc0
internal const val longListUpper = 0xff
