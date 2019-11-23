package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.ExecutionContext

object CryptoOps {
    fun sha3(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (a, b) = elements.map { it.toInt() }
        val (bytes, newMemory) = memory.read(a, b)
        val finalStack = newStack.pushWord(keccak256(bytes))

        updateCurrentCallCtx(stack = finalStack, memory = newMemory)
    }
}