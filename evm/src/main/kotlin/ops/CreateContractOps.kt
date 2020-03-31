package org.kevem.evm.ops

import org.kevem.common.KevemException
import org.kevem.common.Logger
import org.kevem.evm.EIP
import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.evm.gas.TransactionValidator
import org.kevem.evm.model.*
import org.kevem.evm.numbers.generateAddress
import org.kevem.evm.numbers.generateAddressFromSenderAndNonce
import java.math.BigInteger

object CreateContractOps {
    private val log: Logger = Logger.createLogger(CreateContractOps::class)

    fun create(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(3)
        val (v, p, s) = elements

        val sender = context.currentCallCtx.contractAddress ?: context.currentCallCtx.caller
        val nonce = context.accounts.nonceOf(sender)
        val newContractAddress = generateAddressFromSenderAndNonce(sender, nonce)

        return createContract(p.toInt(), s.toInt(), newContractAddress, v, newStack, context)
    }

    fun create2(context: ExecutionContext): ExecutionContext = with(context) {
        val (elements, newStack) = stack.popWords(4)
        val (v, n, p, s) = elements

        val (codeData, _) = memory.read(p.toBigInt(), s.toInt())
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
            val current = contractAddress ?: throw RuntimeException("can't determine contract address")
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
                val (newContractCode, newMemory) = memory.read(p.toBigInteger(), s) // TODO - can overflow

                if (context.config.features.isEnabled(EIP.EIP170) && newContractCode.size > 0x6000) {
                    log.debug("Failing execution with OUT_OF_GAS due to eip170 contract size breach")
                    HaltOps.fail(context, EvmError(ErrorCode.OUT_OF_GAS, "Out of gas"))
                } else {
                    val contractAddress = context.currentCallCtx.contractAddress
                        ?: throw KevemException("can't determine contract address")
                    val contract = Contract(BigIntegerIndexedList.fromBytes(newContractCode))
                    val balance = v.toBigInt()
                    val newEvmState = accounts
                        .updateBalanceAndContract(atAddress, balance, contract)
                        .updateBalance(currentAddress, accounts.balanceOf(currentAddress).subtract(balance))
                        .updateNonce(contractAddress, accounts.nonceOf(contractAddress).add(BigInteger.ONE))

                    val newStack2 = newStack.pushWord(atAddress.toWord())

                    context
                        .copy(accounts = newEvmState)
                        .updateCurrentCallCtx(stack = newStack2, memory = newMemory)
                }
            }
        }
    }
}
