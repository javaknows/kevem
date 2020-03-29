package org.kevem.evm.ops

import org.kevem.common.Logger
import org.kevem.evm.EIP
import org.kevem.evm.gas.GasCost
import org.kevem.evm.gas.Refund
import org.kevem.evm.model.ErrorCode
import org.kevem.evm.model.EvmError
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word

object StorageOps {
    private val log: Logger = Logger.createLogger(StorageOps::class)

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
        val (slot, newValue) = elements

        val address = currentCallCtx.storageAddress ?: throw RuntimeException("can't determine contract address")

        val originalValue = context.originalAccounts.storageAt(address, slot.toBigInt())
        val currentValue = accounts.storageAt(address, slot.toBigInt())

        if (context.config.features.isEnabled(EIP.EIP2200) && currentCallCtx.gasRemaining <= GasCost.CallStipend.costBigInt - GasCost.SLoadEip2200.costBigInt) {
            log.debug("Failing due to eip2200 gas remaining")
            HaltOps.fail(context, EvmError(ErrorCode.OUT_OF_GAS, "Out of gas"))
        } else {
            val newAccounts = accounts.updateStorage(address, slot.toBigInt(), newValue)
            val newCtx = updateCurrentCallCtx(stack = newStack).copy(accounts = newAccounts)

            if (context.config.features.isEnabled(EIP.EIP2200) && currentValue != newValue) {
                sStoreEip2200Refunds(originalValue, currentValue, newValue, newCtx)
            } else if (isSettingNonZeroToZero(currentValue, newValue))
                newCtx.refund(currentTransaction.origin, Refund.StorageClear.wei.toBigInteger())
            else
                newCtx
        }
    }

    // If current value does not equal new value
    private fun sStoreEip2200Refunds(
        originalValue: Word,
        currentValue: Word,
        newValue: Word,
        context: ExecutionContext
    ): ExecutionContext = with(context) {
        return if (originalValue == currentValue && newValue == Word.Zero) {
            // If original value equals current value (this storage slot has not been changed by the current execution context)
            // If new value is 0, add SSTORE_CLEARS_SCHEDULE gas to refund counter.
            refund(currentTransaction.origin, Refund.StorageClear.wei.toBigInteger())
        } else if (originalValue != currentValue) {
            // If original value does not equal current value (this storage slot is dirty), Apply both of the following clauses.

            val newCtx =
                if (originalValue != Word.Zero) { // If original value is not 0
                    if (currentValue == Word.Zero) // If current value is 0
                        refund(currentTransaction.origin, -Refund.StorageClear.wei.toBigInteger())
                    else if (newValue == Word.Zero)
                        refund(currentTransaction.origin, Refund.StorageClear.wei.toBigInteger())
                    else
                        context
                } else
                    context

            if (originalValue == newValue) { // If original value equals new value
                if (originalValue == Word.Zero)  // If original value is 0
                    newCtx.refund(currentTransaction.origin, (GasCost.SStoreSet.costBigInt - GasCost.SLoadEip2200.costBigInt))
                else // Otherwise
                    newCtx.refund(
                        currentTransaction.origin,
                        (GasCost.SStoreReset.costBigInt - GasCost.SLoadEip2200.costBigInt)
                    )
            } else newCtx
        } else context
    }

    private fun isSettingNonZeroToZero(oldValue: Word, newValue: Word) =
        oldValue != Word.Zero && newValue == Word.Zero
}
