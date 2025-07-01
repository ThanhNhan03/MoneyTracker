package com.example.moneytracker.ui.theme

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

// Modern Dark Color Scheme for Money Tracker - Neon Style
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    primaryContainer = Primary,
    secondary = IncomeGreen,
    secondaryContainer = Secondary,
    tertiary = Accent,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardBackgroundDark,
    onPrimary = TextOnDark,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    onSurfaceVariant = TextTertiary,
    outline = DividerColorDark,
    error = ExpenseRed,
    onError = TextOnDark
)

// Modern Light Color Scheme for Money Tracker - Neon Style
private val LightColorScheme = lightColorScheme(
    primary = Primary, // Neon Purple #6c5ce7
    primaryContainer = PrimaryLight,
    secondary = IncomeGreen, // Cyan Neon #00cec9
    secondaryContainer = SecondaryLight, 
    tertiary = Accent,
    background = BackgroundLight, // #f5f6fa as requested
    surface = SurfaceLight,
    surfaceVariant = CardBackground,
    onPrimary = TextOnDark,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    error = ExpenseRed, // Pink Red #ff7675
    onError = TextOnDark
)

@Composable
fun MoneyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but we disable it for consistent branding
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}