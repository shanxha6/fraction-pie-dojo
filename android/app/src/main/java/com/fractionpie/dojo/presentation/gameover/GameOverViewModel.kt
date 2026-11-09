package com.fractionpie.dojo.presentation.gameover

import androidx.lifecycle.ViewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.core.navigation.Screen
import com.fractionpie.dojo.domain.model.BoutResult
import com.fractionpie.dojo.domain.repository.BeltRepository
import com.fractionpie.dojo.domain.repository.OptionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameOverUiState(
    val headlineRes: Int,
    val beltNameRes: Int,
    val beltColor: Long,
    val score: Int,
    val clean: Int,
    val misses: Int,
    val bestStreak: Int,
    val boutsTarget: Int,
    val promoted: Boolean,
    val results: List<BoutResult>,
    val soundOn: Boolean,
    val hapticsOn: Boolean,
)

class GameOverViewModel(
    recap: Screen.GameOver,
    beltRepository: BeltRepository,
    optionsRepository: OptionsRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<GameOverUiState>
    val state: StateFlow<GameOverUiState>

    init {
        val belts = beltRepository.belts()
        val index = recap.beltIndex.coerceIn(0, belts.size - 1)
        val belt = belts[index]
        val lastBelt = index == belts.size - 1
        val headlineRes = when {
            recap.clean >= GameConfig.BOUTS_PER_SESSION && lastBelt -> R.string.headline_master
            recap.clean >= GameConfig.BOUTS_PER_SESSION -> R.string.headline_clean
            recap.clean >= 3 -> R.string.headline_good
            else -> R.string.headline_keep
        }
        val initial = GameOverUiState(
            headlineRes = headlineRes,
            beltNameRes = belt.nameRes,
            beltColor = belt.color,
            score = recap.score,
            clean = recap.clean,
            misses = recap.misses,
            bestStreak = recap.bestStreak,
            boutsTarget = GameConfig.BOUTS_PER_SESSION,
            promoted = recap.promoted,
            results = recap.results,
            soundOn = optionsRepository.soundOn(),
            hapticsOn = optionsRepository.hapticsOn(),
        )
        _state = MutableStateFlow(initial)
        state = _state.asStateFlow()
    }
}
