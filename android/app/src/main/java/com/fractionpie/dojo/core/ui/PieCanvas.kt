package com.fractionpie.dojo.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.CrustDeep
import com.fractionpie.dojo.core.theme.CrustFill
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun PieCanvas(
    slices: Int,
    filled: Set<Int>,
    diameter: Dp,
    modifier: Modifier = Modifier,
    rotationDeg: Float = 0f,
    liftDp: Dp = 4.dp,
    highlight: Color = Accent,
    body: Color = CrustFill,
    crust: Color = CrustDeep,
    description: String? = null,
    onWedgeTap: ((Int) -> Unit)? = null,
) {
    val count = slices.coerceAtLeast(1)
    val tapModifier = if (onWedgeTap == null) {
        Modifier
    } else {
        Modifier.pointerInput(count) {
            detectTapGestures { position ->
                val radius = size.width / 2f
                val dx = position.x - radius
                val dy = position.y - size.height / 2f
                if (hypot(dx, dy) <= radius) {
                    val degrees = (kotlin.math.atan2(dy, dx) * 180f / PI).toFloat()
                    val normalized = (degrees + 450f) % 360f
                    val index = (normalized / (360f / count)).toInt().coerceIn(0, count - 1)
                    onWedgeTap(index)
                }
            }
        }
    }
    val semanticsModifier = if (description == null) {
        Modifier
    } else {
        Modifier.semantics { contentDescription = description }
    }

    Canvas(
        modifier = modifier
            .size(diameter)
            .then(tapModifier)
            .then(semanticsModifier),
    ) {
        val crustWidth = (size.minDimension * 0.055f).coerceAtLeast(3f)
        val lift = liftDp.toPx()
        val radius = (size.minDimension / 2f) - crustWidth - lift
        if (radius <= 0f) return@Canvas
        val middle = Offset(size.width / 2f, size.height / 2f)
        val sweep = 360f / count

        for (index in 0 until count) {
            val isFilled = filled.contains(index)
            val startAngle = index * sweep - 90f + rotationDeg
            val midRadians = ((startAngle + sweep / 2f) * PI / 180f)
            val push = if (isFilled) lift else 0f
            val shiftX = (cos(midRadians) * push).toFloat()
            val shiftY = (sin(midRadians) * push).toFloat()
            drawArc(
                color = if (isFilled) highlight else body,
                startAngle = startAngle,
                sweepAngle = if (count == 1) 360f else sweep - 1.6f,
                useCenter = true,
                topLeft = Offset(middle.x - radius + shiftX, middle.y - radius + shiftY),
                size = Size(radius * 2f, radius * 2f),
            )
            if (count > 1) {
                drawArc(
                    color = Background.copy(alpha = 0.75f),
                    startAngle = startAngle,
                    sweepAngle = if (count == 1) 360f else sweep - 1.6f,
                    useCenter = true,
                    topLeft = Offset(middle.x - radius + shiftX, middle.y - radius + shiftY),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 2f),
                )
            }
        }

        drawCircle(
            color = crust,
            radius = radius + crustWidth / 2f,
            center = middle,
            style = Stroke(width = crustWidth),
        )
    }
}
