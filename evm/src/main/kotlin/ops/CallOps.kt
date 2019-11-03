package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.lang.*
import com.gammadex.kevin.evm.model.*
import java.math.BigInteger

private data class CallArguments(
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
        val (callArguments, newStack) = popCallArgsFromStack(context, withValue = true)

        val newCtx = updateCurrentCallCtx(stack = newStack)

        return doCall(newCtx, callArguments, CallType.CALL)
    }

    fun staticCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context, withValue = false)

        val newCtx = updateCurrentCallCtx(stack = newStack)

        return doCall(newCtx, callArguments, CallType.STATICCALL)
    }

    fun callCode(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context, withValue = true)

        with(callArguments) {
            val nextCallerAddress = currentCallContext.contractAddress ?: throw RuntimeException("can't determine contract address")
            val callerBalance = evmState.balanceOf(nextCallerAddress)
            if (callerBalance < value) {
                TODO("handle case where contract doesn't have enough funds")
            }

            if (currentCallContext.gasRemaining < gas) {
                TODO("handle case where not enugh gas remaining")
            }

            val (destBalance, _) = evmState.balanceAndContractAt(address)
            val newEvmState = evmState
                .updateBalance(address, destBalance + value)

            val startBalance = newEvmState.balanceOf(nextCallerAddress)
            val newEvmState2 = newEvmState
                .updateBalance(nextCallerAddress, startBalance - value)

            val newCall = CallContext(
                nextCallerAddress,
                memory.get(inLocation, inSize),
                CallType.CALLCODE,
                value,
                evmState.codeAt(callArguments.address),
                context,
                gas,
                outLocation,
                outSize,
                contractAddress = currentCallContext.contractAddress,
                storageAddress = currentCallContext.storageAddress
            )

            val updatedCtx = updateCurrentCallCtx(
                stack = newStack,
                gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
            )

            updatedCtx.copy(
                callStack = updatedCtx.callStack + newCall,
                evmState = newEvmState2
            )
        }
    }

    fun delegateCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context, withValue = false)

        with(callArguments) {
            if (currentCallContext.gasRemaining < gas) {
                TODO("handle case where not enugh gas remaining")
            }

            val code = evmState.contractAt(address)?.code ?: emptyList() // TODO - what if code is empty

            val newCall = CallContext(
                currentCallContext.caller,
                memory.get(inLocation, inSize),
                CallType.DELEGATECALL,
                currentCallContext.value,
                code,
                context,
                gas,
                outLocation,
                outSize,
                contractAddress = callArguments.address,
                storageAddress = currentCallContext.storageAddress
            )

            val updatedCtx = updateCurrentCallCtx(
                stack = newStack,
                gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
            )

            updatedCtx.copy(
                callStack = updatedCtx.callStack + newCall
            )
        }
    }

    private fun doCall(context: ExecutionContext, args: CallArguments, callType: CallType): ExecutionContext =
        with(context) {
            with(args) {
                val nextCaller = currentCallContext.contractAddress  ?: throw RuntimeException("can't determine contract address")
                val callerBalance = evmState.balanceOf(nextCaller)
                if (callerBalance < value) {
                    TODO("handle case where contract doesn't have enough funds")
                }

                if (currentCallContext.gasRemaining < gas) {
                    TODO("handle case where not enugh gas remaining")
                }

                val (destBalance, destContract) = evmState.balanceAndContractAt(address)
                val newEvmState = evmState
                    .updateBalance(address, destBalance + value)

                val startBalance = newEvmState.balanceOf(nextCaller)
                val newEvmState2 = newEvmState
                    .updateBalance(nextCaller, startBalance - value)

                val callContractCode = destContract?.code ?: emptyList()
                val newCall = CallContext(
                    nextCaller,
                    memory.get(inLocation, inSize),
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
                    gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
                )

                updatedCtx.copy(
                    callStack = updatedCtx.callStack + newCall,
                    evmState = newEvmState2
                )
            }
        }

    private fun popCallArgsFromStack(context: ExecutionContext, withValue: Boolean): Pair<CallArguments, Stack> {
        val (elements, newStack) = if (withValue) {
            context.stack.popWords(7)
        } else {
            val (elements, newStack) = context.stack.popWords(6)
            val splicedElements = elements.take(2) + Word.Zero + elements.takeLast(4)
            Pair(splicedElements, newStack)
        }
        val (g, a, value, inLocation, inSize, outLocation, outSize) = elements

        val callArguments = CallArguments(
            g.toBigInt(),
            a.toAddress(),
            inLocation.toInt(),
            inSize.toInt(),
            outLocation.toInt(),
            outSize.toInt(),
            value.toBigInt()
        )

        return Pair(callArguments, newStack)
    }
}

