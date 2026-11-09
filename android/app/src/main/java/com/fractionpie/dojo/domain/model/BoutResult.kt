package com.fractionpie.dojo.domain.model

data class BoutResult(
    val target: Fraction,
    val slices: Int,
    val filled: Int,
    val cleared: Boolean,
)
