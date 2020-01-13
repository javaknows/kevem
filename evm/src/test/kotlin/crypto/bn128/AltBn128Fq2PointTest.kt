package org.kevm.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevm.evm.crypto.bn128.AltBn128Fq2Point
import org.kevm.evm.crypto.bn128.FieldElement
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class AltBn128Fq2PointTest {

    @Test
    fun shouldProduceTheSameResultUsingAddsAndDoublings() {
        assertThat(AltBn128Fq2Point.g2().doub() + (AltBn128Fq2Point.g2()) + (AltBn128Fq2Point.g2()))
            .isEqualTo(AltBn128Fq2Point.g2().doub().doub())
    }

    @Test
    fun shouldNotEqualEachOtherWhenDiferentPoints() {
        assertThat(AltBn128Fq2Point.g2().doub()).isNotEqualTo(
            AltBn128Fq2Point.g2())
    }

    @Test
    fun shouldEqualEachOtherWhenImpartialFractionsAreTheSame() {
        assertThat(
            AltBn128Fq2Point.g2() * (BigInteger("9")) + (AltBn128Fq2Point.g2() * (BigInteger("5")))
        ).isEqualTo(AltBn128Fq2Point.g2() * (BigInteger("12")) + (AltBn128Fq2Point.g2() * (BigInteger("2"))))
    }

    @Test
    fun shouldBeInfinityWhenMultipliedByCurveOrder() {
        val curveOrder = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        assertThat((AltBn128Fq2Point.g2() * (curveOrder)).isInfinity).isTrue()
    }

    @Test
    fun shouldNotBeInfinityWhenNotMultipliedByCurveOrder() { // assert not is_inf(multiply(g2(), 2 * field_modulus - curve_order))
        val two = BigInteger.valueOf(2)
        val curveOrder = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        val factor = two.multiply(FieldElement.FIELD_MODULUS).subtract(curveOrder)

        assertThat((AltBn128Fq2Point.g2() * factor).isInfinity).isFalse()
    }
}
