package com.fractionpie.dojo.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.navigation.Screen
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.presentation.beltpath.BeltPathScreen
import com.fractionpie.dojo.presentation.game.GameScreen
import com.fractionpie.dojo.presentation.gameover.GameOverScreen
import com.fractionpie.dojo.presentation.menu.MenuScreen
import com.fractionpie.dojo.presentation.options.OptionsScreen
import com.fractionpie.dojo.presentation.scroll.ScrollScreen
import com.fractionpie.dojo.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState is Screen.Game) {
                    (slideInHorizontally { full -> full / 4 } + fadeIn(tween(240))) togetherWith
                        fadeOut(tween(180))
                } else {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(200))
                }
            },
            label = "appRoot",
        ) { target ->
            when (target) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.navigate(Screen.Menu) },
                )

                Screen.Menu -> MenuScreen(
                    onPlay = { beltIndex -> appViewModel.startGame(beltIndex) },
                    onBeltPath = { appViewModel.navigate(Screen.BeltPath) },
                    onScroll = { appViewModel.navigate(Screen.Scroll) },
                    onOptions = { appViewModel.navigate(Screen.Options) },
                )

                Screen.BeltPath -> BeltPathScreen(
                    onBelt = { beltIndex -> appViewModel.startGame(beltIndex) },
                    onMenu = { appViewModel.toMenu() },
                )

                is Screen.Game -> GameScreen(
                    beltIndex = target.beltIndex,
                    attempt = target.attempt,
                    onFinished = { result ->
                        appViewModel.navigateReplacing(
                            Screen.GameOver(
                                beltIndex = result.beltIndex,
                                score = result.score,
                                clean = result.clean,
                                misses = result.misses,
                                bestStreak = result.bestStreak,
                                promoted = result.promoted,
                                results = result.results,
                            ),
                        )
                    },
                    onMenu = { appViewModel.toMenu() },
                )

                is Screen.GameOver -> GameOverScreen(
                    recap = target,
                    onPlayAgain = { appViewModel.replayGame(target.beltIndex) },
                    onMenu = { appViewModel.toMenu() },
                )

                Screen.Scroll -> ScrollScreen(
                    onMenu = { appViewModel.toMenu() },
                    onBack = { appViewModel.back() },
                )

                Screen.Options -> OptionsScreen(
                    onMenu = { appViewModel.toMenu() },
                    onBack = { appViewModel.back() },
                )
            }
        }
    }
}
