package com.fractionpie.dojo.domain.repository

interface OptionsRepository {
    fun soundOn(): Boolean
    fun hapticsOn(): Boolean
    fun largeNumerals(): Boolean
    fun setSoundOn(value: Boolean)
    fun setHapticsOn(value: Boolean)
    fun setLargeNumerals(value: Boolean)
}
