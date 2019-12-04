package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.gas.Refund
import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.model.Word

object StorageOps {
    fun sLoad(context: ExecutionContext): ExecutionContext = with(context) {
        val (word, newStack) = stack.popWord()
        val index = word.toBigInt()

        val contractAddress =
            context.currentCallCtx.storageAddress ?: throw RuntimeException("can't determine contract address")
        val finalStack = newStack.pushWord(accounts.storageAt(contractAddress, index))

        context.updateCurrentCallCtx(stack = finalStack)
    }

    fun sStore(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(2)
        val (slot, value) = elements

        val address = currentCallCtx.storageAddress ?: throw RuntimeException("can't determine contract address")
        val oldStorageValue = accounts.storageAt(address, slot.toBigInt())
        val newAccounts = accounts.updateStorage(address, slot.toBigInt(), value)

        val newCtx = updateCurrentCallCtx(stack = newStack).copy(accounts = newAccounts)

        if (isSettingNonZeroToZero(oldStorageValue, value))
            newCtx.refund(currentTransaction.origin, Refund.StorageClear.wei.toBigInteger())
        else
            newCtx
    }

    private fun isSettingNonZeroToZero(oldValue: Word, newValue: Word) = oldValue != Word.Zero && newValue == Word.Zero
}
