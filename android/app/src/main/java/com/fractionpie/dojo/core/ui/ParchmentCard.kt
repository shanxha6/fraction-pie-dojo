package com.fractionpie.dojo.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.SurfaceParchment

@Composable
fun ParchmentCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderSand,
    containerColor: Color = SurfaceParchment,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        border = BorderStroke(2.dp, borderColor),
        shadowElevation = elevation,
    ) {
        Column(content = content)
    }
}
