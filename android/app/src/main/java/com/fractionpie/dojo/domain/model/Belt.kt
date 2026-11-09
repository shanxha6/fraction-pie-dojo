package com.fractionpie.dojo.domain.model

data class Belt(
    val index: Int,
    val nameRes: Int,
    val skillRes: Int,
    val color: Long,
    val boutType: BoutType,
    val denominators: List<Int>,
    val sumTarget: Boolean = false,
    val unlikeDenominators: Boolean = false,
    val mixed: Boolean = false,
)
