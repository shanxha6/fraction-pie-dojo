package com.fractionpie.dojo.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.ui.FractionGlyph
import com.fractionpie.dojo.core.ui.PieCanvas

@Composable
fun MatchBoard(
    state: GameUiState,
    onPickOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val targetSize = minOf(maxWidth * 0.38f, maxHeight * 0.38f, 150.dp)
        val discSize = minOf((maxWidth - 80.dp) / 3f, maxHeight * 0.26f, 96.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(targetSize),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.prompt_match),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                state.options.forEachIndexed { index, fraction ->
                    MatchDisc(
                        numerator = fraction.numerator,
                        denominator = fraction.denominator,
                        diameter = discSize,
                        selected = state.selectedOption == index,
                        enabled = !state.inputLocked,
                        onPick = { onPickOption(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchDisc(
    numerator: Int,
    denominator: Int,
    diameter: Dp,
    selected: Boolean,
    enabled: Boolean,
    onPick: () -> Unit,
) {
    val lift by animateDpAsState(
        targetValue = if (selected) (-14).dp else 0.dp,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "discLift",
    )
    val selectedLabel = stringResource(R.string.state_selected)
    val emptyLabel = stringResource(R.string.state_empty)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(y = lift)
            .semantics { stateDescription = if (selected) selectedLabel else emptyLabel },
    ) {
        Box(
            modifier = if (enabled) Modifier.clickable { onPick() } else Modifier,
            contentAlignment = Alignment.Center,
        ) {
            PieCanvas(
                slices = denominator,
                filled = (0 until numerator).toSet(),
                diameter = diameter,
                highlight = if (selected) Accent else Accent.copy(alpha = 0.7f),
                description = stringResource(R.string.cd_pie, denominator, numerator),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        FractionGlyph(
            numerator = numerator,
            denominator = denominator,
            compact = true,
        )
    }
}
