package com.fractionpie.dojo.domain.usecase

import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.domain.model.Belt
import com.fractionpie.dojo.domain.model.Bout
import com.fractionpie.dojo.domain.model.BoutType
import com.fractionpie.dojo.domain.model.Fraction
import kotlin.random.Random

class GenerateBoutUseCase {

    operator fun invoke(belt: Belt, boutIndex: Int, random: Random): Bout {
        val type = if (belt.mixed) MIXED_ORDER[boutIndex % MIXED_ORDER.size] else belt.boutType
        return when (type) {
            BoutType.SLICE -> sliceBout(belt, random)
            BoutType.MATCH -> matchBout(belt, random)
            BoutType.COMPARE -> compareBout(belt, random)
        }
    }

    private fun sliceBout(belt: Belt, random: Random): Bout {
        if (belt.sumTarget && belt.unlikeDenominators) return unlikeSumBout(random)
        if (belt.sumTarget) return likeSumBout(belt, random)
        val denominator = belt.denominators.random(random).coerceAtLeast(2)
        val numerator = random.nextInt(1, denominator)
        val target = Fraction(numerator, denominator)
        return Bout(
            type = BoutType.SLICE,
            target = target,
            allowedSlices = multiplesOf(target.reduced().denominator),
            canonicalSlices = denominator,
            canonicalFilled = numerator,
        )
    }

    private fun likeSumBout(belt: Belt, random: Random): Bout {
        val pool = belt.denominators.filter { it >= 4 }
        val denominator = if (pool.isEmpty()) belt.denominators.random(random) else pool.random(random)
        val first = random.nextInt(1, denominator - 1)
        val second = random.nextInt(1, denominator - first)
        val addendA = Fraction(first, denominator)
        val addendB = Fraction(second, denominator)
        return Bout(
            type = BoutType.SLICE,
            target = addendA + addendB,
            addendA = addendA,
            addendB = addendB,
            allowedSlices = multiplesOf(denominator),
            canonicalSlices = denominator,
            canonicalFilled = first + second,
        )
    }

    private fun unlikeSumBout(random: Random): Bout {
        val pair = UNLIKE_PAIRS.random(random)
        val addendA = pair.first
        val addendB = pair.second
        val common = addendA.commonDenominator(addendB)
        val filled = addendA.numerator * (common / addendA.denominator) +
            addendB.numerator * (common / addendB.denominator)
        return Bout(
            type = BoutType.SLICE,
            target = addendA + addendB,
            addendA = addendA,
            addendB = addendB,
            allowedSlices = multiplesOf(common),
            canonicalSlices = common,
            canonicalFilled = filled,
        )
    }

    private fun matchBout(belt: Belt, random: Random): Bout {
        val base = belt.denominators.first().coerceAtLeast(2)
        val numerator = random.nextInt(1, base)
        val target = Fraction(numerator, base)
        val scaled = belt.denominators.filter { it != base && it % base == 0 && it <= GameConfig.MAX_SLICES }
        val factor = if (scaled.isEmpty()) 2 else scaled.random(random) / base
        val correct = Fraction(numerator * factor, base * factor)
        val distractors = mutableListOf<Fraction>()
        var guard = 0
        while (distractors.size < 2 && guard < 80) {
            guard++
            val denominator = random.nextInt(GameConfig.MIN_SLICES, GameConfig.MAX_SLICES + 1)
            val candidate = Fraction(random.nextInt(1, denominator), denominator)
            if (candidate.isEquivalentTo(target)) continue
            if (distractors.any { it.isEquivalentTo(candidate) }) continue
            distractors.add(candidate)
        }
        while (distractors.size < 2) {
            distractors.add(Fraction(distractors.size + 1, GameConfig.MAX_SLICES - 1))
        }
        val options = (distractors + correct).shuffled(random)
        return Bout(
            type = BoutType.MATCH,
            target = target,
            options = options,
            correctOption = options.indexOfFirst { it == correct },
            canonicalSlices = correct.denominator,
            canonicalFilled = correct.numerator,
        )
    }

    private fun compareBout(belt: Belt, random: Random): Bout {
        val denominator = belt.denominators.random(random).coerceAtLeast(2)
        val like = !belt.unlikeDenominators
        var left = Fraction(random.nextInt(1, denominator), denominator)
        var right = rival(belt, denominator, like, random)
        var guard = 0
        while (left.isEquivalentTo(right) && guard < 40) {
            guard++
            right = rival(belt, denominator, like, random)
        }
        if (left.isEquivalentTo(right)) {
            left = Fraction(1, 2)
            right = Fraction(1, 3)
        }
        return Bout(
            type = BoutType.COMPARE,
            target = if (left.isLargerThan(right)) left else right,
            left = left,
            right = right,
            leftIsLarger = left.isLargerThan(right),
            canonicalSlices = left.denominator,
            canonicalFilled = left.numerator,
        )
    }

    private fun rival(belt: Belt, denominator: Int, like: Boolean, random: Random): Fraction {
        if (like) return Fraction(random.nextInt(1, denominator), denominator)
        val others = belt.denominators.filter { it != denominator && it >= 2 }
        val other = if (others.isEmpty()) denominator else others.random(random)
        return Fraction(random.nextInt(1, other), other)
    }

    private fun multiplesOf(base: Int): List<Int> {
        if (base < GameConfig.MIN_SLICES) return listOf(GameConfig.MIN_SLICES)
        return (base..GameConfig.MAX_SLICES step base).toList()
    }

    private companion object {
        val MIXED_ORDER = listOf(
            BoutType.SLICE,
            BoutType.MATCH,
            BoutType.COMPARE,
            BoutType.SLICE,
            BoutType.MATCH,
        )
        val UNLIKE_PAIRS = listOf(
            Fraction(1, 2) to Fraction(1, 3),
            Fraction(1, 2) to Fraction(1, 4),
            Fraction(1, 2) to Fraction(1, 6),
            Fraction(1, 3) to Fraction(1, 6),
            Fraction(1, 3) to Fraction(1, 4),
            Fraction(1, 4) to Fraction(1, 6),
            Fraction(2, 3) to Fraction(1, 6),
        )
    }
}
