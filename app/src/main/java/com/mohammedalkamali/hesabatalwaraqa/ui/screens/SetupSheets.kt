package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.ui.components.HukmTextField
import com.mohammedalkamali.hesabatalwaraqa.ui.components.Rtl
import com.mohammedalkamali.hesabatalwaraqa.ui.components.SegmentedPicker
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors
import com.mohammedalkamali.hesabatalwaraqa.util.parseAnyDigits

private val teamColors = listOf(
    Color(0xFF33A6FF), Color(0xFFFF7333),
    Color(0xFF66D98C), Color(0xFFCC66E6)
)

@Composable
fun HokmThabetSetupSheet(
    winTarget: Int,
    onStart: (Int) -> Unit,
) {
    val c = LocalHukmColors.current
    var input by remember { mutableStateOf("") }
    Rtl {
        Column(
            Modifier
                .background(Brush.linearGradient(listOf(c.bgDark, c.bg)))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("حدد هدف اللعبة", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = c.goldText)
            Text("حكم ثابت — نقاط × ١٠", fontSize = 14.sp, color = c.goldText.copy(alpha = 0.7f))
            HukmTextField(
                value = input, onValueChange = { input = it },
                placeholder = "الهدف (مثال: ٥٠٠)", fontSize = 26.sp,
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryButton("ابدأ اللعبة") {
                val v = parseAnyDigits(input)
                if (v != null && v > 0) onStart(v)
            }
        }
    }
}

@Composable
fun CustomGameSetupSheet(
    winTarget: Int,
    teamCount: Int,
    teamNames: List<String>,
    lowLoses: Boolean,
    onChange: (winTarget: Int, teamCount: Int, teamNames: List<String>, lowLoses: Boolean) -> Unit,
    onStart: () -> Unit,
) {
    val c = LocalHukmColors.current
    var target by remember { mutableStateOf(winTarget) }
    var count by remember { mutableStateOf(teamCount) }
    var names by remember { mutableStateOf(teamNames) }
    var low by remember { mutableStateOf(lowLoses) }
    var targetInput by remember { mutableStateOf("") }

    fun push() = onChange(target, count, names, low)

    Rtl {
        Column(
            Modifier
                .background(Brush.linearGradient(listOf(c.bgDark, c.bg, c.bgDark)))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text("إعداد اللعبة المخصصة", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text("هدف الفوز", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                HukmTextField(
                    value = targetInput,
                    onValueChange = {
                        targetInput = it
                        parseAnyDigits(it)?.let { n -> if (n > 0) { target = n; push() } }
                    },
                    placeholder = "مثال: 100", textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("الهدف: $target نقطة", fontSize = 12.sp, color = c.goldText.copy(alpha = 0.6f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            GoldDivider()

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text("قاعدة الخسارة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                SegmentedPicker(
                    options = listOf("من يصل أولاً يفوز" to false, "من يصل أولاً يخسر" to true),
                    selected = low,
                    onSelect = { low = it; push() },
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                )
            }

            GoldDivider()

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text("عدد الفرق", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    (2..4).forEach { n ->
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (count == n) c.appGold else c.card)
                                .clickable { count = n; push() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$n", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (count == n) c.navyDark else c.goldText)
                        }
                    }
                }
            }

            GoldDivider()

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("أسماء الفرق", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                (0 until count).forEach { i ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(teamColors[i % teamColors.size]))
                        HukmTextField(
                            value = names.getOrElse(i) { "" },
                            onValueChange = { v ->
                                names = names.toMutableList().also { list ->
                                    while (list.size <= i) list.add("")
                                    list[i] = v
                                }
                                push()
                            },
                            placeholder = "الفريق ${i + 1}",
                            numeric = false, textAlign = TextAlign.End, fontSize = 16.sp,
                            borderColor = teamColors[i % teamColors.size].copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            PrimaryButton("ابدأ اللعبة ←") { push(); onStart() }
            Spacer(Modifier.size(8.dp))
        }
    }
}

@Composable
private fun GoldDivider() {
    val c = LocalHukmColors.current
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.goldText.copy(alpha = 0.2f)))
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.appGold)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = c.navyDark)
    }
}
