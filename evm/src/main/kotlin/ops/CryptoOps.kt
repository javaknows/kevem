package org.kevm.evm.ops

import org.kevm.evm.keccak256
import org.kevm.evm.model.ExecutionContext

object CryptoOps {
    fun sha3(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (a, b) = elements.map { it.toInt() }
        val (bytes, newMemory) = memory.read(a, b)
        val finalStack = newStack.pushWord(keccak256(bytes))

        updateCurrentCallCtx(stack = finalStack, memory = newMemory)
    }
}