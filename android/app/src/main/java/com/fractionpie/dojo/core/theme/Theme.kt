package com.fractionpie.dojo.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DojoColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Background,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = Background,
    secondary = Accent,
    onSecondary = Background,
    secondaryContainer = AccentSoft,
    onSecondaryContainer = TextPrimary,
    tertiary = Gold,
    onTertiary = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceParchment,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = BorderSand,
    outlineVariant = BorderSand,
    error = Primary,
    onError = Background,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DojoColorScheme,
        typography = AppTypography,
        content = content,
    )
}
