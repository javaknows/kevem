package org.kevem.evm.ops

import org.kevem.evm.crypto.keccak256
import org.kevem.common.Byte
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word

object DataOps {

    fun codeSize(context: ExecutionContext): ExecutionContext = with(context) {
        val call = callStack.last()
        val newStack = stack.pushWord(Word.coerceFrom(call.code.size()))
        context.updateCurrentCallCtx(stack = newStack)
    }

    fun codeCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toBigInt() }
        val call = callStack.last()

        val data = call.code.read(from, size.toInt())
        val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size)
        val newMemory = memory.write(to, paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun callDataLoad(context: ExecutionContext): ExecutionContext = with(context) {
        val call = callStack.last()
        val (position, newStack) = stack.popWord()

        val data = call.callData.read(position.toBigInt(), 32).let {
            val append = Byte.Zero.repeat(32 - it.size)
            it + append
        }

        val finalStack = newStack.pushWord(Word.coerceFrom(data))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun callDataSize(context: ExecutionContext): ExecutionContext = with(context) {
        val size = callStack.last().callData.size()
        val newStack = stack.pushWord(Word.coerceFrom(size))

        context.updateCurrentCallCtx(stack = newStack)
    }

    fun callDataCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toBigInt() }
        val call = callStack.last()
        val data = call.callData.read(from, size.toInt())
        val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size)
        val newMemory = memory.write(to, paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun returnDataSize(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(lastReturnData.size()))
        context.updateCurrentCallCtx(stack = newStack)
    }

    fun returnDataCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toBigInt() }
        val data = lastReturnData.read(from, size.toInt())
        val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size) // TODO - overflow
        val newMemory = memory.write(to, paddedData)

        return context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun extCodeSize(context: ExecutionContext): ExecutionContext = with(context) {
        val (popped, newStack) = stack.popWord()
        val code = accounts.codeAt(popped.toAddress())
        val finalStack = newStack.pushWord(Word.coerceFrom(code.size()))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun extCodeCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(4)
        val (address, to, from, size) = elements

        val code = accounts.codeAt(address.toAddress())
        val data = code.read(from.toBigInt(), size.toInt())
        val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size)
        val newMemory = memory.write(to.toBigInt(), paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun extCodeHash(context: ExecutionContext): ExecutionContext = with(context) {
        val (address, newStack) = stack.popWord() { it.toAddress() }

        val hash: Word =
            if (accounts.accountExists(address))
                Word(keccak256(accounts.codeAt(address).toList()))
            else
                Word.Zero

        val finalStack = newStack.pushWord(hash)

        context.updateCurrentCallCtx(stack = finalStack)
    }
}