package org.kevm.web3.test

import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

class ConfigurableGasProvider : ContractGasProvider {

    private var limit: BigInteger = BigInteger.ONE

    private var price: BigInteger = BigInteger.ONE

    fun set(limit: BigInteger, price: BigInteger) {
        this.limit = limit
        this.price = price
    }

    override fun getGasLimit(contractFunc: String?) = getGasLimit()

    override fun getGasPrice(contractFunc: String?) = getGasPrice()

    override fun getGasLimit() = limit

    override fun getGasPrice() = price
}