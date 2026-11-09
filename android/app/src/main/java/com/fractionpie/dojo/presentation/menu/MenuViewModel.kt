package com.fractionpie.dojo.presentation.menu

import androidx.lifecycle.ViewModel
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.domain.repository.BeltRepository
import com.fractionpie.dojo.domain.repository.OptionsRepository
import com.fractionpie.dojo.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val beltRepository: BeltRepository,
    private val optionsRepository: OptionsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(read())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    fun refresh() {
        _state.value = read()
    }

    private fun read(): MenuUiState {
        val belts = beltRepository.belts()
        val index = progressRepository.beltIndex().coerceIn(0, belts.size - 1)
        val belt = belts[index]
        return MenuUiState(
            beltIndex = index,
            beltNameRes = belt.nameRes,
            beltColor = belt.color,
            beltCount = belts.size,
            boutsToday = progressRepository.boutsToday(),
            boutsTarget = GameConfig.BOUTS_PER_SESSION,
            bestStreak = progressRepository.bestStreak(),
            soundOn = optionsRepository.soundOn(),
            hapticsOn = optionsRepository.hapticsOn(),
        )
    }
}
