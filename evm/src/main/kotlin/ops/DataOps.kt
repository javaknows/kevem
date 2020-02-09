package org.kevm.evm.ops

import org.kevm.evm.model.Byte
import org.kevm.evm.model.ExecutionContext
import org.kevm.evm.model.Word

object DataOps {

    fun codeSize(context: ExecutionContext): ExecutionContext = with(context) {
        val call = callStack.last()
        val newStack = stack.pushWord(Word.coerceFrom(call.code.size))
        context.updateCurrentCallCtx(stack = newStack)
    }

    fun codeCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toInt() }
        val call = callStack.last()
        val data = call.code.drop(from).take(size)
        val paddedData = data + Byte.Zero.repeat(size - data.size)
        val newMemory = memory.write(to, paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun callDataLoad(context: ExecutionContext): ExecutionContext = with(context) {
        val call = callStack.last()

        val (position, newStack) = stack.popWord()
        val start = position.toInt().coerceIn(0, call.callData.size)
        val end = (position.toInt() + 32).coerceIn(start, call.callData.size)
        val append = Byte.Zero.repeat(32 - end + start)

        val data = call.callData.subList(start, end) + append
        val finalStack = newStack.pushWord(Word.coerceFrom(data))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun callDataSize(context: ExecutionContext): ExecutionContext = with(context) {
        val size = callStack.last().callData.size
        val newStack = stack.pushWord(Word.coerceFrom(size))

        context.updateCurrentCallCtx(stack = newStack)
    }

    fun callDataCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toInt() }
        val call = callStack.last()
        val data = call.callData.drop(Math.max(from, 0)).take(size)
        val paddedData = data + Byte.Zero.repeat(size - data.size)
        val newMemory = memory.write(to, paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun returnDataSize(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(lastReturnData.size))
        context.updateCurrentCallCtx(stack = newStack)
    }

    fun returnDataCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (to, from, size) = elements.map { it.toInt() }
        val data = lastReturnData.drop(from).take(size)
        val paddedData = data + Byte.Zero.repeat(size - data.size)
        val newMemory = memory.write(to, paddedData)

        return context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun extCodeSize(context: ExecutionContext): ExecutionContext = with(context) {
        val (popped, newStack) = stack.popWord()
        val code = accounts.codeAt(popped.toAddress())
        val finalStack = newStack.pushWord(Word.coerceFrom(code.size))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun extCodeCopy(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(4)
        val (address, to, from, size) = elements

        val code = accounts.codeAt(address.toAddress())
        val data = code.drop(from.toInt()).take(size.toInt())
        val paddedData = data + Byte.Zero.repeat(size.toInt() - data.size)
        val newMemory = memory.write(to.toInt(), paddedData)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

}