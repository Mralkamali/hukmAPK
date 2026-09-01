package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Press-and-hold gesture. Fires [onFire] once the finger has been held for [durationMs]
 * (matching SwiftUI `.onLongPressGesture(minimumDuration:)`). [progress] is driven 0→1
 * while holding and reset to 0 on release; observe [onPressingChange] for visual state.
 */
fun Modifier.holdToFire(
    enabled: Boolean,
    durationMs: Int,
    progress: Animatable<Float, *>,
    scope: kotlinx.coroutines.CoroutineScope,
    onPressingChange: (Boolean) -> Unit,
    onFire: () -> Unit,
): Modifier = this.pointerInput(enabled, durationMs) {
    detectTapGestures(
        onPress = {
            if (!enabled) return@detectTapGestures
            onPressingChange(true)
            progress.snapTo(0f)
            val anim = scope.launch { progress.animateTo(1f, tween(durationMs, easing = LinearEasing)) }
            var fired = false
            val fire = scope.launch { delay(durationMs.toLong()); fired = true; onFire() }
            tryAwaitRelease()
            onPressingChange(false)
            if (!fired) { fire.cancel(); anim.cancel() }
            scope.launch { progress.animateTo(0f, tween(220)) }
        }
    )
}

@Composable
fun HoldButton(
    title: String,
    color: Color,
    durationMs: Int = 450,
    badge: String? = null,
    disabled: Boolean = false,
    compact: Boolean = true,
    action: () -> Unit,
) {
    val c = LocalHukmColors.current
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var pressing by remember { mutableStateOf(false) }
    val d = if (compact) 52.dp else 70.dp
    val scale by animateFloatAsState(if (pressing) 1.06f else 1f, label = "holdScale")

    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            Modifier
                .size(d)
                .scale(scale)
                .holdToFire(!disabled, durationMs, progress, scope, { pressing = it }, action),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(d).clip(CircleShape).background(color.copy(alpha = if (pressing) 0.18f else 0.10f)))
            Canvas(Modifier.size(d)) {
                val sw = 3.5.dp.toPx()
                drawArc(
                    color = color.copy(alpha = if (pressing) 0f else 0.30f),
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = Offset(sw / 2, sw / 2),
                    size = Size(size.width - sw, size.height - sw),
                    style = Stroke(width = 1.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = -90f, sweepAngle = 360f * progress.value, useCenter = false,
                    topLeft = Offset(sw / 2, sw / 2),
                    size = Size(size.width - sw, size.height - sw),
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
            Text(
                title,
                style = TextStyle(
                    fontSize = if (compact) 11.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (disabled) color.copy(alpha = 0.30f) else color
                )
            )
        }
        if (badge != null) {
            Text(
                badge,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = c.navyDark,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = if (disabled) 0.3f else 0.85f))
                    .padding(PaddingValues(horizontal = 4.dp, vertical = 2.dp))
            )
        }
    }
}
