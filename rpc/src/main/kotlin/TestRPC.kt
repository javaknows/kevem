package org.kevm.rpc

import org.kevm.evm.StatefulTransactionProcessor
import org.kevm.evm.model.*
import org.kevm.evm.model.Byte
import org.kevm.evm.toByteList
import java.math.BigInteger
import java.time.Instant

class TestRPC(val evm: StatefulTransactionProcessor) {

    fun setChainParams(genesisConfig: SetChainParamsRequestDTO) {
        // TODO use default from somewhere else
        val difficulty = genesisConfig.genesis.difficulty?.toBigInteger() ?: BigInteger.ONE
        val gasLimit = genesisConfig.genesis.gasLimit?.toBigInteger() ?: BigInteger.ONE
        val timestamp = genesisConfig.genesis.timestamp?.let { Instant.ofEpochMilli(it.toLong()) } ?: Instant.MIN

        // TODO - add author
        val minedBlock = MinedBlock(
            Block(
                BigInteger.ONE,
                difficulty,
                gasLimit,
                timestamp
            ),
            BigInteger.ZERO,
            listOf(Byte(1))
        )

        val accounts = genesisConfig.accounts.map {
            val (addressParam, accountParam) = it
            val address = Address(addressParam)

            Pair(
                address,
                Account(
                    address = address,
                    balance = toBigIntegerNullZero(accountParam.balance),
                    contract = accountParam.code?.let { c ->
                        val code = toByteList(c)
                        val storageContents = accountParam.storage?.map { s ->
                            val (index, contents) = s
                            Pair(toBigInteger(index), Word.coerceFrom(contents))
                        }?.toMap() ?: emptyMap()
                        Contract(code, Storage(storageContents))
                    },
                    nonce = toBigIntegerNullZero(accountParam.nonce)
                )
            )
        }.toMap()

        val worldState = WorldState(
            listOf(minedBlock),
            Accounts(accounts),
            Address("0x0")
        )

        evm.setWorldState(worldState)
    }
}