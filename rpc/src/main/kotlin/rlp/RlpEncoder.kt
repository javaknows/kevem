package com.gammadex.kevin.rpc.rlp

/**
 * RLP encoder as per  https://github.com/ethereum/wiki/wiki/RLP
 */
object RlpEncoder {

    fun encode(node: RlpNode): List<Byte> = when (node) {
        is RlpString ->
            if (node.bytes.size == 1 && node.bytes[0].toInt() < mediumStringOffset) node.bytes
            else encodeLength(node.bytes.size, mediumStringOffset) + node.bytes
        is RlpList -> {
            val output = node.elements.fold(emptyList<Byte>()) { acc, item ->
                acc + encode(item)
            }
            encodeLength(output.size, shortListOffset) + output
        }
    }

    private fun encodeLength(length: Int, offset: Int): List<Byte> = when {
        length < longStringSize -> listOf((length + offset).toByte())
        else -> {
            val bl = toBinary(length)
            toBytes(bl.size + offset + 55) + bl
        }
    }

    private fun toBinary(x: Int): List<Byte> =
        if (x == 0) emptyList()
        else toBinary((x / 256)) + (x % 256).toChar().toByte()

    private fun toBytes(value: Int): List<Byte> = value.toString(16)
        .reversed()
        .chunked(2)
        .map { it.reversed() }
        .reversed()
        .map { it.toInt(16).toByte() }
}