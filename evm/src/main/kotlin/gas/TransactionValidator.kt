package org.kevem.evm.gas

import org.kevem.common.Logger
import org.kevem.evm.model.Features
import org.kevem.evm.model.TransactionMessage
import org.kevem.evm.model.WorldState

class TransactionValidator(
    private val log: Logger = Logger.createLogger(TransactionValidator::class),
    private val txGasCalculator: TransactionGasCalculator = TransactionGasCalculator()
) {

    fun isValid(worldState: WorldState, transaction: TransactionMessage, features: Features): Boolean = with(txGasCalculator) {
        val intrinsicGas = intrinsicGas(transaction, features)
        val upFrontCost = upFrontCost(transaction)

        when {
            transaction.nonce != worldState.accounts.nonceOf(transaction.from) -> {
                log.info("nonce of ${transaction.from} is invalid - tx has ${transaction.nonce} but expected ${worldState.accounts.nonceOf(transaction.from)}")
                false
            }
            intrinsicGas > transaction.gasLimit -> {
                log.info("instrinsic gas $intrinsicGas > than transaction gas ${transaction.gasLimit}")
                false
            }
            upFrontCost > worldState.accounts.balanceOf(transaction.from) -> {
                log.info("up front cost $upFrontCost > account balance of ${worldState.accounts.balanceOf(transaction.from)}")
                false
            }
            else -> true
        }
    }
}