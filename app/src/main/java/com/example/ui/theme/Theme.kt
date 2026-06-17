package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ArabesqueGold,
    onPrimary = Color.Black,
    primaryContainer = ArabesqueDarkSurface,
    onPrimaryContainer = ArabesqueGoldLight,
    secondary = ArabesqueGoldLight,
    onSecondary = Color.Black,
    tertiary = EmeraldSuccess,
    onTertiary = Color.Black,
    background = ArabesqueDarkBg,
    onBackground = Color.White,
    surface = ArabesqueDarkSurface,
    onSurface = Color.White,
    error = CrimsonFailure,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ArabesqueNavy,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = ArabesqueNavy,
    secondary = ArabesqueLightPrimary,
    onSecondary = Color.White,
    tertiary = EmeraldSuccess,
    onTertiary = Color.White,
    background = ArabesqueLightBg,
    onBackground = Color.Black,
    surface = ArabesqueLightSurface,
    onSurface = Color.Black,
    error = CrimsonFailure,
    onError = Color.White
)

@Composable
fun QuizMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
