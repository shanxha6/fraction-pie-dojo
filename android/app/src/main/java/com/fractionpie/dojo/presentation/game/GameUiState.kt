package com.fractionpie.dojo.presentation.game

import com.fractionpie.dojo.domain.model.BoutResult
import com.fractionpie.dojo.domain.model.BoutType
import com.fractionpie.dojo.domain.model.Fraction

enum class Phase {
    READY,
    SLICING,
    CHECKING,
    CORRECT,
    WRONG,
    FINISHED,
}

data class GameUiState(
    val phase: Phase,
    val beltIndex: Int,
    val beltNameRes: Int,
    val beltColor: Long,
    val boutType: BoutType,
    val boutIndex: Int,
    val boutCount: Int,
    val target: Fraction,
    val addendA: Fraction?,
    val addendB: Fraction?,
    val sliceCount: Int,
    val filled: Set<Int>,
    val bladeAngle: Float,
    val options: List<Fraction>,
    val selectedOption: Int,
    val left: Fraction,
    val right: Fraction,
    val pickedSide: Int,
    val leftCorrect: Boolean,
    val score: Int,
    val streak: Int,
    val misses: Int,
    val clean: Int,
    val results: List<BoutResult>,
    val revealSlices: Int,
    val revealFilled: Int,
    val largeNumerals: Boolean,
    val hapticsOn: Boolean,
    val soundOn: Boolean,
    val paused: Boolean,
) {
    val canServe: Boolean
        get() = when (boutType) {
            BoutType.SLICE -> !inputLocked
            BoutType.MATCH -> !inputLocked && selectedOption >= 0
            BoutType.COMPARE -> false
        }

    val inputLocked: Boolean
        get() = phase == Phase.CHECKING ||
            phase == Phase.CORRECT ||
            phase == Phase.WRONG ||
            phase == Phase.FINISHED ||
            paused
}
