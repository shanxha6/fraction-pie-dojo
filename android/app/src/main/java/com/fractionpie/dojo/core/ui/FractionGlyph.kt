package com.fractionpie.dojo.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.core.theme.TextPrimary

@Composable
fun FractionGlyph(
    numerator: Int,
    denominator: Int,
    modifier: Modifier = Modifier,
    large: Boolean = false,
    compact: Boolean = false,
    color: Color = TextPrimary,
) {
    val numeratorText = numerator.toString()
    val denominatorText = denominator.toString()
    val style = when {
        compact -> MaterialTheme.typography.titleLarge
        large -> MaterialTheme.typography.displayLarge
        else -> MaterialTheme.typography.displaySmall
    }
    val barWidth = when {
        compact -> 26.dp
        large -> 58.dp
        else -> 44.dp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = numeratorText, style = style, color = color)
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(3.dp)
                .background(color, RoundedCornerShape(2.dp)),
        )
        Text(text = denominatorText, style = style, color = color)
    }
}
