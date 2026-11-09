package com.fractionpie.dojo.presentation.beltpath

data class BeltRowUi(
    val index: Int,
    val nameRes: Int,
    val skillRes: Int,
    val color: Long,
    val clean: Int,
    val target: Int,
    val unlocked: Boolean,
    val mastered: Boolean,
)

data class BeltPathUiState(
    val rows: List<BeltRowUi>,
)
