package com.fractionpie.dojo.core.ui

import android.view.SoundEffectConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView

@Composable
fun rememberClickFeedback(soundEnabled: Boolean, hapticsEnabled: Boolean): () -> Unit {
    val view = LocalView.current
    val haptics = LocalHapticFeedback.current
    return remember(soundEnabled, hapticsEnabled, view, haptics) {
        {
            if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
            if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}
