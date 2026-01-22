package com.example.collegeschedule.ui.theme

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

//темная цветовая схема
private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = MediumBlue,
    tertiary = Amber,
    background = DarkGray,
    surface = Color(0xFF3A3A3A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
)

//светлая цветовая схема
private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = MediumBlue,
    tertiary = Amber,
    background = LightGray,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = DarkGray,
    onSurface = DarkGray,
)

//тема приложения
@Composable
fun CollegeScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), //автоматическое определение темы
    //dynamic color is available on Android 12+
    dynamicColor: Boolean = true, //динамические цвета для android 12+
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
        typography = Typography, //типографика из Type.kt
        content = content
    )
}