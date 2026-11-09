package com.fractionpie.dojo.domain.model

data class SessionResult(
    val beltIndex: Int,
    val score: Int,
    val clean: Int,
    val misses: Int,
    val bestStreak: Int,
    val promoted: Boolean,
    val results: List<BoutResult>,
)
