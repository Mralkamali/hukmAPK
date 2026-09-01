package com.mohammedalkamali.hesabatalwaraqa.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Fixed dark-theme reference colors (match Swift `Color` extension)
val Navy = Color(0xFF0D1433)
val NavyDark = Color(0xFF080D24)
val NavyMid = Color(0xFF121C42)
val AppGold = Color(0xFFDBB861)
val TeamUs = Color(0xFF33A6FF)
val TeamThem = Color(0xFFFF7333)
val Rummy = Color(0xFFF2A640)

@Immutable
data class HukmColors(
    val dark: Boolean,
    val goldText: Color,
    val bgDark: Color,
    val bg: Color,
    val card: Color,
    val cardBg: Color,
    val subtle: Color,
) {
    val navy get() = Navy
    val navyDark get() = NavyDark
    val appGold get() = AppGold
    val teamUs get() = TeamUs
    val teamThem get() = TeamThem
    val rummy get() = Rummy
}

fun hukmColors(dark: Boolean): HukmColors = if (dark) {
    HukmColors(
        dark = true,
        goldText = Color(0xFFDBB861),
        bgDark = Color(0xFF080D24),
        bg = Color(0xFF0D1433),
        card = Color(0xFF121C42),
        cardBg = Color(0xFF121C42).copy(alpha = 0.65f),
        subtle = Color.White.copy(alpha = 0.05f),
    )
} else {
    HukmColors(
        dark = false,
        goldText = Color(0xFF85631F),
        bgDark = Color(0xFFF1EBDC),
        bg = Color(0xFFFBF6EC),
        card = Color.White,
        cardBg = Color.White,
        subtle = Color(0xFF85662A).copy(alpha = 0.10f),
    )
}

val LocalHukmColors = staticCompositionLocalOf { hukmColors(true) }
