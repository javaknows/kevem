package test

import org.kevm.evm.crypto.keccak256
import org.kevm.evm.model.*
import java.math.BigInteger
import java.time.Instant

object TestObjects {

    var worldState: WorldState = WorldState(
        listOf(
            MinedBlock(
                Block(
                    number = BigInteger.ONE,
                    difficulty = BigInteger.ONE,
                    gasLimit = BigInteger("1000000000000000000000000000000"),
                    timestamp = Instant.parse("2006-12-03T10:15:30.00Z")
                ),
                BigInteger.TWO,
                keccak256(Word.coerceFrom(BigInteger.ONE).data).data
            )
        ),
        Accounts(),
        Address("0xFFEEDD")
    )

    val tx = TransactionMessage(
        Address("0x1"), Address("0x2"), BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, emptyList(), BigInteger.ZERO
    )

    val block2 = Block(
        number = BigInteger.TWO,
        difficulty = BigInteger.TWO,
        gasLimit = BigInteger("1000000000000000000000000000000"),
        timestamp = Instant.parse("2006-12-04T10:15:30.00Z")
    )

}