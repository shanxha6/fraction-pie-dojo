package com.fractionpie.dojo.presentation.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.ui.FractionGlyph
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.PieCanvas

@Composable
fun CompareBoard(
    state: GameUiState,
    onPickSide: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val pieSize = minOf(maxWidth * 0.36f, maxHeight * 0.38f, 160.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ComparePie(
                    numerator = state.left.numerator,
                    denominator = state.left.denominator,
                    diameter = pieSize,
                    picked = state.pickedSide == 0,
                    correct = state.leftCorrect,
                    enabled = !state.inputLocked,
                    large = state.largeNumerals,
                    onPick = { onPickSide(0) },
                    modifier = Modifier.weight(1f),
                )
                ComparePie(
                    numerator = state.right.numerator,
                    denominator = state.right.denominator,
                    diameter = pieSize,
                    picked = state.pickedSide == 1,
                    correct = !state.leftCorrect,
                    enabled = !state.inputLocked,
                    large = state.largeNumerals,
                    onPick = { onPickSide(1) },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.prompt_compare),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun ComparePie(
    numerator: Int,
    denominator: Int,
    diameter: Dp,
    picked: Boolean,
    correct: Boolean,
    enabled: Boolean,
    large: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val border: Color = when {
        picked && correct -> Accent
        picked -> Primary
        else -> BorderSand
    }
    ParchmentCard(
        modifier = modifier.then(
            if (enabled) Modifier.clickable { onPick() } else Modifier,
        ),
        borderColor = border,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            PieCanvas(
                slices = denominator,
                filled = (0 until numerator).toSet(),
                diameter = diameter,
                highlight = Accent,
                description = stringResource(R.string.cd_pie, denominator, numerator),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            FractionGlyph(
                numerator = numerator,
                denominator = denominator,
                compact = !large,
            )
        }
    }
}
