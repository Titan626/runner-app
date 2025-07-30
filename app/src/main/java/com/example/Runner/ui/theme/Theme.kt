package com.example.Runner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Athletic Dark Theme - inspired by Nike Run Club
private val DarkColorScheme = darkColorScheme(
    primary = RunnerOrange,
    onPrimary = RunnerWhite,
    secondary = RunnerRed,
    onSecondary = RunnerWhite,
    tertiary = Gray400,
    onTertiary = RunnerBlack,
    background = BackgroundDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    outline = Gray600,
    error = StatusError,
    onError = RunnerWhite
)

// Athletic Light Theme - inspired by Nike Run Club
private val LightColorScheme = lightColorScheme(
    primary = RunnerBlack,
    onPrimary = RunnerWhite,
    secondary = RunnerOrange,
    onSecondary = RunnerWhite,
    tertiary = Gray600,
    onTertiary = RunnerWhite,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    outline = Gray300,
    error = StatusError,
    onError = RunnerWhite
)

@Composable
fun RunnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled for consistent athletic branding
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep the old name for backward compatibility, but rename to RunnerTheme
@Composable
fun TestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    RunnerTheme(darkTheme, dynamicColor, content)
}