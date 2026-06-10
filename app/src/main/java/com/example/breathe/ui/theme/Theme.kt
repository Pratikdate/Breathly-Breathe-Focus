package com.shanacoder.breathly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    secondary = TealDark,
    tertiary = TealLight,
    background = BackgroundColor,
    surface = CardBackground,
    onPrimary = CardBackground,
    onSecondary = CardBackground,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun BreathlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Forcing light theme for prototype fidelity

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
