package org.kevm.evm.collections

import org.kevm.evm.model.Byte
import java.math.BigInteger

/**
backed by LinkedHasMap (mutable)
copy-on-write, effectively immutable since mutability is confined
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

    private fun copy(original: LinkedHashMap<BigInteger, T>) = LinkedHashMap(original)
}

class BigIntegerIndexedByteList : BigIntegerIndexedList<Byte>(Byte.Zero)