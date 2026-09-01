package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors

@Composable
fun TeamCard(
    name: String,
    score: Int,
    target: Int,
    color: Color,
    isLeading: Boolean = false,
    compact: Boolean = true,
    onLarns: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = LocalHukmColors.current
    val scope = rememberCoroutineScope()
    val pressProgress = remember { Animatable(0f) }
    var pressing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressing) 1.04f else 1f, label = "cardScale")
    val ringSize = if (compact) 158.dp else 214.dp
    val labelOffset = if (compact) 54.dp else 73.dp

    val borderTop = if (pressing) color else color.copy(alpha = if (isLeading) 0.85f else 0.45f)
    val borderMid = c.goldText.copy(alpha = if (isLeading) 0.5f else 0.2f)

    Column(
        modifier
            .scale(scale)
            .shadow(
                elevation = if (pressing) 26.dp else if (isLeading) 24.dp else 14.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = color, spotColor = color
            )
            .clip(RoundedCornerShape(20.dp))
            .background(c.cardBg)
            .border(
                width = if (pressing) 2.dp else if (isLeading) 2.2.dp else 1.2.dp,
                brush = Brush.linearGradient(listOf(borderTop, borderMid, borderTop)),
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (onLarns != null) Modifier.holdToFire(
                    enabled = true, durationMs = 1500, progress = pressProgress, scope = scope,
                    onPressingChange = { pressing = it }, onFire = onLarns
                ) else Modifier
            )
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.goldText)
        Box(contentAlignment = Alignment.Center) {
            CircularGauge(score = score, target = target, color = color, compact = compact)
            if (pressing) {
                Canvas(Modifier.size(ringSize)) {
                    val sw = 6.dp.toPx()
                    drawArc(
                        color = color,
                        startAngle = -90f, sweepAngle = 360f * pressProgress.value, useCenter = false,
                        topLeft = Offset(sw / 2, sw / 2),
                        size = Size(size.width - sw, size.height - sw),
                        style = Stroke(width = sw, cap = StrokeCap.Round)
                    )
                }
                Text(
                    "لارنس",
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color,
                    modifier = Modifier
                        .offset(y = labelOffset)
                        .clip(RoundedCornerShape(50))
                        .background(c.bgDark)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTeamCard(
    name: String,
    score: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val c = LocalHukmColors.current
    val progress = (score.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animated by animateFloatAsState(progress, spring(dampingRatio = 0.8f), label = "customCard")

    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(c.cardBg)
            .border(1.2.dp, color.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.goldText, maxLines = 1)
        Box(Modifier.size(110.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(110.dp).clip(CircleShape).background(c.bgDark))
            Canvas(Modifier.size(110.dp)) {
                val sw = 8.dp.toPx()
                drawArc(
                    color = color.copy(alpha = 0.12f),
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = Offset(sw / 2, sw / 2),
                    size = Size(size.width - sw, size.height - sw),
                    style = Stroke(width = sw)
                )
                drawArc(
                    color = color,
                    startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                    topLeft = Offset(sw / 2, sw / 2),
                    size = Size(size.width - sw, size.height - sw),
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("من $target", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = c.goldText.copy(alpha = 0.35f))
            }
        }
    }
}
