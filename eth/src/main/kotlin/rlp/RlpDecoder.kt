package org.kevem.eth.rlp

/**
 * RLP decoder as per  https://github.com/ethereum/wiki/wiki/RLP
 */
object RlpDecoder {

    fun decode(input: List<Byte>): RlpNode =
        decodeAcc(input).firstOrNull() ?: throw RlpDecodingException("input doesn't conform to RLP encoding form")

    private fun decodeAcc(input: List<Byte>, acc: List<RlpNode> = emptyList()): List<RlpNode> =
        if (input.isEmpty()) acc
        else {
            val (offset, dataLen, type) = decodeLength(input)
            when (type) {
                NodeType.String ->
                    decodeAcc(emptyList(), acc + RlpString(input.drop(offset).take(dataLen)))
                NodeType.List -> {
                    val list = RlpList(decodeAcc(input.drop(offset).take(dataLen)))
                    val remaining = decodeAcc(input.drop(offset + dataLen), acc)

                    listOf(list) + remaining
                }
            }
        }

    private fun decodeLength(input: List<Byte>): Triple<Int, Int, NodeType> {
        val length = input.size
        val prefix = input[0].toInt() and 0xFF

        return when {
            prefix <= singleByteStringUpper -> Triple(0, 1, NodeType.String)
            prefix <= mediumStringUpper && length > prefix - mediumStringOffset -> {
                val strLen = prefix - mediumStringOffset
                if (strLen == 1 && input[1].toInt() <= singleByteStringUpper) throw RlpDecodingException("single byte below 128 must be encoded as itself")
                else Triple(1, strLen, NodeType.String)
            }
            prefix <= longStringUpper
                    && length > prefix - mediumStringUpper
                    && length > prefix - mediumStringUpper + toInteger(input.drop(1).take(prefix - mediumStringUpper)) -> {

                val lenOfStrLen = prefix - mediumStringUpper
                if (input[1].toInt() == 0) throw RlpDecodingException("multi-byte length must have no leading zero")
                else {
                    val strLen = toInteger(input.drop(1).take(lenOfStrLen))
                    if (strLen < 56) throw RlpDecodingException("length below 56 must be encoded in one byte")
                    else Triple(1 + lenOfStrLen, strLen, NodeType.String)
                }
            }
            prefix <= shortListUpper && length > prefix - shortListOffset -> {
                val listLen = prefix - shortListOffset
                Triple(1, listLen, NodeType.List)
            }
            prefix <= longListUpper
                    && length > prefix - shortListUpper
                    && length > prefix - shortListUpper + toInteger(input.drop(1).take(prefix - shortListUpper)) -> {

                val lenOfListLen = prefix - shortListUpper
                if (input[1].toInt() == 0) throw RlpDecodingException("multi-byte length must have no leading zero")
                else {
                    val listLen = toInteger(input.drop(1).take(lenOfListLen))
                    if (listLen < 56) throw RlpDecodingException("length below 56 must be encoded in one byte")
                    else Triple(1 + lenOfListLen, listLen, NodeType.List)
                }
            }
            else -> throw RlpDecodingException("input doesn't conform to RLP encoding form")
        }
    }

    private fun toInteger(bytes: List<Byte>) = bytes
        .map { it.toInt() and 0xFF }
        .map { it.toString(16) }
        .joinToString { if (it.length == 1) "0$it" else it }
        .toInt(16)
}