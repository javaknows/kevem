package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.model.Word

object MemoryOps  {
    fun msize(context: ExecutionContext): ExecutionContext = with(context) {
        val size = memory.maxIndex()?.plus(1) ?: 0
        val newStack = stack.pushWord(Word.coerceFrom(size))

        context.updateCurrentCallCtx(stack = newStack)
    }

    fun mload(context: ExecutionContext): ExecutionContext = with(context) {
        val (word, newStack) = stack.popWord()
        val data = memory.get(word.toInt(), 32)
        val finalStack = newStack.pushWord(Word(data))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun mstore(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (p, v) = elements
        val newMemory = memory.set(p.toInt(), v.data)

        context.updateCurrentCallCtx(stack = newStack, memory = newMemory)
    }

    fun mstore8(context: ExecutionContext): ExecutionContext = with(context) {
        val (v, newStack) = stack.pop()
        val (p, newStack2) = newStack.popWord()
        val newMemory = memory.set(p.toInt(), v.take(1))

        context.updateCurrentCallCtx(stack = newStack2, memory = newMemory)
    }
}