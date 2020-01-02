package org.kevm.evm.model

import org.kevm.evm.*
import java.math.BigInteger
import java.time.Instant
import kotlin.math.max

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

open class Contract(val code: List<Byte> = emptyList(), val storage: Storage = Storage()) {
    operator fun get(index: Int): Byte {
        require(index in code.indices) { "out of range" }

        return code[index]
    }

    fun copy(code: List<Byte>? = null, storage: Storage? = null) =
        Contract(code ?: this.code, storage ?: this.storage)
}

data class Account(
    val address: Address,
    val balance: BigInteger = BigInteger.ZERO,
    val contract: Contract? = null,
    val nonce: BigInteger = BigInteger.ZERO
)

class Accounts(private val addresses: Map<Address, Account> = emptyMap()) {
    fun balanceOf(address: Address) = addresses[address]?.balance ?: BigInteger.ZERO

    fun codeAt(address: Address): List<Byte> = addresses[address]?.contract?.code ?: emptyList()

    fun contractAt(address: Address): Contract? = addresses[address]?.contract

    fun storageAt(address: Address, index: BigInteger): Word =
        addresses[address]?.contract?.storage?.get(index) ?: Word.Zero

    fun nonceOf(address: Address): BigInteger = addresses[address]?.nonce ?: BigInteger.ZERO

    fun balanceAndContractAt(address: Address): Pair<BigInteger, Contract?> = Pair(
        balanceOf(address), contractAt(address)
    )

    fun updateBalance(address: Address, balance: BigInteger): Accounts {
        val location = addresses[address]?.copy(balance = balance) ?: Account(
            address,
            balance,
            null
        )
        return Accounts(addresses + Pair(address, location))
    }

    fun updateNonce(address: Address, nonce: BigInteger): Accounts {
        val location = addresses[address]?.copy(nonce = nonce) ?: Account(
            address,
            BigInteger.ZERO,
            null,
            nonce
        )
        return Accounts(addresses + Pair(address, location))
    }

    fun incrementNonce(address: Address): Accounts {
        val newNonce = nonceOf(address) + BigInteger.ONE
        return updateNonce(address, newNonce)
    }

    fun updateStorage(address: Address, index: BigInteger, value: Word): Accounts {
        val account = addresses[address] ?: Account(address)
        val contract = account.contract ?: Contract()

        val newStorage = contract.storage.set(index, value)
        val newContract = contract.copy(storage = newStorage)
        val newAccount = account.copy(contract = newContract)

        return Accounts(addresses + Pair(address, newAccount))
    }

    fun updateContract(address: Address, contract: Contract): Accounts {
        val location = addresses[address]?.copy(contract = contract) ?: Account(
            address,
            BigInteger.ZERO,
            contract
        )
        return Accounts(addresses + Pair(address, location))
    }

    fun updateBalanceAndContract(address: Address, balance: BigInteger, contract: Contract): Accounts {
        val location = addresses[address]?.copy(balance = balance) ?: Account(
            address,
            balance,
            contract
        )
        return Accounts(addresses + Pair(address, location))
    }

    fun accountExists(address: Address) = address in addresses.keys

    fun removeAccount(address: Address) = Accounts(addresses - address)
}

// TODO - indexes/lengths should be BigInteger
class Memory(private val data: Map<Int, Byte> = emptyMap(), val maxIndex: Int? = null) {
    fun peek(index: Int): Byte = data.getOrDefault(
        index,
        Byte.Zero
    )

    fun peek(index: Int, length: Int): List<Byte> = index.until(index + length).map { peek(it) }

    fun read(index: Int, length: Int): Pair<List<Byte>, Memory> =
        if (length == 0) Pair(emptyList(), this)
        else {
            val max = getMaxIndex(index, length)
            val mem = peek(index, length)

            Pair(mem, Memory(data, max))
        }

    fun write(index: Int, values: List<Byte>): Memory =
        if (values.isEmpty()) this
        else {
            val to = (index + values.size).coerceAtLeast(0)
            val memory = data + (index.until(to) zip values).toMap()
            val max = getMaxIndex(index, values.size)

            Memory(memory, max)
        }

    private fun getMaxIndex(index: Int, length: Int) = max(maxIndex ?: 0, index + length)
}

class Storage(private val data: Map<BigInteger, Word> = emptyMap()) {
    operator fun get(index: BigInteger): Word = data.getOrDefault(
        index,
        Word.Zero
    )

    fun set(index: BigInteger, value: Word): Storage {
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

        return Pair(datas.map { Word.coerceFrom(it) }.reversed(), stack)
    }

    fun peek(num: Int): List<Byte> = backing.reversed()[num]

    fun peekWords(num: Int): List<Word> = backing.takeLast(num)
        .map { Word.coerceFrom(it) }
        .reversed()

    fun peekWord(num: Int = 0) = Word.coerceFrom(peek(num))

    fun set(index: Int, data: List<Byte>): Stack {
        val newBacking = backing.reversed().toMutableList().apply {
            this[index] = data
        }.reversed().toList()

        return Stack(newBacking)
    }

    fun size() = backing.size
}

enum class ErrorCode {
    None,
    INVALID_INSTRUCTION,
    OUT_OF_GAS,
    STACK_UNDERFLOW,
    STACK_OVERFLOW,
    STATE_CHANGE_STATIC_CALL,
    INVALID_JUMP_DESTINATION,
    INSUFFICIENT_FUNDS,
    CONTRACT_EXISTS
}

data class EvmError(val code: ErrorCode, val message: String?) {
    companion object {
        val None = EvmError(ErrorCode.None, null)
    }
}

enum class CallType { INITIAL, CALL, CALLCODE, STATICCALL, DELEGATECALL }

data class CallContext(
    val caller: Address,
    val callData: List<Byte>,
    val type: CallType,
    val value: BigInteger,
    val code: List<Byte>,
    val callingContext: ExecutionContext? = null,
    val gas: BigInteger = BigInteger.ZERO,
    val returnLocation: Int = 0,
    val returnSize: Int = 0,
    val stack: Stack = Stack(),
    val memory: Memory = Memory(),
    val currentLocation: Int = 0,
    val storageAddress: Address? = null,
    val contractAddress: Address? = null,
    val gasUsed: BigInteger = BigInteger.ZERO
) {
    val gasRemaining: BigInteger
        get() = gas - gasUsed
}

data class Block(
    val number: BigInteger,
    val difficulty: BigInteger,
    val gasLimit: BigInteger,
    val timestamp: Instant
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
    val accounts: Accounts = Accounts(),
    val logs: List<Log> = emptyList(),
    val completed: Boolean = false,
    val lastReturnData: List<Byte> = emptyList(),
    val previousBlocks: Map<BigInteger, Word> = emptyMap(),
    val lastCallError: EvmError = EvmError.None,
    val refunds: Map<Address, BigInteger> = emptyMap(),
    val suicidedAccounts: List<Address> = emptyList(),
    val gasUsed: BigInteger = BigInteger.ZERO
) {
    val currentCallCtx: CallContext
        get() = callStack.last()

    val currentCallContextOrNull: CallContext?
        get() = callStack.lastOrNull()

    val stack: Stack
        get() = currentCallCtx.stack

    val memory: Memory
        get() = currentCallCtx.memory

    val currentLocation: Int
        get() = currentCallCtx.currentLocation

    // TODO - replace with updateCurrentCallCtxIfPresent
    fun updateCurrentCallCtx(
        stack: Stack? = null,
        memory: Memory? = null,
        currentLocation: Int? = null,
        gasUsed: BigInteger? = null
    ): ExecutionContext {
        val call = currentCallCtx
        val newCall = currentCallCtx.copy(
            stack = stack ?: call.stack,
            memory = memory ?: call.memory,
            currentLocation = currentLocation ?: call.currentLocation,
            gasUsed = gasUsed ?: call.gasUsed
        )

        return replaceCurrentCallCtx(newCall)
    }

    private fun replaceCurrentCallCtx(callContext: CallContext): ExecutionContext {
        val calls = callStack.dropLast(1) + callContext

        return copy(callStack = calls)
    }

    fun updateCurrentCallCtxIfPresent(updateOperation: (CallContext) -> CallContext): ExecutionContext =
        if (callStack.isNotEmpty()) {
            val newCall = updateOperation(callStack.last())
            val newCallStack = callStack.dropLast(1) + newCall
            copy(callStack = newCallStack)
        } else this

    fun updatePreviousCallCtxIfPresent(updateOperation: (CallContext) -> CallContext): ExecutionContext =
        if (callStack.size >= 2) {
            val call = callStack[callStack.size - 2]
            val newCall = updateOperation(call)
            val newCallStack = callStack.dropLast(2) + newCall + callStack.last()
            copy(callStack = newCallStack)
        } else this

    fun refund(address: Address, value: BigInteger): ExecutionContext {
        val totalRefund = refunds.getOrDefault(address, BigInteger.ZERO) + value

        return copy(
            refunds = refunds + Pair(address, totalRefund)
        )
    }
}

data class WorldState(val blocks: List<MinedBlock>, val accounts: Accounts, val coinbase: Address)

data class TransactionMessage(
    val from: Address,
    val to: Address?,
    val value: BigInteger,
    val gasPrice: BigInteger,
    val gasLimit: BigInteger,
    val data: List<Byte> = emptyList(),
    val nonce: BigInteger,
    val hash: List<Byte> = emptyList() // TODO - this doesn't really fit here - maybe move it
)

enum class ResultStatus { FAILED, COMPLETE, REJECTED }

data class TransactionResult(
    val status: ResultStatus,
    val gasUsed: BigInteger,
    val logs: List<Log> = emptyList(),
    val created: Address? = null,
    val returnData: List<Byte> = emptyList() // CALL only
)

data class MinedTransaction(
    val message: TransactionMessage,
    val result: TransactionResult
)

data class MinedBlock(
    val block: Block,
    val gasUsed: BigInteger,
    val hash: List<Byte>,
    val transactions: List<MinedTransaction> = emptyList() // TODO - index should be BigInteger
)