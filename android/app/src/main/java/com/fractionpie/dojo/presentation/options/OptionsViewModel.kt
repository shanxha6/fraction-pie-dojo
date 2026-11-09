package com.fractionpie.dojo.presentation.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fractionpie.dojo.domain.repository.OptionsRepository
import com.fractionpie.dojo.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OptionsUiState(
    val soundOn: Boolean,
    val hapticsOn: Boolean,
    val largeNumerals: Boolean,
    val resetRequested: Boolean,
    val resetDone: Boolean,
)

class OptionsViewModel(
    private val optionsRepository: OptionsRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        OptionsUiState(
            soundOn = optionsRepository.soundOn(),
            hapticsOn = optionsRepository.hapticsOn(),
            largeNumerals = optionsRepository.largeNumerals(),
            resetRequested = false,
            resetDone = false,
        ),
    )
    val state: StateFlow<OptionsUiState> = _state.asStateFlow()

    fun setSound(value: Boolean) {
        _state.update { it.copy(soundOn = value) }
        viewModelScope.launch { optionsRepository.setSoundOn(value) }
    }

    fun setHaptics(value: Boolean) {
        _state.update { it.copy(hapticsOn = value) }
        viewModelScope.launch { optionsRepository.setHapticsOn(value) }
    }

    fun setLargeNumerals(value: Boolean) {
        _state.update { it.copy(largeNumerals = value) }
        viewModelScope.launch { optionsRepository.setLargeNumerals(value) }
    }

    fun requestReset() {
        _state.update { it.copy(resetRequested = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(resetRequested = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            progressRepository.reset()
            _state.update {
                it.copy(
                    resetRequested = false,
                    resetDone = true,
                    soundOn = optionsRepository.soundOn(),
                    hapticsOn = optionsRepository.hapticsOn(),
                    largeNumerals = optionsRepository.largeNumerals(),
                )
            }
        }
    }
}
