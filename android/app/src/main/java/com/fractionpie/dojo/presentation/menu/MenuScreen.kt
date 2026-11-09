package com.fractionpie.dojo.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.Gold
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.SurfaceParchment
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.theme.TextSecondary
import com.fractionpie.dojo.core.ui.PrimaryButton
import com.fractionpie.dojo.core.ui.SecondaryButton
import com.fractionpie.dojo.core.ui.StatCard
import com.fractionpie.dojo.core.ui.rememberClickFeedback

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onBeltPath: () -> Unit,
    onScroll: () -> Unit,
    onOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = { onPlay(state.beltIndex) },
        onBeltPath = onBeltPath,
        onScroll = onScroll,
        onOptions = onOptions,
        modifier = modifier,
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onBeltPath: () -> Unit,
    onScroll: () -> Unit,
    onOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val feedback = rememberClickFeedback(state.soundOn, state.hapticsOn)
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        entered = true
    }
    val sheetOffset by animateDpAsState(
        targetValue = if (entered) 0.dp else 64.dp,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "sheet",
    )
    val heroScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.9f,
        animationSpec = tween(380, easing = FastOutSlowInEasing),
        label = "heroScale",
    )
    val heroAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(380, easing = FastOutSlowInEasing),
        label = "heroAlpha",
    )
    val tiltTransition = rememberInfiniteTransition(label = "menuTilt")
    val tilt by tiltTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tilt",
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
                            Color(0x00FFF6E9),
                            Color(0x66FFF6E9),
                            Color(0xF2FFF6E9),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceParchment.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, BorderSand),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(44.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.icon_home),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceParchment,
                        border = BorderStroke(2.dp, Color(state.beltColor)),
                        shadowElevation = 4.dp,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.belt_badge,
                                stringResource(state.beltNameRes),
                                state.beltIndex + 1,
                                state.beltCount,
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = stringResource(R.string.cd_hero_pie),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(200.dp)
                            .graphicsLayer {
                                rotationZ = tilt
                                scaleX = heroScale
                                scaleY = heroScale
                                alpha = heroAlpha
                            },
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(18.dp)
                            .graphicsLayer { alpha = 0.5f * heroAlpha }
                            .background(Color(0x332E1F17), CircleShape),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = SurfaceParchment,
                border = BorderStroke(1.dp, BorderSand),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = sheetOffset),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(5.dp)
                            .background(BorderSand, RoundedCornerShape(3.dp)),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    MenuStats(state = state)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.hint_belt),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = stringResource(R.string.play),
                        onClick = {
                            feedback()
                            onPlay()
                        },
                        leadingIcon = ImageVector.vectorResource(R.drawable.ic_play),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.belt_path),
                            onClick = {
                                feedback()
                                onBeltPath()
                            },
                            height = 48.dp,
                            modifier = Modifier.weight(1f),
                        )
                        SecondaryButton(
                            text = stringResource(R.string.scroll_short),
                            onClick = {
                                feedback()
                                onScroll()
                            },
                            height = 48.dp,
                            modifier = Modifier.weight(1f),
                        )
                        SecondaryButton(
                            text = stringResource(R.string.options_short),
                            onClick = {
                                feedback()
                                onOptions()
                            },
                            height = 48.dp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuStats(state: MenuUiState) {
    val showBouts = state.boutsToday > 0
    val showStreak = state.bestStreak > 0
    if (showBouts && showStreak) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatCard(
                label = stringResource(R.string.stat_bouts),
                value = stringResource(
                    R.string.progress_value,
                    state.boutsToday,
                    state.boutsTarget,
                ),
                accent = Accent,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.stat_streak),
                value = state.bestStreak.toString(),
                accent = Gold,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Background,
            border = BorderStroke(1.dp, BorderSand),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.first_bout),
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
        }
    }
}
