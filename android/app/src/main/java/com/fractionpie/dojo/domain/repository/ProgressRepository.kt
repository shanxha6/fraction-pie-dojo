package com.fractionpie.dojo.domain.repository

interface ProgressRepository {
    fun beltIndex(): Int
    fun cleanCounts(): List<Int>
    fun bestStreak(): Int
    fun boutsToday(): Int
    fun saveBeltIndex(value: Int)
    fun saveCleanCount(beltIndex: Int, value: Int)
    fun saveBestStreak(value: Int)
    fun addBoutsToday(count: Int)
    fun reset()
}
