package com.fractionpie.dojo.presentation.menu

data class MenuUiState(
    val beltIndex: Int,
    val beltNameRes: Int,
    val beltColor: Long,
    val beltCount: Int,
    val boutsToday: Int,
    val boutsTarget: Int,
    val bestStreak: Int,
    val soundOn: Boolean,
    val hapticsOn: Boolean,
)
