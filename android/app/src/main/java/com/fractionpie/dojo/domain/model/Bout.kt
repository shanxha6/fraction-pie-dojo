package com.fractionpie.dojo.domain.model

data class Bout(
    val type: BoutType,
    val target: Fraction,
    val addendA: Fraction? = null,
    val addendB: Fraction? = null,
    val allowedSlices: List<Int> = emptyList(),
    val canonicalSlices: Int = 2,
    val canonicalFilled: Int = 1,
    val options: List<Fraction> = emptyList(),
    val correctOption: Int = -1,
    val left: Fraction = Fraction(1, 2),
    val right: Fraction = Fraction(1, 3),
    val leftIsLarger: Boolean = true,
)
