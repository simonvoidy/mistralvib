package com.emberline.launcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EmberOrange = Color(0xFFE8734A)
private val EmberDeep = Color(0xFF2A1A12)
private val EmberAsh = Color(0xFF2E2E2E)

private val DarkScheme = darkColorScheme(
    primary = EmberOrange,
    secondary = Color(0xFFF2A65A),
    background = EmberDeep,
    surface = EmberAsh,
    onPrimary = Color.White
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFFB4502A),
    secondary = EmberOrange,
    onPrimary = Color.White
)

@Composable
fun EmberlineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkScheme else LightScheme,
        content = content
    )
}
