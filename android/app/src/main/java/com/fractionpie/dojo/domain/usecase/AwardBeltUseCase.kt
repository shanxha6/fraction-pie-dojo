package com.fractionpie.dojo.domain.usecase

import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.domain.model.BoutResult
import com.fractionpie.dojo.domain.model.SessionResult
import com.fractionpie.dojo.domain.repository.ProgressRepository

class AwardBeltUseCase(private val repository: ProgressRepository) {

    operator fun invoke(
        beltIndex: Int,
        score: Int,
        clean: Int,
        misses: Int,
        longestStreak: Int,
        results: List<BoutResult>,
    ): SessionResult {
        val previous = repository.cleanCounts().getOrElse(beltIndex) { 0 }
        val earned = (previous + clean).coerceAtMost(GameConfig.BOUTS_PER_SESSION)
        repository.saveCleanCount(beltIndex, earned)

        val mastered = earned >= GameConfig.BOUTS_PER_SESSION
        val promoted = mastered &&
            beltIndex == repository.beltIndex() &&
            beltIndex < GameConfig.BELT_COUNT - 1
        if (promoted) repository.saveBeltIndex(beltIndex + 1)

        val best = maxOf(repository.bestStreak(), longestStreak)
        repository.saveBestStreak(best)
        repository.addBoutsToday(GameConfig.BOUTS_PER_SESSION)

        return SessionResult(
            beltIndex = beltIndex,
            score = score,
            clean = clean,
            misses = misses,
            bestStreak = best,
            promoted = promoted,
            results = results,
        )
    }
}
