package org.kevm.evm.ops

import org.kevm.common.KevmException
import org.kevm.evm.model.ExecutionContext
import org.kevm.evm.model.Word

object CallerOps {
    fun address(context: ExecutionContext): ExecutionContext = with(context) {
        val call = callStack.last()
        val contractAddress =
            call.contractAddress ?: throw RuntimeException("can't determine contract address")
        val newStack = stack.pushWord(Word.coerceFrom(contractAddress.value))

        updateCurrentCallCtx(stack = newStack)
    }

    fun balance(context: ExecutionContext): ExecutionContext = with(context) {
        val (popped, newStack) = stack.popWord()
        val balance = accounts.balanceOf(popped.toAddress())
        val finalStack = newStack.pushWord(Word.coerceFrom(balance))

        updateCurrentCallCtx(stack = finalStack)
    }

    fun origin(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.origin.value))
        updateCurrentCallCtx(stack = newStack)
    }

    fun caller(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(currentCallCtx.caller.toWord())
        updateCurrentCallCtx(stack = newStack)
    }

    fun callValue(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentCallCtx.value))
        updateCurrentCallCtx(stack = newStack)
    }

    fun gasPrice(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentTransaction.gasPrice))
        updateCurrentCallCtx(stack = newStack)
    }

    fun programCounter(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentLocation))
        updateCurrentCallCtx(stack = newStack)
    }

    fun gas(context: ExecutionContext): ExecutionContext = with(context) {
        val newStack = stack.pushWord(Word.coerceFrom(currentCallCtx.gasRemaining))
        updateCurrentCallCtx(stack = newStack)
    }

    fun selfBalance(context: ExecutionContext): ExecutionContext = with(context) {
        val address = currentCallCtx.contractAddress ?: throw KevmException("can't determine contract address")
        val balance = context.accounts.balanceOf(address)
        val newStack = stack.pushWord(Word.coerceFrom(balance))

        updateCurrentCallCtx(stack = newStack)
    }
}