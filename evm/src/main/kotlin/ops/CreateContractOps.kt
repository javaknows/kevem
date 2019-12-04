package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.keccak256
import com.gammadex.kevin.evm.model.*
import com.gammadex.kevin.evm.model.Byte
import com.gammadex.kevin.evm.numbers.generateAddress
import com.gammadex.kevin.evm.numbers.generateAddressFromSenderAndNonce
import java.math.BigInteger

// TODO - create a compatibility pack around this
object CreateContractOps {
    fun create(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (v, p, s) = elements

        val sender = context.currentCallCtx.caller
        val nonce = context.accounts.nonceOf(sender)
        val newContractAddress = generateAddressFromSenderAndNonce(sender, nonce)

        return createContract(p.toInt(), s.toInt(), newContractAddress, v, newStack, context)
    }

    fun create2(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(4)
        val (v, n, p, s) = elements

        val (codeData, _) = memory.read(p.toInt(), s.toInt())
        val contractAddress =
            currentCallCtx.contractAddress ?: throw RuntimeException("can't determine contract address")
        val newContractAddress = generateAddress(
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
        val (currentBalance, currentAddress) = with(currentCallCtx) {
            val current  = contractAddress ?: throw RuntimeException("can't determine contract address")
            Pair(accounts.balanceOf(current), current)
        }

        return when {
            context.accounts.accountExists(atAddress) -> {
                val message = "There is already a contract at $atAddress"
                HaltOps.fail(context, EvmError(ErrorCode.CONTRACT_EXISTS, message))
            }
            currentBalance < v.toBigInt() -> {
                val message = "$currentAddress has balance of $currentBalance but attempted to send $v"
                HaltOps.fail(context, EvmError(ErrorCode.INSUFFICIENT_FUNDS, message))
            }
            else -> {
                val (newContractCode, newMemory) = memory.read(p, s)
                val contract = Contract(newContractCode)
                val balance = v.toBigInt()
                val newEvmState = accounts
                    .updateBalanceAndContract(atAddress, balance, contract)
                    .updateBalance(currentAddress, accounts.balanceOf(currentAddress).subtract(balance))

                val newStack2 = newStack.pushWord(atAddress.toWord())

                context
                    .copy(accounts = newEvmState)
                    .updateCurrentCallCtx(stack = newStack2, memory = newMemory)
            }
        }
    }
}


