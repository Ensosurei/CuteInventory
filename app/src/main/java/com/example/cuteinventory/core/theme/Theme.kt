package com.example.cuteinventory.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = OnDarkPrimary,
    secondary = DarkSecondary,
    onSecondary = OnDarkSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    onBackground = OnDarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = LightSecondary,
    onPrimary = OnLightSecondary,
    secondary = LightPrimary,
    onSecondary = OnLightPrimary,
    tertiary = LightTertiary,
    background = LightBackground,
    onBackground = OnLightBackground,
    surface = LightSurface,
    onSurface = OnLightSurface
)

@Composable
fun CuteInventoryTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val useDarkTheme = darkTheme ?: isSystemInDarkTheme()

    val colors = if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}