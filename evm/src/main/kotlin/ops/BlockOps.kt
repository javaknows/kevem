package org.kevem.evm.ops

import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word

object BlockOps {

    fun blockHash(context: ExecutionContext): ExecutionContext = with(context) {
        val (blockNumber, newStack) = stack.popWord()
        val hash = previousBlocks.getOrDefault(blockNumber.toBigInt(), Word.Zero)
        val finalStack = newStack.pushWord(hash)

        updateCurrentCallCtx(stack = finalStack)
    }

    fun coinBase(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(context.config.coinbase.toWord())
        updateCurrentCallCtx(stack = newStack)
    }

    fun timeStamp(context: ExecutionContext): ExecutionContext = with(context) {
        val epoch = currentBlock.timestamp.epochSecond
        val newStack = stack.pushWord(Word.coerceFrom(epoch))
        updateCurrentCallCtx(stack = newStack)
    }

    fun number(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentBlock.number))
        updateCurrentCallCtx(stack = newStack)
    }

    fun difficulty(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentBlock.difficulty))
        updateCurrentCallCtx(stack = newStack)
    }

    fun gasLimit(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentBlock.gasLimit))
        updateCurrentCallCtx(stack = newStack)
    }

    fun chainId(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(config.chainId))
        updateCurrentCallCtx(stack = newStack)
    }
}