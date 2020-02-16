package org.kevm.evm.ops

import org.kevm.evm.coerceByteListToSize
import org.kevm.evm.collections.BigIntegerIndexedList
import org.kevm.evm.collections.BigIntegerIndexedList.Companion.emptyByteList
import org.kevm.evm.gas.Refund
import org.kevm.evm.model.*
import java.math.BigInteger

object HaltOps {
    fun stop(context: ExecutionContext): ExecutionContext = with(context) {
        val caller = currentCallCtx.caller
        val refund = currentCallCtx.gasRemaining * currentTransaction.gasPrice

        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, _ ->
            val newStack = ctx.stack.pushWord(Word.One)
            ctx.copy(stack = newStack)
        }

        return context
            .refund(caller, refund)
            .copy(
                completed = newCallStack.isEmpty(),
                callStack = newCallStack,
                lastReturnData = emptyByteList(),
                gasUsed = gasUsed + currentCallCtx.gasUsed
            )
    }

    fun doReturn(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, _) = context.stack.popWords(2)
        val (dataLocation, dataSize) = elements
        val (returnData, newMemory) = memory.read(dataLocation.toBigInt(), dataSize.toInt())

        val caller = currentCallCtx.caller
        val refund = currentCallCtx.gasRemaining * currentTransaction.gasPrice

        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, oldCtx ->
            val data = coerceByteListToSize(returnData, oldCtx.returnSize)
            val newMemory = ctx.memory.write(oldCtx.returnLocation, data)
            val newStack = ctx.stack.pushWord(Word.One)

            ctx.copy(memory = newMemory, stack = newStack)
        }

        return context
            .refund(caller, refund)
            .copy(
                completed = newCallStack.isEmpty(),
                callStack = newCallStack,
                lastReturnData = BigIntegerIndexedList.fromBytes(returnData),
                gasUsed = gasUsed + currentCallCtx.gasUsed
            )
    }

    fun invalid(context: ExecutionContext, message: String? = null): ExecutionContext {
        val error = EvmError(ErrorCode.INVALID_INSTRUCTION, message ?: "Invalid instruction")
        return fail(context, error)
    }

    fun fail(context: ExecutionContext, error: EvmError): ExecutionContext = with(context) {
        val callingContext = currentCallCtx.callingContext ?: context.copy(
            callStack = context.callStack.dropLast(1)
        )
        val completed = context.callStack.size == 1

        val callStack = updateLastCallCtxIfPresent(callingContext.callStack) { ctx ->
            val newStack = ctx.stack.pushWord(Word.Zero)
            ctx.copy(stack = newStack)
        }

        return callingContext.copy(
            callStack = callStack,
            completed = completed,
            lastReturnData = emptyByteList(),
            lastCallError = error,
            gasUsed = gasUsed + currentCallCtx.gas
        )
    }

    fun revert(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, _) = context.stack.popWords(2)
        val (outMemLocation, outSize) = elements
        val (returnData, newMemory) = memory.read(outMemLocation.toBigInt(), outSize.toInt())

        val oldCtx = currentCallCtx
        val callingContext = currentCallCtx.callingContext ?: context.copy(
            callStack = context.callStack.dropLast(1)
        )
        val completed = context.callStack.size == 1

        val caller = currentCallCtx.caller
        val refund = currentCallCtx.gasRemaining * currentTransaction.gasPrice

        val callStack = updateLastCallCtxIfPresent(callingContext.callStack) { ctx ->
            val data = coerceByteListToSize(returnData, oldCtx.returnSize)
            val newMemory = ctx.memory.write(oldCtx.returnLocation, data)
            val newStack = ctx.stack.pushWord(Word.Zero)

            ctx.copy(memory = newMemory, stack = newStack)
        }

        return callingContext
            .refund(caller, refund)
            .copy(
                callStack = callStack,
                completed = completed,
                lastReturnData = BigIntegerIndexedList.fromBytes(returnData),
                gasUsed = gasUsed + currentCallCtx.gasUsed
            )
    }

    fun suicide(context: ExecutionContext): ExecutionContext = with(context) {
        val (a, _) = context.stack.popWord()
        val sendFundsToAddress = a.toAddress()

        val caller = currentCallCtx.caller
        val refund = currentCallCtx.gasRemaining * currentTransaction.gasPrice + Refund.SelfDestruct.wei.toBigInteger()

        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, _ ->
            val newStack = ctx.stack.pushWord(Word.One)
            ctx.copy(stack = newStack)
        }

        val contractAddress =
            currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
        val contract =
            accounts.contractAt(contractAddress) ?: throw RuntimeException("can't determine current contract")

        val newEvmState = with(accounts) {
            val newDestBalance = balanceOf(sendFundsToAddress) + balanceOf(contractAddress)

            updateBalance(sendFundsToAddress, newDestBalance)
                .updateBalanceAndContract(contractAddress, BigInteger.ZERO, contract.copy(code = emptyByteList()))
        }

        return context
            .refund(caller, refund)
            .copy(
                completed = newCallStack.isEmpty(),
                lastReturnData = emptyByteList(),
                accounts = newEvmState,
                callStack = newCallStack,
                gasUsed = gasUsed + currentCallCtx.gasUsed
            )
    }

    private fun updateLastCallCtxIfPresent(
        callStack: List<CallContext>,
        op: (ctx: CallContext) -> CallContext
    ): List<CallContext> {
        val updated = callStack.lastOrNull()?.let { op(it) }

        return if (updated != null) callStack.dropLast(1) + updated
        else callStack
    }

    private fun dropLastCtxAndUpdateCurrentCtx(
        callStack: List<CallContext>,
        op: (ctx: CallContext, oldCtx: CallContext) -> CallContext
    ): List<CallContext> {
        val oldCtx = callStack.last()
        val newCallStack = callStack.dropLast(1)

        val updated = newCallStack.lastOrNull()?.let { op(it, oldCtx) }

        return if (updated != null) newCallStack.dropLast(1) + updated
        else newCallStack
    }
}