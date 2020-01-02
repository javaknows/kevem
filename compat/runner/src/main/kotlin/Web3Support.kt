package org.kevm.compat.runner

import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

internal data class ConstantGasProvider(val limit: BigInteger, val price: BigInteger) : ContractGasProvider {
    override fun getGasLimit(contractFunc: String?): BigInteger = limit
    override fun getGasLimit(): BigInteger = limit
    override fun getGasPrice(contractFunc: String?): BigInteger = price
    override fun getGasPrice(): BigInteger = price
}
