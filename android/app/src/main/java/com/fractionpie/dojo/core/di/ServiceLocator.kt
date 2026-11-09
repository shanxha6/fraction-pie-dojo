package com.fractionpie.dojo.core.di

import android.content.Context
import com.fractionpie.dojo.data.local.DojoPreferences
import com.fractionpie.dojo.data.repository.BeltRepositoryImpl
import com.fractionpie.dojo.data.repository.OptionsRepositoryImpl
import com.fractionpie.dojo.data.repository.ProgressRepositoryImpl
import com.fractionpie.dojo.domain.repository.BeltRepository
import com.fractionpie.dojo.domain.repository.OptionsRepository
import com.fractionpie.dojo.domain.repository.ProgressRepository
import com.fractionpie.dojo.domain.usecase.AwardBeltUseCase
import com.fractionpie.dojo.domain.usecase.CheckAnswerUseCase
import com.fractionpie.dojo.domain.usecase.GenerateBoutUseCase

object ServiceLocator {

    private var progress: ProgressRepository? = null
    private var options: OptionsRepository? = null
    private var belts: BeltRepository? = null
    private var award: AwardBeltUseCase? = null

    fun init(context: Context) {
        if (progress != null) return
        val preferences = DojoPreferences(context.applicationContext)
        val progressRepository = ProgressRepositoryImpl(preferences)
        progress = progressRepository
        options = OptionsRepositoryImpl(preferences)
        belts = BeltRepositoryImpl()
        award = AwardBeltUseCase(progressRepository)
    }

    fun progressRepository(): ProgressRepository = checkNotNull(progress)

    fun optionsRepository(): OptionsRepository = checkNotNull(options)

    fun beltRepository(): BeltRepository = checkNotNull(belts)

    fun awardBeltUseCase(): AwardBeltUseCase = checkNotNull(award)

    fun generateBoutUseCase(): GenerateBoutUseCase = GenerateBoutUseCase()

    fun checkAnswerUseCase(): CheckAnswerUseCase = CheckAnswerUseCase()
}
