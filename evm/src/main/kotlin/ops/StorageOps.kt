package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.model.ExecutionContext

object StorageOps {
    fun sLoad(context: ExecutionContext): ExecutionContext = with(context) {
        val (word, newStack) = stack.popWord()
        val index = word.toInt()
        val finalStack = newStack.pushWord(storage[index])

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun sStore(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (a, v) = elements
        val newStorage = storage.set(a.toInt(), v)

        context.updateCurrentCallCtx(stack = newStack, storage = newStorage)
    }
}
