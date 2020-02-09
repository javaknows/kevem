package org.kevm.evm.gas

import org.kevm.common.Logger
import org.kevm.evm.model.TransactionMessage
import org.kevm.evm.model.WorldState

class TransactionValidator(
    private val log: Logger = Logger.createLogger(TransactionValidator::class),
    private val txGasCalculator: TransactionGasCalculator = TransactionGasCalculator()
) {

    fun isValid(worldState: WorldState, transaction: TransactionMessage): Boolean = with(txGasCalculator) {
        val intrinsicGas = intrinsicGas(transaction)
        val upFrontCost = upFrontCost(transaction)

        when {
            transaction.nonce != worldState.accounts.nonceOf(transaction.from) -> {
                log.info("nonce is invalid - ${transaction.nonce} but expected ${worldState.accounts.nonceOf(transaction.from)}")
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