package org.kevem.common

data class Byte(val value: Int) {
    constructor(v: String) : this(Integer.parseInt(v.replaceFirst("0x", ""), 16))

    init {
        require(value in 0..0xFF)
    }

    fun repeat(times: Int): List<Byte> = (0 until times).map { this }

    fun toStringNoHexPrefix() = "0${value.toString(16)}".takeLast(2)

    fun javaByte() = value.toByte()

    infix fun or(other: Byte) = Byte((other.value or value) and 0xff)

    infix fun and(other: Byte) =
        Byte((other.value and value) and 0xff)

    override fun toString() = "0x${toStringNoHexPrefix()}"

    companion object {
        val Zero = Byte(0)
        val One = Byte(1)

        fun padRightToSize(list: List<Byte>, size: Int, pad: Byte = Zero): List<Byte> {
            return list + pad.repeat(size - list.size)
        }

        fun padRightToMultipleOf(list: List<Byte>, multiple: Int, pad: Byte = Zero): List<Byte> {
            return if (list.isEmpty()) pad.repeat(multiple)
            else {
                val appendSize =
                    if (list.size % multiple == 0) 0
                    else multiple - (list.size % multiple)

                list + pad.repeat(appendSize)
            }
        }

        fun padLeftToSize(list: List<Byte>, size: Int, pad: Byte = Zero): List<Byte> {
            return pad.repeat(size - list.size) + list
        }

        fun trimAndPadRight(list: List<Byte>, size: Int, pad: Byte = Zero): List<Byte> {
            val trimmed = list.take(size)
            return trimmed + pad.repeat(size - trimmed.size)
        }

        fun trimAndPadLeft(list: List<Byte>, size: Int, pad: Byte = Zero): List<Byte> {
            val trimmed = list.take(size)
            return pad.repeat(size - trimmed.size) + trimmed
        }
    }
}
