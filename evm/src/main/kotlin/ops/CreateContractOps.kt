package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte

// TODO - create a compatibility pack around this
object CreateContractOps {
    fun create(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (v, p, s) = elements

        val sender = context.currentCallCtx.caller
        val nonce = context.evmState.nonceOf(sender)
        val newContractAddress = keccak256(sender.toWord().data + Word.coerceFrom(nonce).data).toAddress()

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
        val (callerBalance, callerAddress) = with(currentCallCtx) {
            Pair(evmState.balanceOf(caller), caller)
        }

        return when {
            context.evmState.accountExists(atAddress) -> {
                val message = "There is already a contract at $atAddress"
                HaltOps.fail(context, EvmError(ErrorCode.CONTRACT_EXISTS, message))
            }
            callerBalance < v.toBigInt() -> {
                val message = "$callerAddress has balance of $callerBalance but attempted to send $v"
                HaltOps.fail(context, EvmError(ErrorCode.INSUFFICIENT_FUNDS, message))
            }
            else -> {
                val (newContractCode, newMemory) = memory.read(p, s)
                val contract = Contract(newContractCode)
                val balance = v.toBigInt()
                val sender = context.currentCallCtx.caller
                val newEvmState = evmState
                    .updateBalanceAndContract(atAddress, balance, contract)
                    .updateBalance(sender, evmState.balanceOf(sender).subtract(balance))

                val newStack2 = newStack.pushWord(atAddress.toWord())

                context
                    .copy(evmState = newEvmState)
                    .updateCurrentCallCtx(stack = newStack2, memory = newMemory)
            }
        }
    }
}

private fun createAddress(address: List<Byte>, salt: List<Byte>, codeData: List<Byte>): Address =
    keccak256(listOf(Byte(0xFF)) + address + salt + keccak256(codeData).data).toAddress()
