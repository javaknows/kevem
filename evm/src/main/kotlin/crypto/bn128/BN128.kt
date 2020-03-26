package org.kevem.evm.crypto.bn128

import java.math.BigInteger
import java.util.*

/**
 * Adapted from the altbn128 (Apache 2 License) java implementation:
 * https://github.com/hyperledger/besu/tree/master/crypto/src/main/java/org/hyperledger/besu/crypto/altbn128
 *
 * Which was adapted from:
 * https://github.com/ethereum/py_ecc/blob/master/py_ecc/bn128/bn128_field_elements.py
 */

private val ZERO = BigInteger.ZERO
private val ONE = BigInteger.ONE
private val TWO = BigInteger.valueOf(2)

interface FieldPoint<T : FieldPoint<T>> {
    val isInfinity: Boolean

    operator fun plus(other: T): T
    operator fun times(n: BigInteger): T
    fun doub(): T
    operator fun unaryMinus(): T
}

interface FieldElement<T : FieldElement<T>> {
    val isValid: Boolean
    val isZero: Boolean

    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(m: Int): T
    operator fun times(other: T): T
    operator fun unaryMinus(): T
    operator fun div(other: T): T

    fun power(n: Int): T
    fun power(n: BigInteger): T

    fun repeat(times: Int): List<T> = (0 until times).map { this as T }

    companion object {
        val FIELD_MODULUS = BigInteger("21888242871839275222246405745257275088696311157297823662689037894645226208583")
    }
}

data class Fq(val n: BigInteger) :
    FieldElement<Fq> {
    constructor(n: Long) : this(BigInteger.valueOf(n))
    constructor(n: Int) : this(BigInteger.valueOf(n.toLong()))

    override val isZero: Boolean
        get() = n == ZERO

    override val isValid: Boolean
        get() = n < FieldElement.FIELD_MODULUS

    override operator fun plus(other: Fq): Fq =
        Fq((n + other.n).mod(FieldElement.FIELD_MODULUS))

    override operator fun minus(other: Fq): Fq =
        Fq((n - other.n).mod(FieldElement.FIELD_MODULUS))

    override operator fun times(m: Int): Fq = this * (Fq(
        m.toBigInteger()
    ))

    override operator fun times(other: Fq): Fq =
        Fq((n * other.n).mod(FieldElement.FIELD_MODULUS))

    override operator fun div(other: Fq): Fq {
        val inverse = inverse(other.n,
            FieldElement.FIELD_MODULUS
        )
        val result = (n * inverse).mod(FieldElement.FIELD_MODULUS)
        return Fq(result)
    }

    private fun inverse(a: BigInteger, n: BigInteger): BigInteger {
        if (a == ZERO) {
            return ZERO
        }

        var lm = ONE
        var hm = ZERO
        var low = a.mod(n)
        var high = n
        while (low > ONE) {
            val r = high.divide(low)
            val nm = hm.subtract(lm * r)
            val neww = high.subtract(low * r)
            high = low
            hm = lm
            low = neww
            lm = nm
        }

        return lm.mod(n)
    }

    override operator fun unaryMinus(): Fq =
        Fq(n.negate())

    override fun power(n: Int): Fq = when {
        n == 0 -> one()
        n == 1 -> this
        n % 2 == 0 -> (this * this).power(n / 2)
        else -> (this * this).power(n / 2) * this
    }

    override fun power(n: BigInteger): Fq = when {
        n == ZERO -> one()
        n == ONE -> this
        n.mod(TWO) == ZERO -> (this * this).power(n.divide(
            TWO
        ))
        else -> (this * this).power(n.divide(TWO)) * this
    }

    companion object {
        fun zero() = Fq(0)
        fun one() = Fq(1)
    }
}

abstract class BaseFqp<T : BaseFqp<T>>(
    val degree: Int,
    val modulusCoefficients: List<Fq>,
    val coefficients: List<Fq>
) : FieldElement<T> {

    init {
        require(degree == coefficients.size) {
            "point is $degree degree but got ${coefficients.size} coefficients"
        }
        require(degree == modulusCoefficients.size) {
            "point is $degree degree but got ${modulusCoefficients.size} modulus coefficients"
        }
    }

    protected abstract fun newInstance(coefficients: List<Fq>): T

    override val isValid: Boolean
        get() = coefficients.all { it.isValid }

    override val isZero: Boolean
        get() = coefficients.all { it.isZero }

    override operator fun plus(other: T): T =
        newInstance((coefficients zip other.coefficients).map { it.first + it.second })

    override operator fun minus(other: T): T =
        newInstance((coefficients zip other.coefficients).map { it.first - it.second })

    override operator fun times(m: Int): T = newInstance(coefficients.map { it * m })

    override operator fun times(other: T): T {
        val b = arrayOfNulls<Fq>(degree * 2 - 1)
        Arrays.fill(b, Fq.zero())
        for (i in 0 until degree) {
            for (j in 0 until degree) {
                b[i + j] = b[i + j]!! + coefficients[i] * other.coefficients[j]
            }
        }
        for (i in b.size downTo degree + 1) {
            val top = b[i - 1]
            val exp = i - degree - 1
            for (j in 0 until degree) {
                b[exp + j] = b[exp + j]!! - top!! * modulusCoefficients[j]
            }
        }

        return newInstance(Arrays.copyOfRange(b, 0, degree).filterNotNull().toList())
    }

    override operator fun div(other: T): T = this * newInstance(other.inverse())

    override operator fun unaryMinus(): T = newInstance(coefficients.map { -it })

    private fun one(): T = newInstance(listOf(Fq.one()) + Fq.zero().repeat(degree - 1))

    override fun power(n: Int): T = when {
        n == 0 -> one()
        n == 1 -> newInstance(coefficients)
        n % 2 == 0 -> (this * (this as T)).power(n / 2)
        else -> (this * (this as T)).power(n / 2) * this
    }

    override fun power(n: BigInteger): T = when {
        n == ZERO -> one()
        n == ONE -> this as T
        n.mod(TWO) == ZERO -> (this * (this as T)).power(n.divide(
            TWO
        ))
        else -> (this * (this as T)).power(n.divide(TWO)) * this
    }

    protected fun inverse(): List<Fq> {
        var lm = lm().toTypedArray()
        var hm = hm().toTypedArray()
        var low = low()
        var high = high()
        while (deg(low) > 0) {
            val r = polyRoundedDiv(high, low)
            val nm = Arrays.copyOf(hm, hm.size)
            val neww = Arrays.copyOf(high.toTypedArray(), high.size)
            for (i in 0 until degree + 1) {
                for (j in 0 until degree + 1 - i) {
                    nm[i + j] = nm[i + j]!! - (lm[i] * r[j])
                    neww[i + j] = neww[i + j] - (low[i] * r[j])
                }
            }
            high = low
            hm = lm
            low = neww.toList()
            lm = nm
        }
        for (i in lm.indices) {
            lm[i] = lm[i] / low[0]
        }
        return Arrays.copyOfRange(lm, 0, degree).filterNotNull().toList()
    }

    private fun lm(): List<Fq> = listOf(Fq.one()) + Fq.zero().repeat(degree)

    private fun hm(): List<Fq> = Fq.zero().repeat(degree + 1)

    private fun low(): List<Fq> = coefficients + Fq.zero()

    private fun high(): List<Fq> = modulusCoefficients + Fq.one()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is BaseFqp<*> -> false
        else -> degree == other.degree
                && modulusCoefficients == other.modulusCoefficients
                && coefficients == other.coefficients
    }

    override fun hashCode(): Int = Objects.hash(degree, Objects.hash(modulusCoefficients), Objects.hash(coefficients))

    override fun toString(): String = coefficients.toString()

    companion object {
        private fun polyRoundedDiv(a: List<Fq>, b: List<Fq>): List<Fq> {
            val degA = deg(a)
            val degB = deg(b)
            val temp = Arrays.copyOf(a.toTypedArray(), a.size)
            val o = arrayOfNulls<Fq>(a.size)
            Arrays.fill(o, Fq.zero())
            for (i in degA - degB downTo 0) {
                o[i] = o[i]!! + (temp[degB + i] / b[degB])
                for (j in 0..degB) {
                    temp[i + j] = temp[i + j] - (o[j]!!)
                }
            }
            return o.filterNotNull().toList()
        }

        private fun deg(p: List<Fq>): Int {
            var d = p.size - 1
            while (d >= 0 && p[d].equals(Fq.zero())) {
                --d
            }
            return d
        }
    }
}

class Fq2(coefficients: List<Fq>) : BaseFqp<Fq2>(
    DEGREE,
    MODULUS_COEFFICIENTS, coefficients) {
    constructor(c0: Long, c1: Long) : this(BigInteger.valueOf(c0), BigInteger.valueOf(c1))
    constructor(c0: BigInteger, c1: BigInteger) : this(listOf(
        Fq(c0),
        Fq(c1)
    ))

    override fun newInstance(coefficients: List<Fq>): Fq2 {
        return Fq2(coefficients)
    }

    companion object {
        private const val DEGREE = 2
        private val MODULUS_COEFFICIENTS = listOf(
            Fq(1),
            Fq(0)
        )

        fun zero(): Fq2 = Fq2(
            listOf(
                Fq.zero(),
                Fq.zero()
            )
        )
        fun one(): Fq2 = Fq2(
            listOf(
                Fq.one(),
                Fq.zero()
            )
        )
        fun b2(): Fq2 = Fq2(
            3,
            0
        ) / Fq2(9, 1)
    }
}


abstract class BaseFieldPoint<U : BaseFieldPoint<U, T>, T : FieldElement<T>>(
    open val x: FieldElement<T>,
    open val y: FieldElement<T>
) : FieldPoint<U> {

    protected abstract fun infinity(): U
    protected abstract fun newInstance(x: FieldElement<T>, y: FieldElement<T>): U

    override val isInfinity: Boolean
        get() = x.isZero && y.isZero

    override operator fun plus(other: U): U = when {
        isInfinity -> other
        other.isInfinity -> this as U
        this == other -> doub()
        x == other.x -> infinity()
        else -> {
            val m = (other.y - (y as T)) / (other.x - (x as T))
            val mSquared = m.power(2)
            val newX = (mSquared - (x as T)) - (other.x as T)
            val newY = (-m * newX) + m * (x as T) - (y as T)

            newInstance(newX, newY)
        }
    }

    override operator fun times(n: BigInteger): U = when {
        n == ZERO -> infinity()
        n == ONE -> newInstance(x, y)
        n.mod(TWO) == ZERO -> doub() * n.divide(
            TWO
        )
        else -> doub() * n.divide(TWO) + (this as U)
    }

    override fun doub(): U {
        val xSquared = x.power(2)
        val m = xSquared * 3 / (y * 2)
        val mSquared = m.power(2)
        val newX = mSquared - x * 2
        val newY = (-m * newX) + m * (x as T) - (y as T)

        return newInstance(newX, newY)
    }

    override operator fun unaryMinus(): U =
        if (isInfinity) this as U
        else newInstance(x, -y)

    override fun toString(): String = "{$x, $y}"

    override fun hashCode(): Int = Objects.hash(x, y)

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is BaseFieldPoint<*, *> -> false
        else -> x == other.x && y == other.y
    }
}

class Fq12(coefficients: List<Fq>) : BaseFqp<Fq12>(
    DEGREE,
    MODULUS_COEFFICIENTS, coefficients) {
    constructor(
        c0: Long, c1: Long, c2: Long, c3: Long, c4: Long, c5: Long, c6: Long, c7: Long, c8: Long, c9: Long,
        c10: Long, c11: Long
    ) : this(listOf(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11).map {
        Fq(
            it
        )
    })

    override fun newInstance(coefficients: List<Fq>): Fq12 {
        return Fq12(coefficients)
    }

    companion object {
        const val DEGREE = 12
        private val MODULUS_COEFFICIENTS = listOf(82, 0, 0, 0, 0, 0, -18, 0, 0, 0, 0, 0).map {
            Fq(
                it
            )
        }

        fun zero() = Fq12(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        fun one() = Fq12(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    }
}

class AltBn128Fq2Point(override val x: Fq2, override val y: Fq2) : BaseFieldPoint<AltBn128Fq2Point, Fq2>(x, y) {

    override fun infinity(): AltBn128Fq2Point {
        return AltBn128Fq2Point(
            Fq2.zero(),
            Fq2.zero()
        )
    }

    val isOnCurve: Boolean
        get() = when {
            !x.isValid || !y.isValid -> false
            isInfinity -> true
            else -> y.power(2) - x.power(3) == Fq2.b2()
        }

    val isInGroup: Boolean
        get() = (this * (CURVE_ORDER)).isInfinity

    override fun newInstance(x: FieldElement<Fq2>, y: FieldElement<Fq2>): AltBn128Fq2Point =
        AltBn128Fq2Point(
            x as Fq2,
            y as Fq2
        )

    companion object {
        private val CURVE_ORDER
                = BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617")

        fun g2(): AltBn128Fq2Point {
            val x = Fq2(
                BigInteger("10857046999023057135944570762232829481370756359578518086990519993285655852781"),
                BigInteger("11559732032986387107991004021392285783925812861821192530917403151452391805634")
            )
            val y = Fq2(
                BigInteger("8495653923123431417604973247489272438418190587263600148770280649306958101930"),
                BigInteger("4082367875863433681332203403145435568316851327593401208105741076214120093531")
            )
            return AltBn128Fq2Point(x, y)
        }
    }
}

class AltBn128Fq12Point(override val x: Fq12, override val y: Fq12) : BaseFieldPoint<AltBn128Fq12Point, Fq12>(x, y) {

    override fun infinity(): AltBn128Fq12Point =
        AltBn128Fq12Point(
            Fq12.zero(),
            Fq12.zero()
        )

    override fun newInstance(x: FieldElement<Fq12>, y: FieldElement<Fq12>): AltBn128Fq12Point =
        AltBn128Fq12Point(
            x as Fq12,
            y as Fq12
        )

    companion object {
        private val w = Fq12(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        fun g12(): AltBn128Fq12Point =
            twist(AltBn128Fq2Point.g2())

        fun twist(p: AltBn128Fq2Point): AltBn128Fq12Point {
            val zeros = Fq.zero().repeat(5)

            val nX = listOf(p.x.coefficients[0] - p.x.coefficients[1] * 9) + zeros + p.x.coefficients[1] + zeros
            val nY = listOf(p.y.coefficients[0] - p.y.coefficients[1] * 9) + zeros + p.y.coefficients[1] + zeros

            return AltBn128Fq12Point(
                Fq12(
                    nX
                ) * w.power(2),
                Fq12(nY) * w.power(
                    3
                )
            )
        }
    }
}

class AltBn128Point(override val x: Fq, override val y: Fq) : BaseFieldPoint<AltBn128Point, Fq>(x, y) {

    val isOnCurve: Boolean
        get() = when {
            !x.isValid || !y.isValid -> false
            isInfinity -> true
            else -> y.power(2) - x.power(3) == B
        }

    override fun infinity(): AltBn128Point =
        INFINITY

    override fun newInstance(x: FieldElement<Fq>, y: FieldElement<Fq>): AltBn128Point =
        AltBn128Point(
            x as Fq,
            y as Fq
        )

    companion object {
        val B = Fq(3)
        val INFINITY = AltBn128Point(
            Fq.zero(),
            Fq.zero()
        )

        fun g1(): AltBn128Point =
            AltBn128Point(
                Fq(1),
                Fq(2)
            )

    }
}

object AltBn128Fq12Pairer {
    private const val LOG_ATE_LOOP_COUNT = 63
    private val ATE_LOOP_COUNT = BigInteger("29793968203157093288")
    private val CURVE_ORDER
            = BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617")

    fun pair(p: AltBn128Point, q: AltBn128Fq2Point): Fq12 =
        millerLoop(
            cast(p),
            AltBn128Fq12Point.twist(q)
        )

    private fun cast(p: AltBn128Point): AltBn128Fq12Point {
        val newX = listOf(p.x) + Fq.zero().repeat(
            Fq12.DEGREE - 1)
        val newY = listOf(p.y) + Fq.zero().repeat(
            Fq12.DEGREE - 1)

        return AltBn128Fq12Point(
            Fq12(newX),
            Fq12(newY)
        )
    }

    private fun millerLoop(p: AltBn128Fq12Point, q: AltBn128Fq12Point): Fq12 {
        if (p.isInfinity || q.isInfinity) {
            return Fq12.one()
        }
        var r = q
        var f = Fq12.one()
        for (i in LOG_ATE_LOOP_COUNT downTo 0) {
            f = (f * f) * lineFunc(r, r, p)
            r = r.doub()
            if (ATE_LOOP_COUNT.testBit(i)) {
                f = f * lineFunc(r, q, p)
                r = r + q
            }
        }
        val q1 = AltBn128Fq12Point(
            q.x.power(FieldElement.FIELD_MODULUS),
            q.y.power(FieldElement.FIELD_MODULUS)
        )
        val nQ2 = AltBn128Fq12Point(
            q1.x.power(FieldElement.FIELD_MODULUS),
            (-q1.y).power(FieldElement.FIELD_MODULUS)
        )
        f = f * lineFunc(r, q1, p)
        r = r + q1
        f = f * lineFunc(r, nQ2, p)
        return f
    }

    fun finalize(f: Fq12): Fq12 = f.power(
        FieldElement.FIELD_MODULUS.pow(12).subtract(
            ONE
        ).divide(
            CURVE_ORDER
        ))

    private fun lineFunc(p1: AltBn128Fq12Point, p2: AltBn128Fq12Point, t: AltBn128Fq12Point): Fq12 = when {
        p1.x != p2.x -> {
            val m = (p2.y - p1.y) / (p2.x - p1.x)
            m * (t.x - p1.x) - (t.y - p1.y)
        }
        p1.y == p2.y -> {
            val m = (p1.x.power(2) * 3) / (p1.y * 2)
            m * (t.x - p1.x) - (t.y - p1.y)
        }
        else -> t.x - p1.x
    }
}