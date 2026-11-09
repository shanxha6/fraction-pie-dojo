package com.fractionpie.dojo.data.repository

import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.data.local.DojoPreferences
import com.fractionpie.dojo.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val preferences: DojoPreferences) : ProgressRepository {

    override fun beltIndex(): Int =
        preferences.readInt(KEY_BELT_INDEX, 0).coerceIn(0, GameConfig.BELT_COUNT - 1)

    override fun cleanCounts(): List<Int> {
        val raw = preferences.readString(KEY_CLEAN_COUNTS, "")
        if (raw.isEmpty()) return List(GameConfig.BELT_COUNT) { 0 }
        val parsed = raw.split(",").map { it.trim().toIntOrNull() ?: 0 }
        return List(GameConfig.BELT_COUNT) { parsed.getOrElse(it) { 0 } }
    }

    override fun bestStreak(): Int = preferences.readInt(KEY_BEST_STREAK, 0)

    override fun boutsToday(): Int =
        if (preferences.readInt(KEY_DAY_STAMP, -1) == currentDay()) {
            preferences.readInt(KEY_BOUTS_TODAY, 0)
        } else {
            0
        }

    override fun saveBeltIndex(value: Int) {
        preferences.writeInt(KEY_BELT_INDEX, value.coerceIn(0, GameConfig.BELT_COUNT - 1))
    }

    override fun saveCleanCount(beltIndex: Int, value: Int) {
        if (beltIndex !in 0 until GameConfig.BELT_COUNT) return
        val counts = cleanCounts().toMutableList()
        counts[beltIndex] = value.coerceIn(0, GameConfig.BOUTS_PER_SESSION)
        preferences.writeString(KEY_CLEAN_COUNTS, counts.joinToString(","))
    }

    override fun saveBestStreak(value: Int) {
        preferences.writeInt(KEY_BEST_STREAK, value.coerceAtLeast(0))
    }

    override fun addBoutsToday(count: Int) {
        val today = currentDay()
        val current = if (preferences.readInt(KEY_DAY_STAMP, -1) == today) {
            preferences.readInt(KEY_BOUTS_TODAY, 0)
        } else {
            0
        }
        preferences.writeInt(KEY_DAY_STAMP, today)
        preferences.writeInt(KEY_BOUTS_TODAY, current + count)
    }

    override fun reset() {
        preferences.clear()
    }

    private fun currentDay(): Int = (System.currentTimeMillis() / MILLIS_PER_DAY).toInt()

    private companion object {
        const val KEY_BELT_INDEX = "belt_index"
        const val KEY_CLEAN_COUNTS = "belt_clean_counts"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_BOUTS_TODAY = "bouts_today"
        const val KEY_DAY_STAMP = "bouts_day_stamp"
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
