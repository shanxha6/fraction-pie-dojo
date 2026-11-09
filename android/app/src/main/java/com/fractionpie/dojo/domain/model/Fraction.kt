package com.fractionpie.dojo.domain.model

import kotlin.math.abs

data class Fraction(val numerator: Int, val denominator: Int) {

    val value: Float
        get() = if (denominator == 0) 0f else numerator.toFloat() / denominator.toFloat()

    fun reduced(): Fraction {
        if (denominator == 0) return Fraction(0, 1)
        val divisor = greatestCommonDivisor(abs(numerator), abs(denominator)).coerceAtLeast(1)
        return Fraction(numerator / divisor, denominator / divisor)
    }

    operator fun plus(other: Fraction): Fraction = Fraction(
        numerator * other.denominator + other.numerator * denominator,
        denominator * other.denominator,
    ).reduced()

    fun commonDenominator(other: Fraction): Int =
        leastCommonMultiple(denominator, other.denominator)

    fun isLargerThan(other: Fraction): Boolean =
        numerator * other.denominator > other.numerator * denominator

    fun isEquivalentTo(other: Fraction): Boolean =
        numerator * other.denominator == other.numerator * denominator
}

fun greatestCommonDivisor(first: Int, second: Int): Int {
    var a = first
    var b = second
    while (b != 0) {
        val temp = b
        b = a % b
        a = temp
    }
    return abs(a)
}

fun leastCommonMultiple(first: Int, second: Int): Int {
    if (first == 0 || second == 0) return 1
    return abs(first * second) / greatestCommonDivisor(first, second).coerceAtLeast(1)
}
