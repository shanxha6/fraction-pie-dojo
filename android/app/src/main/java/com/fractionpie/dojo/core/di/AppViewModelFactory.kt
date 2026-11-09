package com.fractionpie.dojo.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fractionpie.dojo.core.navigation.Screen
import com.fractionpie.dojo.presentation.app.AppViewModel
import com.fractionpie.dojo.presentation.beltpath.BeltPathViewModel
import com.fractionpie.dojo.presentation.game.GameViewModel
import com.fractionpie.dojo.presentation.gameover.GameOverViewModel
import com.fractionpie.dojo.presentation.menu.MenuViewModel
import com.fractionpie.dojo.presentation.options.OptionsViewModel
import com.fractionpie.dojo.presentation.scroll.ScrollViewModel
import com.fractionpie.dojo.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val beltIndex: Int = 0,
    private val recap: Screen.GameOver? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.progressRepository(),
                ServiceLocator.beltRepository(),
                ServiceLocator.optionsRepository(),
            )
            modelClass.isAssignableFrom(BeltPathViewModel::class.java) -> BeltPathViewModel(
                ServiceLocator.progressRepository(),
                ServiceLocator.beltRepository(),
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                beltIndex,
                ServiceLocator.beltRepository(),
                ServiceLocator.optionsRepository(),
                ServiceLocator.generateBoutUseCase(),
                ServiceLocator.checkAnswerUseCase(),
                ServiceLocator.awardBeltUseCase(),
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                checkNotNull(recap),
                ServiceLocator.beltRepository(),
                ServiceLocator.optionsRepository(),
            )
            modelClass.isAssignableFrom(ScrollViewModel::class.java) -> ScrollViewModel(
                ServiceLocator.beltRepository(),
            )
            modelClass.isAssignableFrom(OptionsViewModel::class.java) -> OptionsViewModel(
                ServiceLocator.optionsRepository(),
                ServiceLocator.progressRepository(),
            )
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return viewModel as T
    }
}
