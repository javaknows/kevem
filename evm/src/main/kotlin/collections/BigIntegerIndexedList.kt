package org.kevm.evm.collections

import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import java.math.BigInteger

/**
backed by LinkedHashMap (mutable)
copy-on-write, effectively immutable since mutability is confined

 TODO - back by list for Int sized index values
 */
open class BigIntegerIndexedList<T>(
    private val default: T,
    private val backing: LinkedHashMap<BigInteger, T> = LinkedHashMap()
) {
    operator fun get(key: BigInteger): T = backing.getOrDefault(key, default)

    fun write(key: BigInteger, value: T): BigIntegerIndexedList<T> = copy(backing).let {
        it[key] = value
        BigIntegerIndexedList(default, it)
    }

    fun write(setIndex: BigInteger, value: List<T>): BigIntegerIndexedList<T> {
        val copy = copy(backing)
        value.zip(value.indices).forEach {
            val (v, i) = it
            copy[setIndex + i.toBigInteger()] = v
        }
        return BigIntegerIndexedList(default, copy)
    }

    fun read(fromIndex: BigInteger, num: Int): List<T> =
        (0 until num).map { offset ->
            val key = fromIndex + offset.toBigInteger()
            this[key]
        }

    fun size(): BigInteger =
        if (backing.keys.isEmpty()) BigInteger.ZERO
        else (backing.keys.max() ?: BigInteger.ZERO) + BigInteger.ONE

    operator fun plus(other: BigIntegerIndexedList<T>): BigIntegerIndexedList<T> =
        write(size(), other.backing.values.toList())

    fun indices(): List<BigInteger> = backing.keys.toList().sorted()

    fun toList(): List<T> = read(BigInteger.ZERO, size().toInt()) // TODO - watch out - can overflow, should fail

    private fun copy(original: LinkedHashMap<BigInteger, T>) = LinkedHashMap(original)

    override fun equals(other: Any?): Boolean =
        if (other is BigIntegerIndexedList<*>)
            backing == other.backing
        else false

    override fun hashCode(): Int = backing.hashCode()

    companion object {
        fun fromByteString(data: String): BigIntegerIndexedList<Byte> =
            BigIntegerIndexedList(Byte.Zero).write(BigInteger.ZERO, toByteList(data))

        fun fromBytes(data: List<Byte>): BigIntegerIndexedList<Byte> =
            BigIntegerIndexedList(Byte.Zero).write(BigInteger.ZERO, data)

        fun emptyByteList() = BigIntegerIndexedList(Byte.Zero)
    }
}
