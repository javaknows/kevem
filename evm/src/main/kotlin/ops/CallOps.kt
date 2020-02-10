package org.kevm.evm.ops

import org.kevm.evm.collections.BigIntegerIndexedList
import org.kevm.evm.crypto.*
import org.kevm.evm.model.Byte
import org.kevm.evm.lang.*
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte.Companion.trimAndPadLeft
import org.kevm.evm.model.Byte.Companion.trimAndPadRight
import org.kevm.evm.precompiled.expmod
import java.math.BigInteger
import org.kevm.evm.PrecompiledContractExecutor as Precompiled

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

        return if (Precompiled.isPrecompiledContractCall(callArguments.address))
            Precompiled.doPrecompiled(context, callArguments)
        else {
            with(callArguments) {
                val nextCallerAddress =
                    currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
                val callerBalance = accounts.balanceOf(nextCallerAddress)

                if (callerBalance < value) {
                    val message = "$nextCallerAddress has balance of $callerBalance but attempted to send $value"
                    HaltOps.fail(context, EvmError(ErrorCode.INSUFFICIENT_FUNDS, message))
                } else {
                    val (destBalance, _) = accounts.balanceAndContractAt(address)
                    val newEvmState = accounts
                        .updateBalance(address, destBalance + value)

                    val startBalance = newEvmState.balanceOf(nextCallerAddress)
                    val newEvmState2 = newEvmState
                        .updateBalance(nextCallerAddress, startBalance - value)

                    val (callData, newMemory) = memory.read(inLocation, inSize)
                    val newCall = CallContext(
                        nextCallerAddress,
                        BigIntegerIndexedList.fromBytes(callData),
                        CallType.CALLCODE,
                        value,
                        BigIntegerIndexedList.fromBytes(accounts.codeAt(callArguments.address)),
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
                        accounts = newEvmState2
                    )
                }
            }
        }
    }

    fun delegateCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (callArguments, newStack) = popCallArgsFromStack(context.stack, withValue = false)

        return if (Precompiled.isPrecompiledContractCall(callArguments.address))
            Precompiled.doPrecompiled(context, callArguments)
        else {
            with(callArguments) {
                val code = accounts.contractAt(address)?.code ?: emptyList() // TODO - what if code is empty

                val (callData, newMemory) = memory.read(inLocation, inSize)
                val newCall = CallContext(
                    currentCallCtx.caller,
                    BigIntegerIndexedList.fromBytes(callData),
                    CallType.DELEGATECALL,
                    currentCallCtx.value,
                    BigIntegerIndexedList.fromBytes(code),
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
    }

    private fun doCall(context: ExecutionContext, args: CallArguments, callType: CallType): ExecutionContext =
        if (Precompiled.isPrecompiledContractCall(args.address))
            Precompiled.doPrecompiled(context, args)
        else
            doCallX(context, args, callType)

    private fun doCallX(context: ExecutionContext, args: CallArguments, callType: CallType): ExecutionContext =
        with(context) {
            with(args) {
                val nextCaller =
                    currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
                val callerBalance = accounts.balanceOf(nextCaller)
                if (callerBalance < value) {
                    val message = "$nextCaller has balance of $callerBalance but attempted to send $value"
                    HaltOps.fail(context, EvmError(ErrorCode.INSUFFICIENT_FUNDS, message))
                } else {

                    val (destBalance, destContract) = accounts.balanceAndContractAt(address)
                    val newEvmState = accounts
                        .updateBalance(address, destBalance + value)

                    val startBalance = newEvmState.balanceOf(nextCaller)
                    val newEvmState2 = newEvmState
                        .updateBalance(nextCaller, startBalance - value)

                    val (callData, newMemory) = memory.read(inLocation, inSize)
                    val callContractCode = destContract?.code ?: emptyList()
                    val newCall = CallContext(
                        nextCaller,
                        BigIntegerIndexedList.fromBytes(callData),
                        callType,
                        value,
                        BigIntegerIndexedList.fromBytes(callContractCode),
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
                        accounts = newEvmState2
                    )
                }
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

