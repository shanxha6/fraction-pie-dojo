package com.fractionpie.dojo.presentation.scroll

import androidx.lifecycle.ViewModel
import com.fractionpie.dojo.domain.model.ScrollTerm
import com.fractionpie.dojo.domain.repository.BeltRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScrollUiState(
    val terms: List<ScrollTerm>,
)

class ScrollViewModel(beltRepository: BeltRepository) : ViewModel() {

    private val _state = MutableStateFlow(ScrollUiState(beltRepository.scrollTerms()))
    val state: StateFlow<ScrollUiState> = _state.asStateFlow()
}
