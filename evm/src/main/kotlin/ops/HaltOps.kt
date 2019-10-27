package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.coerceByteListToSize
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger

object HaltOps {
    fun stop(context: ExecutionContext): ExecutionContext = with(context) {
        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, _ ->
            val newStack = ctx.stack.pushWord(Word.One)
            ctx.copy(stack = newStack)
        }

        return context.copy(
            completed = newCallStack.isEmpty(),
            callStack = newCallStack,
            lastReturnData = emptyList()
        )
    }

    fun doReturn(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, _) = context.stack.popWords(2)
        val (dataLocation, dataSize) = elements.map { it.toInt() }
        val returnData = memory.get(dataLocation, dataSize)

        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, oldCtx ->
            val data = coerceByteListToSize(returnData, oldCtx.returnSize)
            val newMemory = ctx.memory.set(oldCtx.returnLocation, data)
            val newStack = ctx.stack.pushWord(Word.One)

            ctx.copy(memory = newMemory, stack = newStack)
        }

        return context.copy(
            completed = newCallStack.isEmpty(),
            callStack = newCallStack,
            lastReturnData = returnData
        )
    }

    fun invalid(context: ExecutionContext, message: String? = null): ExecutionContext {
        val error = EvmError(ErrorCode.INVALID_INSTRUCTION, message ?: "Invalid instruction")
        return fail(context, error)
    }

    fun fail(context: ExecutionContext, error: EvmError): ExecutionContext = with(context) {
        val callingContext = currentCallContext.callingContext ?: context.copy(
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
            lastReturnData = emptyList(),
            lastCallError = error
        )
    }

    fun revert(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, _) = context.stack.popWords(2)
        val (outMemLocation, outSize) = elements.map { it.toInt() }
        val returnData = memory.get(outMemLocation, outSize)

        val oldCtx = currentCallContext
        val callingContext = currentCallContext.callingContext ?: context.copy(
            callStack = context.callStack.dropLast(1)
        )
        val completed = context.callStack.size == 1

        val callStack = updateLastCallCtxIfPresent(callingContext.callStack) { ctx ->
            val data = coerceByteListToSize(returnData, oldCtx.returnSize)
            val newMemory = ctx.memory.set(oldCtx.returnLocation, data)
            val newStack = ctx.stack.pushWord(Word.Zero)

            ctx.copy(memory = newMemory, stack = newStack)
        }

        return callingContext.copy(
            callStack = callStack,
            completed = completed,
            lastReturnData = returnData
        )
    }

    fun suicide(context: ExecutionContext): ExecutionContext = with(context) {
        val (a, _) = context.stack.popWord()
        val address = a.toAddress()

        val newCallStack = dropLastCtxAndUpdateCurrentCtx(callStack) { ctx, _ ->
            val newStack = ctx.stack.pushWord(Word.One)
            ctx.copy(stack = newStack)
        }

        val contract = currentCallContext.contract

        val newEvmState = with(evmState) {
            val newDestBalance = balanceOf(address) + balanceOf(contract.address)

            updateBalance(address, newDestBalance)
                .updateBalanceAndContract(contract.address, BigInteger.ZERO, contract.copy(code = emptyList()))
        }

        return context.copy(
            completed = newCallStack.isEmpty(),
            lastReturnData = emptyList(),
            evmState = newEvmState,
            callStack = newCallStack
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