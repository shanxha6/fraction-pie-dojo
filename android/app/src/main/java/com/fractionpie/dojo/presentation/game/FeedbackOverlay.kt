package com.fractionpie.dojo.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.PieCanvas

@Composable
fun FeedbackOverlay(
    state: GameUiState,
    modifier: Modifier = Modifier,
) {
    val correct = state.phase == Phase.CORRECT
    val wrong = state.phase == Phase.WRONG
    val pulse by animateFloatAsState(
        targetValue = if (correct) 1f else 0f,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "pulse",
    )
    val fan by animateFloatAsState(
        targetValue = if (wrong) 1f else 0f,
        animationSpec = tween(GameConfig.WRONG_FAN_MS, easing = FastOutSlowInEasing),
        label = "fan",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (correct) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .graphicsLayer {
                        val scale = 1f + 0.25f * pulse
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.6f * (1f - pulse)
                    }
                    .border(4.dp, Accent, CircleShape),
            )
        }
        if (wrong) {
            ParchmentCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .graphicsLayer { alpha = 0.35f + 0.65f * fan },
                borderColor = Primary,
                containerColor = Background,
                elevation = 12.dp,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PieCanvas(
                        slices = state.revealSlices,
                        filled = (0 until state.revealFilled).toSet(),
                        diameter = 150.dp,
                        rotationDeg = (1f - fan) * -35f,
                        highlight = Primary,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(
                        R.string.this_is,
                        stringResource(
                            R.string.progress_value,
                            state.revealFilled,
                            state.revealSlices,
                        ),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )
            }
        }
    }
}
