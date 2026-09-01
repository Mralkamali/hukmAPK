package com.mohammedalkamali.hesabatalwaraqa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors

/** Segmented control — SwiftUI `.pickerStyle(.segmented)`. options = list of (label, value). */
@Composable
fun <T> SegmentedPicker(
    options: List<Pair<String, T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalHukmColors.current
    Row(
        modifier
            .clip(RoundedCornerShape(9.dp))
            .background(c.subtle)
            .border(1.dp, c.goldText.copy(alpha = 0.18f), RoundedCornerShape(9.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { (label, value) ->
            val on = value == selected
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (on) c.appGold else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(value) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (on) c.navyDark else c.goldText.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

/** Styled text field used across setup sheets and score inputs. */
@Composable
fun HukmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    numeric: Boolean = true,
    borderColor: androidx.compose.ui.graphics.Color? = null,
    textAlign: androidx.compose.ui.text.style.TextAlign = androidx.compose.ui.text.style.TextAlign.Center,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    enabled: Boolean = true,
) {
    val c = LocalHukmColors.current
    val bc = borderColor ?: c.goldText.copy(alpha = 0.4f)
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = MaterialThemeContentColor(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(c.goldText),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = if (numeric) androidx.compose.ui.text.input.KeyboardType.Number
            else androidx.compose.ui.text.input.KeyboardType.Text
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.card)
            .border(1.2.dp, bc, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(placeholder, fontSize = fontSize, fontWeight = FontWeight.Bold, color = c.goldText.copy(alpha = 0.3f))
                }
                inner()
            }
        }
    )
}

@Composable
private fun MaterialThemeContentColor(): androidx.compose.ui.graphics.Color =
    androidx.compose.material3.LocalContentColor.current

/** Simple +/- stepper row — SwiftUI `Stepper`. */
@Composable
fun StepperRow(
    label: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalHukmColors.current
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp)
        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        StepBtn("−") { if (value > range.first) onChange(value - 1) }
        androidx.compose.foundation.layout.Spacer(Modifier.padding(3.dp))
        StepBtn("+") { if (value < range.last) onChange(value + 1) }
    }
}

@Composable
private fun StepBtn(sym: String, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(c.goldText.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(sym, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = c.goldText)
    }
}
