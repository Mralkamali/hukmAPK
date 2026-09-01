package com.mohammedalkamali.hesabatalwaraqa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontFamily

/** appTheme values: "system" | "dark" | "light" */
@Composable
fun HukmTheme(
    appTheme: String = "system",
    content: @Composable () -> Unit,
) {
    val dark = when (appTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    val colors = hukmColors(dark)

    val scheme = if (dark) {
        darkColorScheme(
            background = colors.bg,
            surface = colors.card,
            primary = colors.goldText,
        )
    } else {
        lightColorScheme(
            background = colors.bg,
            surface = colors.card,
            primary = colors.goldText,
        )
    }

    val rounded = Typography() // default; individual Text() calls set sizing

    CompositionLocalProvider(LocalHukmColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = rounded, content = content)
    }
}

val RoundedFamily = FontFamily.Default
