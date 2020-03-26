package org.kevem.rpc

import org.kevem.evm.StatefulTransactionProcessor
import org.kevem.evm.collections.BigIntegerIndexedList
import org.kevem.evm.model.*
import org.kevem.evm.model.Byte
import org.kevem.evm.toByteList
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class TestRPC(val evm: StatefulTransactionProcessor) {

    fun setChainParams(genesisConfig: SetChainParamsRequestDTO): Boolean {
        // TODO use default from somewhere else

        val difficulty = toBigIntegerOr(genesisConfig.genesis.difficulty, BigInteger.ONE)
        val gasLimit = toBigIntegerOr(genesisConfig.genesis.gasLimit, BigInteger.ONE)
        val timestamp =
            genesisConfig.genesis.timestamp?.let { Instant.ofEpochMilli(toBigInteger(it).toLong()) } ?: Instant.MIN

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
                    balance = toBigIntegerOrZero(accountParam.balance),
                    contract = accountParam.code?.let { c ->
                        val code = toByteList(c)
                        val storageContents = accountParam.storage?.map { s ->
                            val (index, contents) = s
                            Pair(toBigInteger(index), Word.coerceFrom(contents))
                        }?.toMap() ?: emptyMap()
                        Contract(BigIntegerIndexedList.fromBytes(code), Storage(storageContents))
                    },
                    nonce = toBigIntegerOrZero(accountParam.nonce)
                )
            )
        }.toMap()

        val worldState = WorldState(
            listOf(minedBlock),
            Accounts(accounts)
        )

        evm.setWorldState(worldState)

        return true;
    }

    fun mineBlocks(): Boolean {
        evm.mine()
        return true
    }

    fun revertToBlock(number: BigInteger): Boolean {
        evm.revertToBlock(number)
        return true
    }

    fun modifyTimestamp(timestamp: Long): Boolean {
        val fixedClock = Clock.fixed(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
        evm.setClock(fixedClock)
        return true
    }
}