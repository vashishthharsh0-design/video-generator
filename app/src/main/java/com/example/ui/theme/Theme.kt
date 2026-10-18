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
    primary = CinemaPrimaryCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = CinemaPrimaryCyanLight,
    secondary = CinemaSecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1E78),
    onSecondaryContainer = Color(0xFFE3D6FF),
    tertiary = CinemaAccentMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF6B0033),
    onTertiaryContainer = Color(0xFFFFD8E6),
    background = CinemaBackgroundDark,
    onBackground = CinemaTextPrimaryDark,
    surface = CinemaSurfaceDark,
    onSurface = CinemaTextPrimaryDark,
    surfaceVariant = CinemaSurfaceVariantDark,
    onSurfaceVariant = CinemaTextSecondaryDark,
    outline = CinemaOutlineDark,
    error = CinemaRecordRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBCEBFF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFF6D28D9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADBFF),
    onSecondaryContainer = Color(0xFF22005D),
    tertiary = Color(0xFFBE185D),
    onTertiary = Color.White,
    background = CinemaBackgroundLight,
    onBackground = CinemaTextPrimaryLight,
    surface = CinemaSurfaceLight,
    onSurface = CinemaTextPrimaryLight,
    surfaceVariant = CinemaSurfaceVariantLight,
    onSurfaceVariant = CinemaTextSecondaryLight,
    outline = CinemaOutlineLight,
    error = CinemaRecordRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to filmmaker dark theme for high-fidelity video editing
    dynamicColor: Boolean = false, // Keep branded cinematic palette
    content: @Composable () -> Unit,
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
