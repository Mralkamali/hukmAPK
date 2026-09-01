package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.foundation.border
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors

@Composable
fun OrnamentDivider() {
    val gold = LocalHukmColors.current.goldText
    val sizes = listOf(3, 4, 5, 7, 10, 7, 5, 4, 3)
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        sizes.forEach { s ->
            val op = if (s > 7) 1.0f else if (s > 4) 0.7f else 0.4f
            Box(
                Modifier
                    .size(s.dp)
                    .clip(DiamondShape())
                    .background(gold.copy(alpha = op))
            )
        }
    }
}

@Composable
fun ScreenBorderDecoration() {
    val gold = LocalHukmColors.current.goldText
    val dm = LocalHukmColors.current.dark
    Canvas(Modifier.fillMaxSize()) {
        val m = 8.dp.toPx()
        val outer = Size(size.width - m * 2, size.height - m * 2)
        val inner = Size(size.width - (m + 5.dp.toPx()) * 2, size.height - (m + 5.dp.toPx()) * 2)
        drawRoundRect(
            color = gold.copy(alpha = if (dm) 0.18f else 0.07f),
            topLeft = Offset(m, m),
            size = outer,
            cornerRadius = CornerRadius(36.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
        drawRoundRect(
            color = gold.copy(alpha = 0.08f),
            topLeft = Offset(m + 5.dp.toPx(), m + 5.dp.toPx()),
            size = inner,
            cornerRadius = CornerRadius(31.dp.toPx()),
            style = Stroke(width = 0.5.dp.toPx())
        )
    }
}

@Composable
fun DecorativeHeader(modifier: Modifier = Modifier) {
    val c = LocalHukmColors.current
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.subtle)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        c.goldText.copy(alpha = 0.55f),
                        c.goldText.copy(alpha = 0.15f),
                        c.goldText.copy(alpha = 0.55f)
                    )
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OrnamentDivider()
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("♠", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (c.dark) Color.White.copy(alpha = 0.88f) else Color.Black.copy(alpha = 0.88f))
            Text("♦", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.teamThem.copy(alpha = 0.9f))
            Box(Modifier.size(13.dp).clip(DiamondShape()).background(c.goldText))
            Text("♥", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.teamThem.copy(alpha = 0.9f))
            Text("♣", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (c.dark) Color.White.copy(alpha = 0.88f) else Color.Black.copy(alpha = 0.88f))
        }
        Spacer(Modifier.weight(1f))
        OrnamentDivider()
    }
}
