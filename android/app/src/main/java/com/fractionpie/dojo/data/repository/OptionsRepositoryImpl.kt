package com.fractionpie.dojo.data.repository

import com.fractionpie.dojo.data.local.DojoPreferences
import com.fractionpie.dojo.domain.repository.OptionsRepository

class OptionsRepositoryImpl(private val preferences: DojoPreferences) : OptionsRepository {

    override fun soundOn(): Boolean = preferences.readBoolean(KEY_SOUND, true)

    override fun hapticsOn(): Boolean = preferences.readBoolean(KEY_HAPTICS, true)

    override fun largeNumerals(): Boolean = preferences.readBoolean(KEY_LARGE, false)

    override fun setSoundOn(value: Boolean) = preferences.writeBoolean(KEY_SOUND, value)

    override fun setHapticsOn(value: Boolean) = preferences.writeBoolean(KEY_HAPTICS, value)

    override fun setLargeNumerals(value: Boolean) = preferences.writeBoolean(KEY_LARGE, value)

    private companion object {
        const val KEY_SOUND = "sound_on"
        const val KEY_HAPTICS = "haptics_on"
        const val KEY_LARGE = "large_numerals"
    }
}
