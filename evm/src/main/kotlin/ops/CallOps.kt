package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.lang.*
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger

data class CallArguments(
    val gas: BigInteger,
    val address: Address,
    val inLocation: Int,
    val inSize: Int,
    val outLocation: Int,
    val outSize: Int,
    val value: BigInteger = BigInteger.ZERO
)

object CallOps {
    fun call(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context.stack, withValue = true)

        val newCtx = updateCurrentCallCtx(stack = newStack)

        return doCall(newCtx, callArguments, CallType.CALL)
    }

    fun staticCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context.stack, withValue = false)

        val newCtx = updateCurrentCallCtx(stack = newStack)

        return doCall(newCtx, callArguments, CallType.STATICCALL)
    }

    fun callCode(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context.stack, withValue = true)

        with(callArguments) {
            val nextCallerAddress =
                currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
            val callerBalance = evmState.balanceOf(nextCallerAddress)
            if (callerBalance < value) {
                TODO("handle case where contract doesn't have enough funds")
            }

            val (destBalance, _) = evmState.balanceAndContractAt(address)
            val newEvmState = evmState
                .updateBalance(address, destBalance + value)

            val startBalance = newEvmState.balanceOf(nextCallerAddress)
            val newEvmState2 = newEvmState
                .updateBalance(nextCallerAddress, startBalance - value)

            val (callData, newMemory) = memory.read(inLocation, inSize)
            val newCall = CallContext(
                nextCallerAddress,
                callData,
                CallType.CALLCODE,
                value,
                evmState.codeAt(callArguments.address),
                context,
                gas,
                outLocation,
                outSize,
                contractAddress = currentCallCtx.contractAddress,
                storageAddress = currentCallCtx.storageAddress
            )

            val updatedCtx = updateCurrentCallCtx(
                stack = newStack,
                memory = newMemory
            )

            updatedCtx.copy(
                callStack = updatedCtx.callStack + newCall,
                evmState = newEvmState2
            )
        }
    }

    fun delegateCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context.stack, withValue = false)

        with(callArguments) {
            val code = evmState.contractAt(address)?.code ?: emptyList() // TODO - what if code is empty

            val (callData, newMemory) = memory.read(inLocation, inSize)
            val newCall = CallContext(
                currentCallCtx.caller,
                callData,
                CallType.DELEGATECALL,
                currentCallCtx.value,
                code,
                context,
                gas,
                outLocation,
                outSize,
                contractAddress = callArguments.address,
                storageAddress = currentCallCtx.storageAddress
            )

            val updatedCtx = updateCurrentCallCtx(
                stack = newStack,
                memory = newMemory
            )

            updatedCtx.copy(
                callStack = updatedCtx.callStack + newCall
            )
        }
    }

    private fun doCall(context: ExecutionContext, args: CallArguments, callType: CallType): ExecutionContext =
        with(context) {
            with(args) {
                val nextCaller =
                    currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
                val callerBalance = evmState.balanceOf(nextCaller)
                if (callerBalance < value) {
                    TODO("handle case where contract doesn't have enough funds")
                }

                val (destBalance, destContract) = evmState.balanceAndContractAt(address)
                val newEvmState = evmState
                    .updateBalance(address, destBalance + value)

                val startBalance = newEvmState.balanceOf(nextCaller)
                val newEvmState2 = newEvmState
                    .updateBalance(nextCaller, startBalance - value)

                val (callData, newMemory) = memory.read(inLocation, inSize)
                val callContractCode = destContract?.code ?: emptyList()
                val newCall = CallContext(
                    nextCaller,
                    callData,
                    callType,
                    value,
                    callContractCode,
                    context,
                    gas,
                    outLocation,
                    outSize,
                    contractAddress = address,
                    storageAddress = address
                )

                val updatedCtx = updateCurrentCallCtx(
                    memory = newMemory
                )

                updatedCtx.copy(
                    callStack = updatedCtx.callStack + newCall,
                    evmState = newEvmState2
                )
            }
        }

    fun popCallArgsFromStack(stack: Stack, withValue: Boolean): Pair<CallArguments, Stack> {
        val (elements, newStack) = if (withValue) {
            stack.popWords(7)
        } else {
            val (elements, newStack) = stack.popWords(6)
            val splicedElements = elements.take(2) + Word.Zero + elements.takeLast(4)
            Pair(splicedElements, newStack)
        }
        val callArguments = callArguments(elements)

        return Pair(callArguments, newStack)
    }

    fun peekCallArgsFromStack(stack: Stack, withValue: Boolean): CallArguments {
        val elements = if (withValue) {
            stack.peekWords(7)
        } else {
            val elements = stack.peekWords(6)
            elements.take(2) + Word.Zero + elements.takeLast(4)
        }

        return callArguments(elements)
    }

    private fun callArguments(elements: List<Word>): CallArguments {
        val (g, a, value, inLocation, inSize, outLocation, outSize) = elements

        return CallArguments(
            g.toBigInt(),
            a.toAddress(),
            inLocation.toInt(),
            inSize.toInt(),
            outLocation.toInt(),
            outSize.toInt(),
            value.toBigInt()
        )
    }
}

