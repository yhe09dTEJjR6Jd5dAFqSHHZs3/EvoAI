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
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = NeonTeal,
    onSecondary = Color(0xFF003831),
    secondaryContainer = Color(0xFF005147),
    onSecondaryContainer = Color(0xFF73F8E0),
    tertiary = AccentPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5A189A),
    onTertiaryContainer = Color(0xFFE0AAFF),
    background = DeepObsidian,
    onBackground = TextPrimaryDark,
    surface = CardBackgroundDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Color(0xFFFF5449),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = CyberCyanLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF006B5F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF73F8E0),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = ElectricViolet,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0AAFF),
    onTertiaryContainer = Color(0xFF240046),
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardBackgroundLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE0E5EC),
    onSurfaceVariant = TextSecondaryLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek futuristic dark theme
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
