package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors

@Composable
fun CircularGauge(
    score: Int,
    target: Int,
    color: Color,
    compact: Boolean = true,
) {
    val c = LocalHukmColors.current
    val d = if (compact) 148.dp else 200.dp
    val strokeW = if (compact) 11.dp else 14.dp
    val scoreFs = if (compact) 52.sp else 68.sp
    val targetFs = if (compact) 11.sp else 14.sp
    val progress = (score.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "gaugeProgress"
    )

    Box(Modifier.size(d), contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(percent = 50)).background(c.bgDark))
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeW.toPx()
            val inset = sw / 2f
            val arcSize = Size(size.width - sw, size.height - sw)
            drawArc(
                color = color.copy(alpha = 0.12f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(width = sw)
            )
            drawArc(
                brush = Brush.linearGradient(
                    listOf(color.copy(alpha = 0.65f), color, color.copy(alpha = 0.85f))
                ),
                startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(width = sw, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text("$score", fontSize = scoreFs, fontWeight = FontWeight.Bold)
            Text("من $target", fontSize = targetFs, fontWeight = FontWeight.Medium, color = c.goldText.copy(alpha = 0.35f))
        }
    }
}

@Composable
fun ScoreBar(score: Int, target: Int, color: Color) {
    val c = LocalHukmColors.current
    val progress = (score.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animated by animateFloatAsState(progress, tween(600), label = "scoreBar")
    Box(
        Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(c.subtle)
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .height(9.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.horizontalGradient(listOf(color.copy(alpha = 0.55f), color)))
        )
    }
}
