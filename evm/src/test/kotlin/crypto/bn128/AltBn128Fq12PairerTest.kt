package org.kevem.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevem.evm.crypto.bn128.AltBn128Fq12Pairer
import org.kevem.evm.crypto.bn128.AltBn128Fq2Point
import org.kevem.evm.crypto.bn128.AltBn128Point
import org.kevem.evm.crypto.bn128.Fq12
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class AltBn128Fq12PairerTest {
    @Test
    fun shouldEqualOneWhenNegatedPairsAreMultiplied() {
        val p1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val p1Finalzied: Fq12 = AltBn128Fq12Pairer.finalize(p1Paired)
        val pn1Paired: Fq12 = AltBn128Fq12Pairer.pair(-AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val pn1Finalzied: Fq12 = AltBn128Fq12Pairer.finalize(pn1Paired)
        assertThat((p1Finalzied * pn1Finalzied)).isEqualTo(Fq12.one())
    }

    @Test
    fun shouldEqualOneWhenNegatedPairsAreMultipliedBothWays() {
        val p1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val p1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p1Paired)
        val pn1Paired: Fq12 = AltBn128Fq12Pairer.pair(-AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val pn1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(pn1Paired)
        val np1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), -AltBn128Fq2Point.g2())
        val np1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(np1Paired)
        assertThat((p1Finalized * np1Finalized)).isEqualTo(Fq12.one())
        assertThat(pn1Finalized).isEqualTo(np1Finalized)
    }

    @Test
    fun shouldEqualOneWhenRaisedToCurveOrder() {
        val p1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val p1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p1Paired)
        val curveOrder = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        assertThat(p1Finalized.power(curveOrder)).isEqualTo(Fq12.one())
    }

    @Test
    fun shouldBeBilinear() {
        val p1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val p1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p1Paired)
        val p2Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1() * (BigInteger.valueOf(2)), AltBn128Fq2Point.g2()
        )
        val p2Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p2Paired)
        assertThat((p1Finalized * p1Finalized)).isEqualTo(p2Finalized)
    }

    @Test
    fun shouldBeNongenerate() {
        val p1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), AltBn128Fq2Point.g2())
        val p1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p1Paired)
        val p2Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1() * (BigInteger.valueOf(2)), AltBn128Fq2Point.g2()
        )
        val p2Finalized: Fq12 = AltBn128Fq12Pairer.finalize(p2Paired)
        val np1Paired: Fq12 = AltBn128Fq12Pairer.pair(
            AltBn128Point.g1(), -AltBn128Fq2Point.g2())
        val np1Finalized: Fq12 = AltBn128Fq12Pairer.finalize(np1Paired)
        assertThat(p1Finalized).isNotEqualTo(p2Finalized)
        assertThat(p1Finalized).isNotEqualTo(np1Finalized)
        assertThat(p2Finalized).isNotEqualTo(np1Finalized)
    }
}
