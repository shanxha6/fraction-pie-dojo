package com.fractionpie.dojo.presentation.beltpath

import androidx.lifecycle.ViewModel
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.domain.repository.BeltRepository
import com.fractionpie.dojo.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BeltPathViewModel(
    private val progressRepository: ProgressRepository,
    private val beltRepository: BeltRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(read())
    val state: StateFlow<BeltPathUiState> = _state.asStateFlow()

    fun refresh() {
        _state.value = read()
    }

    private fun read(): BeltPathUiState {
        val unlockedUpTo = progressRepository.beltIndex()
        val counts = progressRepository.cleanCounts()
        val rows = beltRepository.belts().map { belt ->
            val clean = counts.getOrElse(belt.index) { 0 }
            BeltRowUi(
                index = belt.index,
                nameRes = belt.nameRes,
                skillRes = belt.skillRes,
                color = belt.color,
                clean = clean,
                target = GameConfig.BOUTS_PER_SESSION,
                unlocked = belt.index <= unlockedUpTo,
                mastered = clean >= GameConfig.BOUTS_PER_SESSION,
            )
        }
        return BeltPathUiState(rows = rows)
    }
}
