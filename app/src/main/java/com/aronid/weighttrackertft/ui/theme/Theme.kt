package com.aronid.weighttrackertft.ui.theme

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

//https://material-foundation.github.io/material-theme-builder/

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF63A4FF),
    onPrimary = Color(0xFF001E3C),

    primaryContainer = Color(0xFF102A43),
    onPrimaryContainer = Color(0xFFD6EBFF),

    secondary = Color(0xFF90A4AE),
    onSecondary = Color(0xFF1E2930),

    secondaryContainer = Color(0xFF37474F),
    onSecondaryContainer = Color(0xFFECEFF1),

    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFB0BEC5),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    surfaceVariant = Color(0xFF263238),
    outline = Color(0xFF90A4AE)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF63A4FF),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFD6EBFF),
    onPrimaryContainer = Color(0xFF102A43),

    secondary = Color(0xFF90A4AE),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFECEFF1),
    onSecondaryContainer = Color(0xFF455A64),

    background = Color(0xFFFFFFFF),
    onBackground = Color(0XFF000000),
//For text color
    surface = Color(0xFF3F51B5),
    onSurface = Color(0xFF050000),

    error = Color(0xFFB00020),
    onError = Color.White,

    surfaceVariant = Color(0xFFE3F2FD),
    outline = Color(0xFF90A4AE)
)



@Composable
fun WeightTrackerTFTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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