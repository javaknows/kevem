package org.kevem.evm.ops

import org.kevem.evm.crypto.keccak256
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word

object CryptoOps {
    fun sha3(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (a, b) = elements
        val (bytes, newMemory) = memory.read(a.toBigInt(), b.toInt())
        val finalStack = newStack.pushWord(Word(keccak256(bytes)))

        updateCurrentCallCtx(stack = finalStack, memory = newMemory)
    }
}