package com.fractionpie.dojo.presentation.app

import androidx.lifecycle.ViewModel
import com.fractionpie.dojo.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()
    private var gameAttempt = 0

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.add(current)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun navigateReplacing(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun back() {
        if (backStack.isEmpty()) return
        val previous = backStack.removeAt(backStack.size - 1)
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun startGame(beltIndex: Int) {
        gameAttempt++
        navigate(Screen.Game(beltIndex, gameAttempt))
    }

    fun replayGame(beltIndex: Int) {
        gameAttempt++
        navigateReplacing(Screen.Game(beltIndex, gameAttempt))
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
