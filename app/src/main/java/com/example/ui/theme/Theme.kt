package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppleBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF162A45),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = ApplePurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF331F41),
    onSecondaryContainer = Color(0xFFF2DAFF),
    tertiary = AppleGreen,
    onTertiary = Color.Black,
    background = Color(0xFF090B0E),
    surface = Color(0xFF11141A),
    surfaceVariant = Color(0xFF1B202A),
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED),
    onSurfaceVariant = Color(0xFFA1A8B8),
    outline = Color(0xFF2E3544)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00629E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFE5FF),
    secondary = Color(0xFF704E94),
    background = Color(0xFFF7F9FC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9ECF3),
    onBackground = Color(0xFF161A22),
    onSurface = Color(0xFF161A22),
    outline = Color(0xFFCDD2DC)
)

@Composable
fun DynamicIslandTheme(
    darkTheme: Boolean = true, // Default to sleek dark OLED theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
