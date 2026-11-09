package com.fractionpie.dojo.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.navigation.Screen
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.Gold
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.ui.PieCanvas
import com.fractionpie.dojo.core.ui.PrimaryButton
import com.fractionpie.dojo.core.ui.SecondaryButton
import com.fractionpie.dojo.core.ui.StatCard
import com.fractionpie.dojo.core.ui.rememberClickFeedback

@Composable
fun GameOverScreen(
    recap: Screen.GameOver,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = "gameover-${recap.beltIndex}-${recap.score}-${recap.misses}",
        factory = AppViewModelFactory(recap = recap),
    )
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    GameOverContent(
        state = state,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu,
        modifier = modifier,
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val feedback = rememberClickFeedback(state.soundOn, state.hapticsOn)
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        entered = true
    }
    val ribbonProgress by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(GameConfig.RIBBON_MS, easing = FastOutSlowInEasing),
        label = "ribbon",
    )
    val shownScore by animateIntAsState(
        targetValue = if (entered) state.score else 0,
        animationSpec = tween(320),
        label = "score",
    )

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
                            Color(0xE6F3D9B8),
                            Color(0xF2FFF6E9),
                            Color(0xE6E8DDC4),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (state.promoted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(34.dp)
                            .graphicsLayer {
                                translationX = (ribbonProgress - 0.5f) * 900f
                                rotationZ = -12f
                            },
                    )
                }
                Text(
                    text = stringResource(R.string.new_belt),
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = stringResource(state.headlineRes),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(state.beltNameRes),
                style = MaterialTheme.typography.labelLarge,
                color = Color(state.beltColor),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.score_value, shownScore),
                style = MaterialTheme.typography.headlineLarge,
                color = Primary,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )

            Spacer(modifier = Modifier.height(14.dp))

            RecapStats(state = state)

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(state.results) { index, result ->
                    RecapPie(
                        slices = result.slices,
                        filled = result.filled,
                        cleared = result.cleared,
                        index = index,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.hint_recap),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = {
                    feedback()
                    onPlayAgain()
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = {
                    feedback()
                    onMenu()
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RecapStats(state: GameOverUiState) {
    val showClean = state.clean > 0
    val showMisses = state.misses > 0
    val showStreak = state.bestStreak > 0
    val visible = (if (showClean) 1 else 0) +
        (if (showMisses) 1 else 0) +
        (if (showStreak) 1 else 0)

    if (visible >= 2) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (showClean) {
                StatCard(
                    label = stringResource(R.string.stat_clean),
                    value = stringResource(
                        R.string.progress_value,
                        state.clean,
                        state.boutsTarget,
                    ),
                    accent = Accent,
                    modifier = Modifier.weight(1f),
                )
            }
            if (showMisses) {
                StatCard(
                    label = stringResource(R.string.stat_misses),
                    value = state.misses.toString(),
                    accent = Primary,
                    modifier = Modifier.weight(1f),
                )
            }
            if (showStreak) {
                StatCard(
                    label = stringResource(R.string.stat_streak_short),
                    value = state.bestStreak.toString(),
                    accent = Gold,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Background,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSand, RoundedCornerShape(12.dp)),
        ) {
            Text(
                text = stringResource(R.string.no_misses),
                style = MaterialTheme.typography.labelLarge,
                color = Accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun RecapPie(
    slices: Int,
    filled: Int,
    cleared: Boolean,
    index: Int,
) {
    var replay by remember(index) { mutableStateOf(false) }
    val spin by animateFloatAsState(
        targetValue = if (replay) 90f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "recapSpin",
    )
    val ring = if (cleared) Accent else Primary

    Box(
        modifier = Modifier
            .size(68.dp)
            .border(2.dp, ring, CircleShape)
            .clickable { replay = !replay },
        contentAlignment = Alignment.Center,
    ) {
        PieCanvas(
            slices = slices,
            filled = (0 until filled).toSet(),
            diameter = 60.dp,
            rotationDeg = spin,
            liftDp = 2.dp,
            highlight = ring,
            description = stringResource(R.string.cd_pie, slices, filled),
        )
    }
}
