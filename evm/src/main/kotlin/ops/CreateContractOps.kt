package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte

object CreateContractOps {
    fun create(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (v, p, s) = elements

        val sender = context.currentCallCtx.caller
        val nonce = context.evmState.nonceOf(sender)
        val newContractAddress =  keccak256(sender.toWord().data + Word.coerceFrom(nonce).data).toAddress()

        return createContract(p.toInt(), s.toInt(), newContractAddress, v, newStack, context)
    }

    fun create2(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(4)
        val (v, n, p, s) = elements

        val (codeData, _) = memory.read(p.toInt(), s.toInt())
        val contractAddress =
            currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
        val newContractAddress = createAddress(
            contractAddress.toWord().data,
            n.data,
            codeData
        )

        return createContract(p.toInt(), s.toInt(), newContractAddress, v, newStack, context)
    }

    private fun ExecutionContext.createContract(
        p: Int, s: Int,
        atAddress: Address,
        v: Word,
        newStack: Stack,
        context: ExecutionContext
    ): ExecutionContext {
        // TODO - what if current contract doesn't have enough wei to send
        // TODO - what if the generated address already exists

        val (newContractCode, newMemory) = memory.read(p, s)
        val contract = Contract(newContractCode)
        val balance = v.toBigInt()
        val currentAddress =
            currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
        val newEvmState = evmState
            .updateBalanceAndContract(atAddress, balance, contract)
            .updateBalance(currentAddress, evmState.balanceOf(currentAddress).subtract(balance))

        val newStack2 = newStack.pushWord(atAddress.toWord())

        return context
            .copy(evmState = newEvmState)
            .updateCurrentCallCtx(stack = newStack2, memory = newMemory)
    }

    // TODO - create a compatibility pack around this
    private fun createAddress(address: List<Byte>, n: List<Byte>, mem: List<Byte>): Address =
        keccak256(address + n + keccak256(mem).data).toAddress()
}