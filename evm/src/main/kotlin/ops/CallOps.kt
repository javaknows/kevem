package com.gammadex.kevin.evm.ops

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

// TODO - add return value sto stack - 0 or 1

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
            val callerBalance = evmState.balanceOf(currentCallContext.contract.address)
            if (callerBalance < value) {
                TODO("handle case where contract doesn't have enough funds")
            }

            if (currentCallContext.gasRemaining < gas) {
                TODO("handle case where not enugh gas remaining")
            }

            val (destBalance, _) = evmState.balanceAndContractAt(address)
            val newEvmState = evmState
                .updateBalance(address, destBalance + value)

            val startBalance = newEvmState.balanceOf(currentCallContext.contract.address)
            val newEvmState2 = newEvmState
                .updateBalance(currentCallContext.contract.address, startBalance - value)

            val newCall = CallContext(
                currentCallContext.contract.address,
                memory.get(inLocation, inSize),
                currentCallContext.contract,
                CallType.CALLCODE,
                value,
                currentCallContext.contract.code,
                context,
                gas,
                outLocation,
                outSize
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
                currentCallContext.contract,
                CallType.DELEGATECALL,
                currentCallContext.value,
                code,
                context,
                gas,
                outLocation,
                outSize
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
                val callerBalance = evmState.balanceOf(currentCallContext.contract.address)
                if (callerBalance < value) {
                    TODO("handle case where contract doesn't have enough funds")
                }

                if (currentCallContext.gasRemaining < gas) {
                    TODO("handle case where not enugh gas remaining")
                }

                val (destBalance, destContract) = evmState.balanceAndContractAt(address)
                val newEvmState = evmState
                    .updateBalance(address, destBalance + value)

                val startBalance = newEvmState.balanceOf(currentCallContext.contract.address)
                val newEvmState2 = newEvmState
                    .updateBalance(currentCallContext.contract.address, startBalance - value)

                val callContract = destContract ?: EmptyContract(address)
                val newCall = CallContext(
                    currentCallContext.contract.address,
                    memory.get(inLocation, inSize),
                    callContract,
                    callType,
                    value,
                    callContract.code,
                    context,
                    gas,
                    outLocation,
                    outSize
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
        val (elements, newStack) = context.stack.popWords(4)
        val (inLocation, inSize, outLocation, outSize) = elements

        val (value, newStack2) =
            if (withValue) {
                val (value, newStack2) = newStack.popWord()
                Pair(value.toBigInt(), newStack2)
            } else Pair(BigInteger.ZERO, newStack)

        val (elements2, newStack3) = newStack2.popWords(2)
        val (g, a) = elements2

        val callArguments = CallArguments(
            g.toBigInt(),
            a.toAddress(),
            inLocation.toInt(),
            inSize.toInt(),
            outLocation.toInt(),
            outSize.toInt(),
            value
        )

        return Pair(callArguments, newStack3)
    }
}

