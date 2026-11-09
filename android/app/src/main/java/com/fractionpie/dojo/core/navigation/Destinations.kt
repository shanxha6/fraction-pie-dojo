package com.fractionpie.dojo.core.navigation

import com.fractionpie.dojo.domain.model.BoutResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object BeltPath : Screen
    data class Game(val beltIndex: Int, val attempt: Int) : Screen
    data class GameOver(
        val beltIndex: Int,
        val score: Int,
        val clean: Int,
        val misses: Int,
        val bestStreak: Int,
        val promoted: Boolean,
        val results: List<BoutResult>,
    ) : Screen
    data object Scroll : Screen
    data object Options : Screen
}
