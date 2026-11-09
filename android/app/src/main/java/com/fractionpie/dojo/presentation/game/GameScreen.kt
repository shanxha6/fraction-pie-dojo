package com.fractionpie.dojo.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.SurfaceParchment
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.ui.FractionGlyph
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.PrimaryButton
import com.fractionpie.dojo.domain.model.BoutType
import com.fractionpie.dojo.domain.model.SessionResult
import com.fractionpie.dojo.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    beltIndex: Int,
    attempt: Int,
    onFinished: (SessionResult) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game-$beltIndex-$attempt",
        factory = AppViewModelFactory(beltIndex = beltIndex),
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { result -> onFinished(result) }
    }

    DisposableEffect(Unit) {
        gameViewModel.attach()
        onDispose { gameViewModel.pause() }
    }

    BackHandler { gameViewModel.requestPause() }

    LaunchedEffect(state.phase, state.boutIndex) {
        if (state.hapticsOn && (state.phase == Phase.CORRECT || state.phase == Phase.WRONG)) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    GameContent(
        state = state,
        onWedgeTap = gameViewModel::onWedgeTap,
        onBladeDrag = gameViewModel::onBladeDrag,
        onSliceDelta = gameViewModel::onSliceDelta,
        onPickOption = gameViewModel::onPickOption,
        onPickSide = gameViewModel::onPickSide,
        onServe = gameViewModel::onServe,
        onPause = gameViewModel::requestPause,
        modifier = modifier,
    )

    if (state.paused) {
        PauseDialog(
            onResume = gameViewModel::resume,
            onMenu = onMenu,
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onWedgeTap: (Int) -> Unit,
    onBladeDrag: (Float) -> Unit,
    onSliceDelta: (Int) -> Unit,
    onPickOption: (Int) -> Unit,
    onPickSide: (Int) -> Unit,
    onServe: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xCCFFF6E9),
                            Color(0x99F3D9B8),
                            Color(0xE6FFF6E9),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            GameHeader(state = state, onPause = onPause)

            TargetBanner(state = state)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when (state.boutType) {
                    BoutType.SLICE -> SliceBoard(
                        state = state,
                        onWedgeTap = onWedgeTap,
                        onBladeDrag = onBladeDrag,
                        modifier = Modifier.fillMaxSize(),
                    )

                    BoutType.MATCH -> MatchBoard(
                        state = state,
                        onPickOption = onPickOption,
                        modifier = Modifier.fillMaxSize(),
                    )

                    BoutType.COMPARE -> CompareBoard(
                        state = state,
                        onPickSide = onPickSide,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                FeedbackOverlay(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            ControlBar(
                state = state,
                onSliceDelta = onSliceDelta,
                onServe = onServe,
            )
        }
    }
}

@Composable
private fun GameHeader(
    state: GameUiState,
    onPause: () -> Unit,
) {
    Surface(
        color = SurfaceParchment.copy(alpha = 0.92f),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onPause() },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_pause),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        R.string.bout_counter,
                        state.boutIndex + 1,
                        state.boutCount,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (index in 0 until state.boutCount) {
                        val result = state.results.getOrNull(index)
                        val pipColor = when {
                            result == null -> BorderSand
                            result.cleared -> Accent
                            else -> Primary
                        }
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(6.dp)
                                .background(pipColor, RoundedCornerShape(3.dp)),
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.score_value, state.score),
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .width(56.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

@Composable
private fun TargetBanner(state: GameUiState) {
    val promptRes = when (state.boutType) {
        BoutType.SLICE -> R.string.prompt_fill
        BoutType.MATCH -> R.string.prompt_match
        BoutType.COMPARE -> R.string.prompt_compare
    }

    ParchmentCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        borderColor = BorderSand,
        containerColor = Background,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(promptRes),
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            val addendA = state.addendA
            val addendB = state.addendB
            if (state.boutType == BoutType.COMPARE) {
                Text(
                    text = stringResource(state.beltNameRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                )
            } else if (addendA != null && addendB != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FractionGlyph(
                        numerator = addendA.numerator,
                        denominator = addendA.denominator,
                        large = state.largeNumerals,
                    )
                    Text(
                        text = stringResource(R.string.plus_sign),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                    )
                    FractionGlyph(
                        numerator = addendB.numerator,
                        denominator = addendB.denominator,
                        large = state.largeNumerals,
                    )
                }
            } else {
                FractionGlyph(
                    numerator = state.target.numerator,
                    denominator = state.target.denominator,
                    large = state.largeNumerals,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ControlBar(
    state: GameUiState,
    onSliceDelta: (Int) -> Unit,
    onServe: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceParchment.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, BorderSand),
        shadowElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 18.dp)
            .height(GameConfig.CONTROL_BAR_HEIGHT_DP.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            when (state.boutType) {
                BoutType.SLICE -> {
                    StepButton(
                        label = stringResource(R.string.minus_sign),
                        enabled = !state.inputLocked && state.sliceCount > GameConfig.MIN_SLICES,
                        onStep = { onSliceDelta(-1) },
                    )
                    Text(
                        text = stringResource(R.string.slices_label, state.sliceCount),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.width(80.dp),
                    )
                    StepButton(
                        label = stringResource(R.string.plus_sign),
                        enabled = !state.inputLocked && state.sliceCount < GameConfig.MAX_SLICES,
                        onStep = { onSliceDelta(1) },
                    )
                    PrimaryButton(
                        text = stringResource(R.string.serve),
                        onClick = onServe,
                        enabled = state.canServe,
                        compact = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                BoutType.MATCH -> {
                    val pendingLabel = stringResource(R.string.state_disabled)
                    Spacer(modifier = Modifier.width(2.dp))
                    PrimaryButton(
                        text = stringResource(R.string.serve),
                        onClick = onServe,
                        enabled = state.canServe,
                        compact = true,
                        modifier = Modifier
                            .weight(1f)
                            .semantics {
                                if (!state.canServe) stateDescription = pendingLabel
                            },
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                BoutType.COMPARE -> {
                    Text(
                        text = stringResource(R.string.prompt_compare),
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StepButton(
    label: String,
    enabled: Boolean,
    onStep: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = Background,
        border = BorderStroke(1.5.dp, BorderSand),
        modifier = Modifier
            .size(48.dp)
            .clickable(enabled = enabled) { onStep() },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = if (enabled) Primary else TextMuted,
            )
        }
    }
}
