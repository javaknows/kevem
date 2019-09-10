package com.gammadex.kevin

import java.math.BigInteger

fun stripHexPrefix(num: String) = num.replaceFirst("0x", "")

fun hexPrefix(num: String) = "0x$num"

data class Byte(val value: Int) {
    constructor(v: String) : this(Integer.parseInt(stripHexPrefix(v), 16))

    init {
        if (value > 0xFF || value < 0) throw IllegalArgumentException()
    }

    override fun toString() = hexPrefix(value.toString(16))

    companion object {
        val Zero = Byte(0)
    }
}

data class Word(val value: BigInteger)

data class Address(val value: BigInteger)

class Contract(val code: List<Byte>)

class AddressLocation(val address: Address, val balance: BigInteger, contract: Contract)

data class Block(
    val number: BigInteger,
    val difficulty: BigInteger,
    val gasLimit: BigInteger
)

data class Transaction(
    val origin: Address,
    val gasPrice: BigInteger
)

data class Log(
    val data: List<Byte>,
    val topics: List<Word> = emptyList()
)

class Memory(private val data: Map<Int, Byte> = emptyMap()) {
    operator fun get(index: Int): Byte = data.getOrDefault(index, Byte.Zero)

    fun set(index: Int, values: List<Byte>) {
        val to = index + values.size
        val memory = data + (index.until(to) zip values).toMap()

        Memory(memory)
    }
}

data class ExecutionContext(
    val currentBlock: Block,
    val currentTransaction: Transaction,
    val code: Contract,
    val callData: List<Byte>,
    val stack: List<Word> = emptyList(),
    val memory: List<Byte> = emptyList(),
    val storage: List<Word> = emptyList(),
    val logs: List<Log> = emptyList(),
    val currentLocation: BigInteger = BigInteger.ZERO
) {

}

fun execute(context: ExecutionContext): ExecutionContext {

}


fun add(w1: Word, w2: Word) = Word(w1.value + w2.value)

fun mul(w1: Word, w2: Word) = Word(w1.value * w2.value)

fun sub(w1: Word, w2: Word) = Word(w1.value - w2.value)

fun div(w1: Word, w2: Word) = Word(w1.value % w2.value)



