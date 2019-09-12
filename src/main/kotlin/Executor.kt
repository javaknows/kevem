package com.gammadex.kevin

import java.math.BigInteger
import java.time.Clock

fun stripHexPrefix(num: String) = num.replaceFirst("0x", "")

fun hexPrefix(num: String) = "0x$num"

data class Byte(val value: Int) {
    constructor(v: String) : this(Integer.parseInt(stripHexPrefix(v), 16))

    init {
        require(value in 0..0xFF)
    }

    fun repeat(times: Int): List<Byte> = (0 until times).map { this }

    fun toStringNoHexPrefix() = "0${value.toString(16)}".takeLast(2)

    fun javaByte() = value.toByte()

    override fun toString() = hexPrefix(toStringNoHexPrefix())

    companion object {
        val Zero = Byte(0)
        val One = Byte(1)
    }
}

data class Word(val data: List<Byte>) {
    init {
        require(data.size == numBytes) { "Word data needs to be 32 bytes exactly but is ${data.size}" }
    }

    fun toBigInt() = BigInteger(toStringNoHexPrefix(), 16)

    fun toInt() = toBigInt().toInt()

    fun toAddress() = Address(toBigInt())

    fun toBoolean() = data.last() == Byte.One

    fun toStringNoHexPrefix() = data.joinToString("") { it.toStringNoHexPrefix() }

    override fun toString() = hexPrefix(toStringNoHexPrefix())

    companion object {
        const val numBytes = 32

        val Zero = Word(Byte.Zero.repeat(numBytes))

        fun coerceFrom(data: List<Byte>): Word {
            val size = data.size.coerceAtMost(numBytes)
            val numRequiredForPadding = numBytes - size
            val padding = Byte.Zero.repeat(numRequiredForPadding)

            return Word(padding + data.takeLast(numBytes))
        }

        fun coerceFrom(num: BigInteger) = coerceFrom(toBytes(num))

        fun coerceFrom(num: Int) = coerceFrom(num.toBigInteger())

        fun coerceFrom(byte: Byte) = Word(listOf(byte))

        fun coerceFrom(bool: Boolean): Word {
            val byte = if (bool) Byte.One else Byte.Zero

            return coerceFrom(listOf(byte))
        }

        fun coerceFrom(str: String): Word = coerceFrom(str.chunked(2).map { Byte(it) })
    }
}

data class Address(val value: BigInteger) {
    fun toWord() = Word.coerceFrom(value)
}

class Contract(val code: List<Byte>, val address: Address) {
    operator fun get(index: Int): Byte {
        require(index in code.indices) { "out of range" }

        return code[index]
    }
}

class AddressLocation(val address: Address, val balance: BigInteger, val contract: Contract?)

class EvmState(private val addresses: Map<Address, AddressLocation> = emptyMap()) {
    fun balanceOf(address: Address) = addresses[address]?.balance ?: BigInteger.ZERO

    fun codeAt(address: Address): List<Byte> = addresses[address]?.contract?.code ?: emptyList()
}

class Memory(private val data: Map<Int, Byte> = emptyMap()) {
    operator fun get(index: Int): Byte = data.getOrDefault(index, Byte.Zero)

    fun get(index: Int, length: Int): List<Byte> = index.until(index + length).map { this[it] }

    fun set(index: Int, values: List<Byte>): Memory {
        val to = (index + values.size).coerceAtLeast(0)
        val memory = data + (index.until(to) zip values).toMap()

        return Memory(memory)
    }

    fun maxIndex() = data.keys.max()
}

class Storage(private val data: Map<Int, Word> = emptyMap()) {
    operator fun get(index: Int): Word = data.getOrDefault(index, Word.Zero)

    fun set(index: Int, value: Word): Storage {
        val newData = data + (index to value)

        return Storage(newData)
    }
}

class Stack(private val backing: List<List<Byte>> = emptyList()) {

    fun push(data: List<Byte>): Stack = Stack(backing.plusElement(data))

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

    fun peekWord(num: Int) = Word.coerceFrom(peek(num))

    fun set(index: Int, data: List<Byte>): Stack {
        val newBacking = backing.reversed().toMutableList().apply {
            this[index] = data
        }.reversed().toList()

        return Stack(newBacking)
    }
}

enum class CallType { CALL, CALLCODE, STATICCALL, DELEGATECALL }

data class Call(
    val caller: Address,
    val value: BigInteger,
    val valueRemaining: BigInteger,
    val callData: List<Byte>,
    val type: CallType
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
    val contract: Contract,
    val coinBase: Address,
    val callStack: List<Call> = emptyList(),
    val evmState: EvmState = EvmState(),
    val stack: Stack = Stack(),
    val memory: Memory = Memory(),
    val storage: Storage = Storage(),
    val logs: List<Log> = emptyList(),
    val currentLocation: Int = 0,
    val gasRemaining: Int = 0,
    val completed: Boolean = false,
    val lastReturnData: List<Byte> = emptyList(),
    val clock: Clock = Clock.systemUTC()
)

fun uniStackOp(executionContext: ExecutionContext, op: (w1: Word) -> Word): ExecutionContext {
    val (a, newStack) = executionContext.stack.popWord()
    val finalStack = newStack.pushWord(op(a))

    return executionContext.copy(stack = finalStack)
}

fun biStackOp(executionContext: ExecutionContext, op: (w1: Word, w2: Word) -> Word): ExecutionContext {
    val (elements, newStack) = executionContext.stack.popWords(2)
    val (a, b) = elements
    val finalStack = newStack.pushWord(op(a, b))

    return executionContext.copy(stack = finalStack)
}

fun triStackOp(executionContext: ExecutionContext, op: (w1: Word, w2: Word, w3: Word) -> Word): ExecutionContext {
    val (elements, newStack) = executionContext.stack.popWords(3)
    val (a, b, c) = elements
    val finalStack = newStack.pushWord(op(a, b, c))

    return executionContext.copy(stack = finalStack)
}

fun execute(context: ExecutionContext): ExecutionContext = with(context) {
    val opcode = Opcode.byCode[contract[currentLocation]]

    when (opcode) {
        Opcode.STOP -> context.copy(completed = true)
        Opcode.ADD -> biStackOp(context, VmMath::add)
        Opcode.MUL -> biStackOp(context, VmMath::mul)
        Opcode.SUB -> biStackOp(context, VmMath::sub)
        Opcode.DIV -> biStackOp(context, VmMath::div)
        Opcode.SDIV -> biStackOp(context, VmMath::sdiv)
        Opcode.MOD -> biStackOp(context, VmMath::mod)
        Opcode.SMOD -> biStackOp(context, VmMath::smod)
        Opcode.ADDMOD -> triStackOp(context, VmMath::addMod)
        Opcode.MULMOD -> triStackOp(context, VmMath::mulMod)
        Opcode.EXP -> biStackOp(context, VmMath::exp)
        Opcode.SIGNEXTEND -> biStackOp(context, VmMath::signExtend)
        Opcode.LT -> biStackOp(context, VmMath::lt)
        Opcode.GT -> biStackOp(context, VmMath::gt)
        Opcode.SLT -> biStackOp(context, VmMath::slt)
        Opcode.SGT -> biStackOp(context, VmMath::sgt)
        Opcode.EQ -> biStackOp(context) { a, b -> Word.coerceFrom(a == b) }
        Opcode.ISZERO -> uniStackOp(context) { w -> Word.coerceFrom(w.data.none { it != Byte.Zero }) }
        Opcode.AND -> biStackOp(context, VmMath::and)
        Opcode.OR -> biStackOp(context, VmMath::or)
        Opcode.XOR -> biStackOp(context, VmMath::xor)
        Opcode.NOT -> uniStackOp(context, VmMath::not)
        Opcode.BYTE -> biStackOp(context) { a, b ->
            val location = a.toBigInt().toInt().coerceIn(0..31)
            Word.coerceFrom(b.data[location])
        }
        Opcode.SHL -> biStackOp(context, VmMath::shl)
        Opcode.SHR -> biStackOp(context, VmMath::shr)
        Opcode.SAR -> biStackOp(context, VmMath::sar)
        Opcode.SHA3 -> biStackOp(context) { a, b ->
            val bytes = memory.get(a.toInt(), b.toInt())
            keccak256(bytes)
        }
        Opcode.ADDRESS -> {
            val newStack = stack.pushWord(Word.coerceFrom(contract.address.value))

            context.copy(stack = newStack)
        }
        Opcode.BALANCE -> {
            val (popped, newStack) = stack.popWord()
            val balance = evmState.balanceOf(popped.toAddress())
            val finalStack = newStack.pushWord(Word.coerceFrom(balance))

            context.copy(stack = finalStack)
        }
        Opcode.ORIGIN -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.origin.value))
            context.copy(stack = newStack)
        }
        Opcode.CALLER -> {
            val call = callStack.last { it.type != CallType.DELEGATECALL }
            val newStack = stack.pushWord(call.caller.toWord())

            context.copy(stack = newStack)
        }
        Opcode.CALLVALUE -> {
            val call = callStack.last { it.type != CallType.DELEGATECALL }
            val newStack = stack.pushWord(Word.coerceFrom(call.value))

            context.copy(stack = newStack)
        }
        Opcode.CALLDATALOAD -> {
            val (position, newStack) = stack.popWord()

            val call = callStack.last()
            val data = call.callData.subList(position.toInt(), position.toInt() + 32)
            val finalStack = newStack.pushWord(Word.coerceFrom(data))

            context.copy(stack = finalStack)
        }
        Opcode.CALLDATASIZE -> {
            val size = callStack.last().callData.size
            val newStack = stack.pushWord(Word.coerceFrom(size))

            context.copy(stack = newStack)
        }
        Opcode.CALLDATACOPY -> {
            val (elements, newStack) = stack.popWords(3)
            val (to, from, size) = elements.map { it.toInt() }
            val call = callStack.last()
            val data = call.callData.subList(from, from + size)
            val newMemory = memory.set(to, data)

            context.copy(stack = newStack, memory = newMemory)
        }
        Opcode.CODESIZE -> {
            val newStack = stack.pushWord(Word.coerceFrom(contract.code.size))
            context.copy(stack = newStack)
        }
        Opcode.CODECOPY -> {
            val (elements, newStack) = stack.popWords(3)
            val (to, from, size) = elements.map { it.toInt() }
            val data = contract.code.subList(from, from + size)
            val newMemory = memory.set(to, data)

            context.copy(stack = newStack, memory = newMemory)
        }
        Opcode.GASPRICE -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.gasPrice))
            context.copy(stack = newStack)
        }
        Opcode.EXTCODESIZE -> {
            val (popped, newStack) = stack.popWord()
            val code = evmState.codeAt(popped.toAddress())
            val finalStack = newStack.pushWord(Word.coerceFrom(code.size))

            context.copy(stack = finalStack)
        }
        Opcode.EXTCODECOPY -> {
            val (elements, newStack) = stack.popWords(4)
            val (address, to, from, size) = elements

            val code = evmState.codeAt(address.toAddress())
            val data = code.subList(from.toInt(), size.toInt())
            val newMemory = memory.set(to.toInt(), data)

            context.copy(stack = newStack, memory = newMemory)
        }
        Opcode.RETURNDATASIZE -> {
            val newStack = stack.pushWord(Word.coerceFrom(lastReturnData.size))
            context.copy(stack = newStack)
        }
        Opcode.RETURNDATACOPY -> {
            val (elements, newStack) = stack.popWords(3)
            val (to, from, size) = elements.map { it.toInt() }
            val data = lastReturnData.subList(from, from + size)
            val newMemory = memory.set(to, data)

            context.copy(stack = newStack, memory = newMemory)
        }
        Opcode.BLOCKHASH -> TODO()
        Opcode.COINBASE -> {
            val newStack = stack.pushWord(coinBase.toWord())
            context.copy(stack = newStack)
        }
        Opcode.TIMESTAMP -> {
            val epoch = clock.instant().epochSecond
            val newStack = stack.pushWord(Word.coerceFrom(epoch.toBigInteger()))
            context.copy(stack = newStack)
        }
        Opcode.NUMBER -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentBlock.number))
            context.copy(stack = newStack)
        }
        Opcode.DIFFICULTY -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentBlock.difficulty))
            context.copy(stack = newStack)
        }
        Opcode.GASLIMIT -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentBlock.difficulty))
            context.copy(stack = newStack)
        }
        Opcode.POP -> {
            val (_, newStack) = stack.pop()
            context.copy(stack = newStack)
        }
        Opcode.MLOAD -> {
            val (word, newStack) = stack.popWord()
            val data = memory.get(word.toInt(), 32)
            val finalStack = newStack.pushWord(Word(data))

            context.copy(stack = finalStack)
        }
        Opcode.MSTORE -> {
            val (elements, newStack) = stack.popWords(2)
            val (p, v) = elements
            val newMemory = memory.set(p.toInt(), v.data)

            context.copy(stack = newStack, memory = newMemory)
        }
        Opcode.MSTORE8 -> {
            val (p, newStack) = stack.popWord()
            val (v, newStack2) = newStack.pop()
            val newMemory = memory.set(p.toInt(), v.take(1))

            context.copy(stack = newStack2, memory = newMemory)
        }
        Opcode.SLOAD -> {
            val (word, newStack) = stack.popWord()
            val index = word.toInt()
            val finalStack = newStack.pushWord(storage[index])

            context.copy(stack = finalStack)
        }
        Opcode.SSTORE -> {
            val (elements, newStack) = stack.popWords(2)
            val (a, v) = elements
            val newStorage = storage.set(a.toInt(), v)

            context.copy(stack = newStack, storage = newStorage)
        }
        Opcode.JUMP -> {
            val (to, newStack) = stack.popWord()
            val toLocation = to.toInt()
            val nextOpCode = Opcode.byCode[contract[toLocation]]

            if (nextOpCode == Opcode.JUMPDEST) {
                context.copy(stack = newStack, currentLocation = toLocation)
            } else {
                TODO()
            }
        }
        Opcode.JUMPI -> {
            val (elements, newStack) = stack.popWords(2)
            val (to, condition) = elements

            if (condition.toBoolean()) {
                val toLocation = to.toInt()
                val nextOpCode = Opcode.byCode[contract[toLocation]]

                if (nextOpCode == Opcode.JUMPDEST) {
                    context.copy(stack = newStack, currentLocation = toLocation)
                } else {
                    TODO()
                }
            } else {
                context.copy(stack = newStack)
            }
        }
        Opcode.PC -> {
            val newStack = stack.pushWord(Word.coerceFrom(currentLocation))
            context.copy(stack = newStack)
        }
        Opcode.MSIZE -> {
            val size = memory.maxIndex()?.plus(1) ?: 0
            val newStack = stack.pushWord(Word.coerceFrom(size))
            context.copy(stack = newStack)
        }
        Opcode.GAS -> {
            val newStack = stack.pushWord(Word.coerceFrom(gasRemaining))
            context.copy(stack = newStack)
        }
        Opcode.JUMPDEST -> {
            context
        }
        Opcode.PUSH1 -> push(context, 1)
        Opcode.PUSH2 -> push(context, 2)
        Opcode.PUSH3 -> push(context, 3)
        Opcode.PUSH4 -> push(context, 4)
        Opcode.PUSH5 -> push(context, 5)
        Opcode.PUSH6 -> push(context, 6)
        Opcode.PUSH7 -> push(context, 7)
        Opcode.PUSH8 -> push(context, 8)
        Opcode.PUSH9 -> push(context, 9)
        Opcode.PUSH10 -> push(context, 10)
        Opcode.PUSH11 -> push(context, 11)
        Opcode.PUSH12 -> push(context, 12)
        Opcode.PUSH13 -> push(context, 13)
        Opcode.PUSH14 -> push(context, 14)
        Opcode.PUSH15 -> push(context, 15)
        Opcode.PUSH16 -> push(context, 16)
        Opcode.PUSH17 -> push(context, 17)
        Opcode.PUSH18 -> push(context, 18)
        Opcode.PUSH19 -> push(context, 19)
        Opcode.PUSH20 -> push(context, 20)
        Opcode.PUSH21 -> push(context, 21)
        Opcode.PUSH22 -> push(context, 22)
        Opcode.PUSH23 -> push(context, 23)
        Opcode.PUSH24 -> push(context, 24)
        Opcode.PUSH25 -> push(context, 25)
        Opcode.PUSH26 -> push(context, 26)
        Opcode.PUSH27 -> push(context, 27)
        Opcode.PUSH28 -> push(context, 28)
        Opcode.PUSH29 -> push(context, 29)
        Opcode.PUSH30 -> push(context, 30)
        Opcode.PUSH31 -> push(context, 31)
        Opcode.PUSH32 -> push(context, 32)
        Opcode.DUP1 -> dup(context, 0)
        Opcode.DUP2 -> dup(context, 1)
        Opcode.DUP3 -> dup(context, 2)
        Opcode.DUP4 -> dup(context, 3)
        Opcode.DUP5 -> dup(context, 4)
        Opcode.DUP6 -> dup(context, 5)
        Opcode.DUP7 -> dup(context, 6)
        Opcode.DUP8 -> dup(context, 7)
        Opcode.DUP9 -> dup(context, 8)
        Opcode.DUP10 -> dup(context, 9)
        Opcode.DUP11 -> dup(context, 10)
        Opcode.DUP12 -> dup(context, 11)
        Opcode.DUP13 -> dup(context, 12)
        Opcode.DUP14 -> dup(context, 13)
        Opcode.DUP15 -> dup(context, 14)
        Opcode.DUP16 -> dup(context, 15)
        Opcode.SWAP1 -> swap(context, 0)
        Opcode.SWAP2 -> swap(context, 1)
        Opcode.SWAP3 -> swap(context, 2)
        Opcode.SWAP4 -> swap(context, 3)
        Opcode.SWAP5 -> swap(context, 4)
        Opcode.SWAP6 -> swap(context, 5)
        Opcode.SWAP7 -> swap(context, 6)
        Opcode.SWAP8 -> swap(context, 7)
        Opcode.SWAP9 -> swap(context, 8)
        Opcode.SWAP10 -> swap(context, 9)
        Opcode.SWAP11 -> swap(context, 10)
        Opcode.SWAP12 -> swap(context, 11)
        Opcode.SWAP13 -> swap(context, 12)
        Opcode.SWAP14 -> swap(context, 13)
        Opcode.SWAP15 -> swap(context, 14)
        Opcode.SWAP16 -> swap(context, 15)
        Opcode.LOG0 -> log(context, 0)
        Opcode.LOG1 -> log(context, 1)
        Opcode.LOG2 -> log(context, 2)
        Opcode.LOG3 -> log(context, 3)
        Opcode.LOG4 -> log(context, 4)
        Opcode.CREATE -> {
            val (elements, newStack) = stack.popWords(3)
            val (v, p, s) = elements.map { it.toInt() }

            val data = memory.get(p, s)

            //val newStack = stack.pushWord(Word.coerceFrom(currentLocation))
            context.copy(stack = newStack)
        }



        /*
        lastReturnData

        evmState
         */


        else -> TODO("$opcode is not implemented")
    }
}

private fun push(context: ExecutionContext, numBytes: Int): ExecutionContext {
    val data = context.contract.code.subList(context.currentLocation + 1, context.currentLocation + 1 + numBytes)
    val newStack = context.stack.push(data)

    return context.copy(stack = newStack)
}

private fun dup(context: ExecutionContext, offset: Int): ExecutionContext {
    val word = context.stack.peekWord(offset)
    val newStack = context.stack.pushWord(word)

    return context.copy(stack = newStack)
}

private fun swap(context: ExecutionContext, offset: Int): ExecutionContext {
    val a = context.stack.peekWord(offset)
    val b = context.stack.peekWord(offset + 1)
    val newStack = context.stack
        .set(offset, b.data)
        .set(offset + 1, a.data)

    return context.copy(stack = newStack)
}

private fun log(context: ExecutionContext, num: Int): ExecutionContext = with(context) {
    val (elements, newStack) = stack.popWords(2)
    val (p, s) = elements.map { it.toInt() }
    val data = memory.get(p, s)
    val (topics, newStack2) = newStack.popWords(num)
    val newLog = Log(data, topics)

    return context.copy(stack = newStack2, logs = context.logs + newLog)
}

/*
data class Log(
    val data: List<Byte>,
    val topics: List<Word> = emptyList()
)
 */

/*
        Opcode.DUP1 -> {
            val offset = 0
            val word = stack.peekWord(offset)
            val newStack = stack.pushWord(word)

            context.copy(stack = newStack)

            TODO()
        }
 */

/*

 */
