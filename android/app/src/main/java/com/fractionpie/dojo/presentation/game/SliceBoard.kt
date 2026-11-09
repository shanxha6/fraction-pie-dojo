package com.fractionpie.dojo.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.core.ui.PieCanvas

@Composable
fun SliceBoard(
    state: GameUiState,
    onWedgeTap: (Int) -> Unit,
    onBladeDrag: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spin by animateFloatAsState(
        targetValue = if (state.phase == Phase.CORRECT) 90f else 0f,
        animationSpec = tween(GameConfig.CORRECT_SPIN_MS, easing = FastOutSlowInEasing),
        label = "spin",
    )
    val bladeAngle by animateFloatAsState(
        targetValue = state.bladeAngle,
        animationSpec = tween(90, easing = LinearOutSlowInEasing),
        label = "blade",
    )
    val description = stringResource(R.string.cd_pie, state.sliceCount, state.filled.size)
    val filledLabel = stringResource(R.string.state_filled)
    val emptyLabel = stringResource(R.string.state_empty)

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val diameter = minOf(
            maxWidth * GameConfig.PIE_WIDTH_FRACTION,
            maxHeight * 0.82f,
            GameConfig.PIE_MAX_DP.dp,
        )
        Box(
            modifier = Modifier
                .size(diameter + 32.dp)
                .pointerInput(state.boutIndex) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        onBladeDrag(dragAmount)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            PieCanvas(
                slices = state.sliceCount,
                filled = state.filled,
                diameter = diameter,
                rotationDeg = spin,
                description = description,
                onWedgeTap = onWedgeTap,
                modifier = Modifier.semantics {
                    stateDescription = if (state.filled.isEmpty()) emptyLabel else filledLabel
                },
            )
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        rotationZ = bladeAngle
                        alpha = 0.9f
                    },
            )
        }
    }
}
