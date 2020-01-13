package org.kevm.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevm.evm.crypto.bn128.AltBn128Fq12Point
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class AltBn128Fq12PointTest {

    @Test
    fun shouldProduceTheSameResultUsingAddsAndDoublings() {
        assertThat(
            AltBn128Fq12Point.g12().doub() + (AltBn128Fq12Point.g12()) + (AltBn128Fq12Point.g12())
        ).isEqualTo(AltBn128Fq12Point.g12().doub().doub())
    }

    @Test
    fun shouldNotEqualEachOtherWhenDiferentPoints() {
        assertThat(AltBn128Fq12Point.g12().doub()).isNotEqualTo(
            AltBn128Fq12Point.g12())
    }

    @Test
    fun shouldEqualEachOtherWhenImpartialFractionsAreTheSame() {
        assertThat(
            AltBn128Fq12Point.g12() * (BigInteger.valueOf(9)) + (AltBn128Fq12Point.g12() * (BigInteger("5")))
        ).isEqualTo(
            AltBn128Fq12Point.g12() * (BigInteger.valueOf(12)) + (AltBn128Fq12Point.g12() * (
                BigInteger("2")
            ))
        )
    }

    @Test
    fun shouldBeInfinityWhenMultipliedByCurveOrder() {
        val curveOrder = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        assertThat((AltBn128Fq12Point.g12() * (curveOrder)).isInfinity).isTrue()
    }
}
