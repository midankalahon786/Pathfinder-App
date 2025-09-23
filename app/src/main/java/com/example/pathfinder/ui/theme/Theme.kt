package com.example.pathfinder.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
// --- Light Color Scheme (Expanded) ---
private val LightColorScheme = lightColorScheme(
    primary = TealHeader,
    onPrimary = Color.White,
    secondary = TealButton,
    onSecondary = Color.White,
    tertiary = Green,
    onTertiary = Color.White,
    background = LightPurpleBackground,
    onBackground = DarkGrayText,
    surface = Color.White,
    onSurface = DarkGrayText,
    surfaceVariant = VeryLightGray,
    onSurfaceVariant = MediumGrayText,
    error = RedLogOut,
    onError = Color.White,
    errorContainer = LightRed,
    onErrorContainer = DarkRed,
    secondaryContainer = GraySwitchUser,
    onSecondaryContainer = DarkGrayText, // Changed from Color.White

    outline = DividerColor
)

// --- Dark Color Scheme (Corrected & Expanded) ---
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    secondary = DarkPrimary,
    onSecondary = Color.White,
    tertiary = Green,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkerGray, // Now defined
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = RedLogOut,
    onError = Color.White,
    errorContainer = DarkRed, // Now defined
    onErrorContainer = LightRed, // Now defined
    secondaryContainer = DarkerGray, // Now defined
    onSecondaryContainer = DarkOnSurfaceVariant,
    outline = DarkGrayText
)

@Composable
fun PathfinderAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}