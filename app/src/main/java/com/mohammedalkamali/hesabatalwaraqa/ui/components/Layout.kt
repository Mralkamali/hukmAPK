package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Wrap content in right-to-left layout — SwiftUI `.environment(\.layoutDirection, .rightToLeft)`. */
@Composable
fun Rtl(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, content = content)
}

/** Force left-to-right for a subtree — the score rows the Swift code flips back to LTR. */
@Composable
fun Ltr(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr, content = content)
}

/** `true` on wide screens — analogue of `horizontalSizeClass == .regular`. */
@Composable
fun isRegularWidth(): Boolean = LocalConfiguration.current.screenWidthDp >= 600
