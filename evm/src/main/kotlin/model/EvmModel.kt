package com.gammadex.kevin.evm.model

import com.gammadex.kevin.evm.*
import java.math.BigInteger
import java.time.Clock

data class Byte(val value: Int) {
    constructor(v: String) : this(Integer.parseInt(stripHexPrefix(v), 16))

    init {
        require(value in 0..0xFF)
    }

    fun repeat(times: Int): List<Byte> = (0 until times).map { this }

    fun toStringNoHexPrefix() = "0${value.toString(16)}".takeLast(2)

    fun javaByte() = value.toByte()

    infix fun or(other: Byte) = Byte((other.value or value) and 0xff)

    infix fun and(other: Byte) =
        Byte((other.value and value) and 0xff)

    override fun toString() = hexPrefix(toStringNoHexPrefix())

    companion object {
        val Zero = Byte(0)
        val One = Byte(1)
    }
}

data class Word(val data: List<Byte>, private val numBytes: Int = 32) {
    init {
        require(data.size == numBytes) { "Word data needs to be ${numBytes} bytes exactly but is ${data.size}" }
    }

    fun toBigInt() = BigInteger(toStringNoHexPrefix(), 16)

    fun toInt() = toBigInt().toInt()

    fun toAddress() = Address(Word(data.takeLast(20), 20).toBigInt())

    fun toBoolean() = data.last() != Byte.Zero

    fun toStringNoHexPrefix() = data.joinToString("") { it.toStringNoHexPrefix() }

    override fun toString() = hexPrefix(toStringNoHexPrefix())

    fun not() = Word(data.map { it.value }.map { it.inv() and 0xff }.map {
        Byte(it)
    }, numBytes)

    fun or(other: Word): Word =
        if (numBytes != other.numBytes) throw Exception("Mismatched word sizes")
        else coerceFrom(
            (data zip other.data).map { it.first or it.second },
            numBytes
        )

    fun and(other: Word): Word =
        if (numBytes != other.numBytes) throw Exception("Mismatched word sizes")
        else coerceFrom(
            (data zip other.data).map { it.first and it.second },
            numBytes
        )

    companion object {
        val Zero = Word(Byte.Zero.repeat(32))
        val One = Word(Byte.Zero.repeat(31) + listOf(Byte.One))

        fun coerceFrom(data: List<Byte>, numBytes: Int = 32): Word {
            val size = data.size.coerceAtMost(numBytes)
            val numRequiredForPadding = numBytes - size
            val padding = Byte.Zero.repeat(numRequiredForPadding)

            return Word(padding + data.takeLast(numBytes), numBytes)
        }

        fun coerceFrom(num: BigInteger, numBytes: Int = 32) =
            coerceFrom(toBytes(num), numBytes)

        fun coerceFrom(num: Int, numBytes: Int = 32) =
            coerceFrom(num.toBigInteger(), numBytes)

        fun coerceFrom(num: Long, numBytes: Int = 32) =
            coerceFrom(num.toBigInteger(), numBytes)

        fun coerceFrom(byte: Byte, numBytes: Int = 32) = Word(
            Byte.Zero.repeat(31) + listOf(byte),
            numBytes
        )

        fun coerceFrom(bool: Boolean, numBytes: Int = 32): Word {
            val byte = if (bool) Byte.One else Byte.Zero

            return coerceFrom(listOf(byte), numBytes)
        }

        fun coerceFrom(str: String, numBytes: Int = 32): Word =
            coerceFrom(
                ("0000000000000000000000000000000000000000000000000000000000000000" + stripHexPrefix(
                    str
                ))
                    .takeLast(64)
                    .chunked(2)
                    .map { Byte(it) },
                numBytes
            )

        fun max(numBytes: Int = 32) =
            Word(Byte(0xff).repeat(numBytes))
    }
}

data class Address(val value: BigInteger) {
    constructor(v: String) : this(BigInteger(stripHexPrefix(v), 16))

    fun toWord() = Word.coerceFrom(value)

    override fun toString() = Word.coerceFrom(value, 20).toString()
}

open class Contract(val code: List<Byte>, val address: Address) {
    operator fun get(index: Int): Byte {
        require(index in code.indices) { "out of range" }

        return code[index]
    }

    fun copy(code: List<Byte>? = null, address: Address? = null) =
        Contract(code ?: this.code, address ?: this.address)
}

class EmptyContract(address: Address): Contract(emptyList(), address)

data class AddressLocation(val address: Address, val balance: BigInteger, val contract: Contract?)

class EvmState(private val addresses: Map<Address, AddressLocation> = emptyMap()) {
    fun balanceOf(address: Address) = addresses[address]?.balance ?: BigInteger.ZERO

    fun codeAt(address: Address): List<Byte> = addresses[address]?.contract?.code ?: emptyList()

    fun contractAt(address: Address): Contract? = addresses[address]?.contract

    fun balanceAndContractAt(address: Address): Pair<BigInteger, Contract?> = Pair(
        balanceOf(address), contractAt(address)
    )

    fun updateBalance(address: Address, balance: BigInteger): EvmState {
        val location = addresses[address]?.copy(balance = balance) ?: AddressLocation(
            address,
            balance,
            null
        )
        return EvmState(addresses + Pair(address, location))
    }

    fun updateContract(address: Address, contract: Contract): EvmState {
        val location = addresses[address]?.copy(contract = contract) ?: AddressLocation(
            address,
            BigInteger.ZERO,
            contract
        )
        return EvmState(addresses + Pair(address, location))
    }

    fun updateBalanceAndContract(address: Address, balance: BigInteger, contract: Contract): EvmState {
        val location = addresses[address]?.copy(balance = balance) ?: AddressLocation(
            address,
            balance,
            contract
        )
        return EvmState(addresses + Pair(address, location))
    }
}

class Memory(private val data: Map<Int, Byte> = emptyMap()) {
    operator fun get(index: Int): Byte = data.getOrDefault(index,
        Byte.Zero
    )

    fun get(index: Int, length: Int): List<Byte> = index.until(index + length).map { this[it] }

    fun set(index: Int, values: List<Byte>): Memory {
        val to = (index + values.size).coerceAtLeast(0)
        val memory = data + (index.until(to) zip values).toMap()

        return Memory(memory)
    }

    fun maxIndex() = data.keys.max()
}

// TODO - move Storage inside Contract rather than being part of context
class Storage(private val data: Map<Int, Word> = emptyMap()) {
    operator fun get(index: Int): Word = data.getOrDefault(index,
        Word.Zero
    )

    fun set(index: Int, value: Word): Storage {
        val newData = data + (index to value)

        return Storage(newData)
    }
}

class Stack(private val backing: List<List<Byte>> = emptyList()) {

    fun push(data: List<Byte>): Stack =
        Stack(backing.plusElement(data))

    fun pop(): Pair<List<Byte>, Stack> {
        val last = backing.last()
        val remaining = backing.dropLast(1)

        return Pair(last, Stack(remaining))
    }

    fun pop(num: Int): Pair<List<List<Byte>>, Stack> {
        val last = backing.takeLast(num)
        val remaining = backing.dropLast(num)

        return Pair(last, Stack(remaining))
    }

    fun pushWord(word: Word) = push(word.data.dropWhile { it == Byte.Zero })

    fun popWord(): Pair<Word, Stack> {
        val (data, stack) = pop()

        return Pair(Word.coerceFrom(data), stack)
    }

    fun popWords(num: Int): Pair<List<Word>, Stack> {
        val (datas, stack) = pop(num)

        return Pair(datas.map { Word.coerceFrom(it) }, stack)
    }

    fun peek(num: Int): List<Byte> = backing.reversed()[num]

    fun peekWord(num: Int = 0) = Word.coerceFrom(peek(num))

    fun set(index: Int, data: List<Byte>): Stack {
        val newBacking = backing.reversed().toMutableList().apply {
            this[index] = data
        }.reversed().toList()

        return Stack(newBacking)
    }

    fun size() = backing.size
}

enum class ErrorCode { None, INVALID_INSTRUCTION, OUT_OF_GAS }

data class EvmError(val code: ErrorCode, val message: String?) {
    companion object {
        val None = EvmError(ErrorCode.None, null)
    }
}

enum class CallType { INITIAL, CALL, CALLCODE, STATICCALL, DELEGATECALL }

data class CallContext(
    val caller: Address,
    val callData: List<Byte>,
    val contract: Contract,
    val type: CallType,
    val value: BigInteger,
    val code: List<Byte>,
    val callingContext: ExecutionContext? = null,
    val gasRemaining: BigInteger = BigInteger.ZERO,
    val returnLocation: Int = 0,
    val returnSize: Int = 0,
    val stack: Stack = Stack(),
    val memory: Memory = Memory(),
    val storage: Storage = Storage(),
    val currentLocation: Int = 0
)

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

data class ExecutionContext(
    val currentBlock: Block,
    val currentTransaction: Transaction,
    val coinBase: Address,
    val callStack: List<CallContext> = emptyList(),
    val evmState: EvmState = EvmState(),
    val logs: List<Log> = emptyList(),
    val completed: Boolean = false,
    val lastReturnData: List<Byte> = emptyList(),
    val clock: Clock = Clock.systemUTC(),
    val previousBlocks: Map<BigInteger, Word> = emptyMap(),
    val addressGenerator: AddressGenerator = DefaultAddressGenerator(), // TODO - make a dependency rather than in ctx
    val lastCallError: EvmError = EvmError.None
) {
    val currentCallContext: CallContext
        get() = callStack.last()

    val stack: Stack
        get() = currentCallContext.stack

    val storage: Storage
        get() = currentCallContext.storage

    val memory: Memory
        get() = currentCallContext.memory

    val currentLocation: Int
        get() = currentCallContext.currentLocation

    fun updateCurrentCallContext(
        stack: Stack? = null,
        memory: Memory? = null,
        storage: Storage? = null,
        currentLocation: Int? = null,
        gasRemaining: BigInteger? = null
    ): ExecutionContext {
        val call = currentCallContext
        val newCall = currentCallContext.copy(
            stack = stack ?: call.stack,
            memory = memory ?: call.memory,
            storage = storage ?: call.storage,
            currentLocation = currentLocation ?: call.currentLocation,
            gasRemaining = gasRemaining ?: call.gasRemaining
        )

        return replaceCurrentCallContext(newCall)
    }

    fun replaceCurrentCallContext(callContext: CallContext): ExecutionContext {
        val calls = callStack.dropLast(1) + callContext

        return copy(callStack = calls)
    }
}
