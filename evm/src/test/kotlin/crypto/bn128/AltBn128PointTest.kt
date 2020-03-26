package org.kevem.evm.crypto.zksnarks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.kevem.evm.crypto.bn128.AltBn128Point
import org.kevem.evm.crypto.bn128.Fq
import java.math.BigInteger

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

class AltBn128PointTest {

    @Test
    fun shouldReturnEquivalentValueByAdditionAndDouble() {
        assertThat(AltBn128Point.g1().doub() + (AltBn128Point.g1()) + (AltBn128Point.g1()))
            .isEqualTo(AltBn128Point.g1().doub().doub())
    }

    @Test
    fun shouldReturnInequivalentValueOnIdentityVsDouble() {
        assertThat(AltBn128Point.g1().doub()).isNotEqualTo(
            AltBn128Point.g1())
    }

    @Test
    fun shouldReturnEqualityOfValueByEquivalentAdditionMultiplication() {
        assertThat(
            AltBn128Point.g1()
                    * (BigInteger.valueOf(9))
                    + (AltBn128Point.g1() * (BigInteger.valueOf(5)))
        )
            .isEqualTo(
                AltBn128Point.g1()
                        * (BigInteger.valueOf(12))
                        + (AltBn128Point.g1() * (BigInteger.valueOf(2)))
            )
    }

    @Test
    fun shouldReturnInfinityOnMultiplicationByCurveOrder() {
        val curveOrder = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        assertThat((AltBn128Point.g1() * curveOrder).isInfinity).isTrue()
    }

    @Test
    fun shouldReturnTrueWhenValuesAreInfinityBigIntZero() {
        val p = AltBn128Point(
            Fq(0),
            Fq(0)
        )
        assertThat(p.isInfinity).isTrue()
    }

    @Test
    fun shouldReturnInfinityWhenAddingTwoInfinities() {
        val p0 = AltBn128Point.INFINITY
        val p1 = AltBn128Point.INFINITY
        assertThat(p0 + p1).isEqualTo(AltBn128Point.INFINITY)
    }

    @Test
    fun shouldReturnTrueWhenEqual() {
        val p0 = AltBn128Point(
            Fq(3),
            Fq(4)
        )
        val p1 = AltBn128Point(
            Fq(3),
            Fq(4)
        )
        assertThat(p0.equals(p1)).isTrue()
    }

    @Test
    fun shouldReturnFalseWhenNotEqual() {
        val p0 = AltBn128Point(
            Fq(4),
            Fq(4)
        )
        val p1 = AltBn128Point(
            Fq(3),
            Fq(4)
        )
        assertThat(p0.equals(p1)).isFalse()
    }

    @Test
    fun shouldReturnIdentityWhenPointAddInfinity() {
        val p0 = AltBn128Point(
            Fq(1),
            Fq(2)
        )
        val p1 = AltBn128Point(
            Fq(0),
            Fq(0)
        )
        assertThat((p0 + p1).equals(
            AltBn128Point(
                Fq(
                    1
                ), Fq(2)
            )
        )).isTrue()
    }

    @Test
    fun shouldReturnPointOnInfinityAddPoint() {
        val p0 =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        0
                    )
                ), Fq(BigInteger.valueOf(0))
            )
        val p1 =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        assertThat((p0 + p1).equals(p1)).isTrue()
    }

    @Test
    fun shouldReturnTrueSumOnDoubling() {
        val p0 =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val p1 =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val sumX: Fq = Fq(
            BigInteger(
                "1368015179489954701390400359078579693043519447331113978918064868415326638035"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "9918110051302171585080402603319702774565515993150576347155970296011118125764"
            )
        )
        assertThat((p0 + p1).equals(AltBn128Point(sumX, sumY))).isTrue()
    }

    @Test
    fun shouldReturnInfinityOnIdenticalInputPointValuesOfX() {
        val p0x: Fq = Fq(
            BigInteger(
                "10744596414106452074759370245733544594153395043370666422502510773307029471145"
            )
        )
        val p0y: Fq = Fq(
            BigInteger(
                "848677436511517736191562425154572367705380862894644942948681172815252343932"
            )
        )
        val p0 = AltBn128Point(p0x, p0y)
        val p1x: Fq = Fq(
            BigInteger(
                "10744596414106452074759370245733544594153395043370666422502510773307029471145"
            )
        )
        val p1y: Fq = Fq(
            BigInteger(
                "21039565435327757486054843320102702720990930294403178719740356721829973864651"
            )
        )
        val p1 = AltBn128Point(p1x, p1y)
        assertThat((p0 + p1).equals(AltBn128Point.INFINITY)).isTrue()
    }

    @Test
    fun shouldReturnTrueAddAndComputeSlope() {
        val p0x: Fq = Fq(
            BigInteger(
                "10744596414106452074759370245733544594153395043370666422502510773307029471145"
            )
        )
        val p0y: Fq = Fq(
            BigInteger(
                "848677436511517736191562425154572367705380862894644942948681172815252343932"
            )
        )
        val p0 = AltBn128Point(p0x, p0y)
        val p1x: Fq = Fq(
            BigInteger(
                "1624070059937464756887933993293429854168590106605707304006200119738501412969"
            )
        )
        val p1y: Fq = Fq(
            BigInteger(
                "3269329550605213075043232856820720631601935657990457502777101397807070461336"
            )
        )
        val p1 = AltBn128Point(p1x, p1y)
        val sumX: Fq = Fq(
            BigInteger(
                "9836339169314901400584090930519505895878753154116006108033708428907043344230"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "2085718088180884207082818799076507077917184375787335400014805976331012093279"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((p0 + p1).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnInfinityWhenMultiplierIsInfinity() {
        val px: Fq = Fq(
            BigInteger(
                "11999875504842010600789954262886096740416429265635183817701593963271973497827"
            )
        )
        val py: Fq = Fq(
            BigInteger(
                "11843594000332171325303933275547366297934113019079887694534126289021216356598"
            )
        )
        val p = AltBn128Point(px, py)
        val multiplier = BigInteger.ZERO
        assertThat((p * multiplier).equals(AltBn128Point.INFINITY)).isTrue()
    }

    @Test
    fun shouldReturnTrueMultiplyScalarAndPoint() {
        val multiplicand =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val multiplier = BigInteger(
            "115792089237316195423570985008687907853269984665640564039457584007913129639935"
        )
        val sumX: Fq = Fq(
            BigInteger(
                "21415159568991615317144600033915305503576371596506956373206836402282692989778"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "8573070896319864868535933562264623076420652926303237982078693068147657243287"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnIdentityWhenMultipliedByScalarValueOne() {
        val multiplicandX: Fq =
            Fq(
                BigInteger(
                    "11999875504842010600789954262886096740416429265635183817701593963271973497827"
                )
            )
        val multiplicandY: Fq =
            Fq(
                BigInteger(
                    "11843594000332171325303933275547366297934113019079887694534126289021216356598"
                )
            )
        val multiplicand = AltBn128Point(multiplicandX, multiplicandY)
        val multiplier = BigInteger.valueOf(1)
        val sumX: Fq = Fq(
            BigInteger(
                "11999875504842010600789954262886096740416429265635183817701593963271973497827"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "11843594000332171325303933275547366297934113019079887694534126289021216356598"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnTrueMultiplyPointByScalar() {
        val multiplicand =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val multiplier = BigInteger.valueOf(9)
        val sumX: Fq = Fq(
            BigInteger(
                "1624070059937464756887933993293429854168590106605707304006200119738501412969"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "3269329550605213075043232856820720631601935657990457502777101397807070461336"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnInfinityMultiplyPointByFieldModulus() {
        val multiplicandX: Fq =
            Fq(
                BigInteger(
                    "11999875504842010600789954262886096740416429265635183817701593963271973497827"
                )
            )
        val multiplicandY: Fq =
            Fq(
                BigInteger(
                    "11843594000332171325303933275547366297934113019079887694534126289021216356598"
                )
            )
        val multiplicand = AltBn128Point(multiplicandX, multiplicandY)
        val multiplier = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495617"
        )
        assertThat((multiplicand * multiplier).equals(AltBn128Point.INFINITY)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_0() {
        val multiplicandX: Fq =
            Fq(
                BigInteger(
                    "11999875504842010600789954262886096740416429265635183817701593963271973497827"
                )
            )
        val multiplicandY: Fq =
            Fq(
                BigInteger(
                    "11843594000332171325303933275547366297934113019079887694534126289021216356598"
                )
            )
        val multiplicand = AltBn128Point(multiplicandX, multiplicandY)
        val multiplier = BigInteger.valueOf(2)
        val sumX: Fq = Fq(
            BigInteger(
                "1735584146725871897168740753407579795109098319299249929076571979506257370192"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "6064265680718387101814183009970545962379849150498077125987052947023017993936"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_1() {
        val multiplicandX: Fq =
            Fq(
                BigInteger(
                    "11999875504842010600789954262886096740416429265635183817701593963271973497827"
                )
            )
        val multiplicandY: Fq =
            Fq(
                BigInteger(
                    "11843594000332171325303933275547366297934113019079887694534126289021216356598"
                )
            )
        val multiplicand = AltBn128Point(multiplicandX, multiplicandY)
        val multiplier = BigInteger.valueOf(9)
        val sumX: Fq = Fq(
            BigInteger(
                "13447195743588318540108422034660542894354216867239950480700468911927695682420"
            )
        )
        val sumY: Fq = Fq(
            BigInteger(
                "20282243652944194694550455553589850678366346583698568858716117082144718267765"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_2() {
        val multiplicand =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val multiplier = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495616"
        )
        val sumX: Fq =
            Fq(BigInteger.valueOf(1))
        val sumY: Fq = Fq(
            BigInteger(
                "21888242871839275222246405745257275088696311157297823662689037894645226208581"
            )
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_3() {
        val multiplicandX: Fq =
            Fq(
                BigInteger(
                    "11999875504842010600789954262886096740416429265635183817701593963271973497827"
                )
            )
        val multiplicandY: Fq =
            Fq(
                BigInteger(
                    "11843594000332171325303933275547366297934113019079887694534126289021216356598"
                )
            )
        val multiplicand = AltBn128Point(multiplicandX, multiplicandY)
        val multiplier = BigInteger(
            "21888242871839275222246405745257275088548364400416034343698204186575808495616"
        )
        val sumX: Fq = Fq(
            BigInteger(
                "11999875504842010600789954262886096740416429265635183817701593963271973497827"
            )
        )
        val sumY: Fq = Fq(
            BigInteger("10044648871507103896942472469709908790762198138217935968154911605624009851985")
        )
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_4() {
        val multiplicand =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val multiplier = BigInteger("340282366920938463463374607431768211456")
        val sumX =
            Fq(BigInteger("8920802327774939509523725599419958131004060744305956036272850138837360588708"))
        val sumY =
            Fq(BigInteger("15515729996153051217274459095713198084165220977632053298080637275617709055542"))
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

    @Test
    fun shouldReturnSumMultiplyPointByScalar_5() {
        val multiplicand =
            AltBn128Point(
                Fq(
                    BigInteger.valueOf(
                        1
                    )
                ), Fq(BigInteger.valueOf(2))
            )
        val multiplier = BigInteger.valueOf(2)
        val sumX =
            Fq(BigInteger("1368015179489954701390400359078579693043519447331113978918064868415326638035"))
        val sumY =
            Fq(BigInteger("9918110051302171585080402603319702774565515993150576347155970296011118125764"))
        val sum = AltBn128Point(sumX, sumY)
        assertThat((multiplicand * multiplier).equals(sum)).isTrue()
    }

}