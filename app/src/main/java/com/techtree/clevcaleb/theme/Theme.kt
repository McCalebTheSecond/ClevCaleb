package com.techtree.clevcaleb.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HermesNousBlueDark = darkColorScheme(
    primary = HermesColors.Primary,
    onPrimary = HermesColors.PrimaryForeground,
    primaryContainer = HermesColors.Accent,
    onPrimaryContainer = HermesColors.AccentForeground,
    secondary = HermesColors.Secondary,
    onSecondary = HermesColors.SecondaryForeground,
    tertiary = HermesColors.NousBlue,
    background = HermesColors.Background,
    onBackground = HermesColors.Foreground,
    surface = HermesColors.Card,
    onSurface = HermesColors.Foreground,
    surfaceVariant = HermesColors.Muted,
    onSurfaceVariant = HermesColors.MutedForeground,
    outline = HermesColors.Border,
    error = HermesColors.Destructive,
    onError = Color.White,
)

@Composable
fun ClevCalebTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HermesNousBlueDark,
        content = content,
    )
}
