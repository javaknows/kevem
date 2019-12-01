package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.model.ExecutionContext

object StorageOps {
    fun sLoad(context: ExecutionContext): ExecutionContext = with(context) {
        val (word, newStack) = stack.popWord()
        val index = word.toBigInt()

        val contractAddress = context.currentCallCtx.storageAddress ?: throw RuntimeException("can't determine contract address")
        val finalStack = newStack.pushWord(accounts.storageAt(contractAddress, index))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun sStore(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (a, v) = elements

        val contractAddress = context.currentCallCtx.storageAddress ?: throw RuntimeException("can't determine contract address")
        val newEvmState = context.accounts.updateStorage(contractAddress, a.toBigInt(), v)

        context.updateCurrentCallCtx(stack = newStack).copy(accounts = newEvmState)
    }
}
