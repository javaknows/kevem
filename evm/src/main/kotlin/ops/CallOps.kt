package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.lang.component6
import com.gammadex.kevin.evm.lang.component7
import com.gammadex.kevin.evm.model.CallContext
import com.gammadex.kevin.evm.model.CallType
import com.gammadex.kevin.evm.model.EmptyContract
import com.gammadex.kevin.evm.model.ExecutionContext

object CallOps {
    fun call(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(7)
        val (g, a, v, inLocation, inSize, outLocation, outSize) = elements

        val value = v.toBigInt()
        val gas = g.toBigInt()
        val address = a.toAddress()

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

        val xDestContract  = destContract ?: EmptyContract(address)
        val newCall = CallContext(
            currentCallContext.contract.address,
            memory.get(inLocation.toInt(), inSize.toInt()),
            xDestContract,
            CallType.CALL,
            value,
            xDestContract.code,
            gas,
            outLocation.toInt(),
            outSize.toInt()
        )

        val updatedCtx = updateCurrentCallContext(
            stack = newStack,
            gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
        )

        updatedCtx.copy(
            callStack = updatedCtx.callStack + newCall,
            evmState = newEvmState2
        )
    }

    fun callCode(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(7)
        val (g, a, v, inLocation, inSize, outLocation, outSize) = elements

        val value = v.toBigInt()
        val gas = g.toBigInt()
        val address = a.toAddress()

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
            memory.get(inLocation.toInt(), inSize.toInt()),
            currentCallContext.contract, // TODO - check that this is correct
            CallType.CALLCODE,
            value,
            currentCallContext.contract.code,
            gas,
            outLocation.toInt(),
            outSize.toInt()
        )

        val updatedCtx = updateCurrentCallContext(
            stack = newStack,
            gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
        )

        updatedCtx.copy(
            callStack = updatedCtx.callStack + newCall,
            evmState = newEvmState2
        )
    }

    fun delegateCall(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(7)
        val (g, a, inLocation, inSize, outLocation, outSize) = elements

        val gas = g.toBigInt()
        val address = a.toAddress()

        if (currentCallContext.gasRemaining < gas) {
            TODO("handle case where not enugh gas remaining")
        }

        val code = evmState.contractAt(address)?.code ?: emptyList() // TODO - what if code is empty

        val newCall = CallContext(
            currentCallContext.caller,
            memory.get(inLocation.toInt(), inSize.toInt()),
            currentCallContext.contract, // TODO - check that this is correct
            CallType.DELEGATECALL,
            currentCallContext.value,
            code,
            gas,
            outLocation.toInt(),
            outSize.toInt()
        )

        val updatedCtx = updateCurrentCallContext(
            stack = newStack,
            gasRemaining = currentCallContext.gasRemaining - gas // TODO should only subtract gas used
        )

        updatedCtx.copy(
            callStack = updatedCtx.callStack + newCall
        )
    }

    fun staticCall(context: ExecutionContext): ExecutionContext  {
        TODO()
    }
}

