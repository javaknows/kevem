package org.kevem.evm.ops

import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word
import java.math.BigInteger

object MemoryOps  {
    fun msize(context: ExecutionContext): ExecutionContext = with(context) {
        val size = memory.maxIndex ?: BigInteger.ZERO
        val newStack = stack.pushWord(Word.coerceFrom(size))

        context.updateCurrentCallCtx(stack = newStack)
    }

    fun mload(context: ExecutionContext): ExecutionContext = with(context) {
        val (word, newStack) = stack.popWord()
        val (data, newMemory) = memory.read(word.toBigInt(), 32)
        val finalStack = newStack.pushWord(Word(data))

        context.updateCurrentCallCtx(stack = finalStack, memory = newMemory)
    }

    fun mstore(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (p, v) = elements
        val newMemory = memory.write(p.toBigInt(), v.data)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun mstore8(context: ExecutionContext): ExecutionContext = with(context) {
        val (p, newStack) = stack.popWord()
        val (v, newStack2) = newStack.pop()
        val newMemory = memory.write(p.toBigInt(), v.take(1))

        context.updateCurrentCallCtx(stack = newStack2, memory = newMemory)
    }
}