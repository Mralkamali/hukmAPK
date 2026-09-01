package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mohammedalkamali.hesabatalwaraqa.model.GameType
import com.mohammedalkamali.hesabatalwaraqa.ui.components.DecorativeHeader
import com.mohammedalkamali.hesabatalwaraqa.ui.components.Rtl
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors

@Composable
fun HomeScreen(onSelect: (GameType) -> Unit) {
    val c = LocalHukmColors.current
    var showSettings by remember { mutableStateOf(false) }

    Rtl {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(c.bgDark, c.bg, c.bgDark)))
        ) {
            com.mohammedalkamali.hesabatalwaraqa.ui.components.ScreenBorderDecoration()
            Column(Modifier.fillMaxSize()) {
                DecorativeHeader(Modifier.padding(horizontal = 18.dp).padding(top = 14.dp))
                Text(
                    "اختر اللعبة",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = c.goldText,
                    modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(GameType.entries.toList()) { game ->
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(c.cardBg)
                                .border(1.2.dp, game.accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                .clickable { onSelect(game) }
                                .padding(vertical = 20.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(game.suits, fontSize = 28.sp)
                            Text(game.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(
                                game.ruleText,
                                fontSize = 11.sp,
                                color = c.goldText.copy(alpha = 0.45f),
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 28.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(c.goldText.copy(alpha = 0.10f))
                            .border(1.dp, c.goldText.copy(alpha = 0.28f), RoundedCornerShape(50))
                            .clickable { showSettings = true }
                            .padding(horizontal = 18.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Icon(Icons.Filled.Settings, null, tint = c.goldText, modifier = Modifier.size(16.dp))
                        Text("الإعدادات", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.goldText)
                    }
                }
            }
        }
    }

    if (showSettings) {
        Dialog(
            onDismissRequest = { showSettings = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AppSettingsScreen(onClose = { showSettings = false })
        }
    }
}
