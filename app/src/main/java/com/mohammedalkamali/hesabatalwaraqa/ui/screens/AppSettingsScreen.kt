package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.BuildConfig
import com.mohammedalkamali.hesabatalwaraqa.ui.components.Rtl
import com.mohammedalkamali.hesabatalwaraqa.ui.components.SegmentedPicker
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors
import com.mohammedalkamali.hesabatalwaraqa.util.AppPrefs

@Composable
fun AppSettingsScreen(onClose: () -> Unit) {
    val c = LocalHukmColors.current
    val ctx = LocalContext.current
    val playUrl = "https://play.google.com/store/apps/details?id=" + ctx.packageName

    Rtl {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(c.bgDark, c.bg, c.bgDark)))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("الإعدادات", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.goldText)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.Filled.Close, "إغلاق", tint = c.goldText,
                        modifier = Modifier.size(24.dp).clickable { onClose() }
                    )
                }

                Text("التفضيلات", fontSize = 12.sp, color = c.goldText, fontWeight = FontWeight.Bold)
                SettingRow {
                    Text("صوت التسجيل", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = AppPrefs.soundEnabled,
                        onCheckedChange = { AppPrefs.setSoundEnabled(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = c.appGold)
                    )
                }
                SettingRow {
                    Text("عرض الوقت", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = AppPrefs.showTime,
                        onCheckedChange = { AppPrefs.setShowTime(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = c.appGold)
                    )
                }
                SettingRow {
                    Text("المظهر", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    SegmentedPicker(
                        options = listOf("غامق" to "dark", "فاتح" to "light", "تلقائي" to "system"),
                        selected = AppPrefs.appTheme,
                        onSelect = { AppPrefs.setAppTheme(it) },
                        modifier = Modifier.size(width = 210.dp, height = 34.dp)
                    )
                }

                Spacer(Modifier.size(6.dp))
                Text("عن التطبيق", fontSize = 12.sp, color = c.goldText, fontWeight = FontWeight.Bold)
                SettingRow {
                    Icon(Icons.Filled.Info, null, tint = c.goldText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("الإصدار", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    Text("v" + BuildConfig.VERSION_NAME, fontSize = 14.sp, color = c.goldText.copy(alpha = 0.7f))
                }
                SettingRow {
                    Icon(Icons.Filled.Person, null, tint = c.goldText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("المطوّر", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    Text("Mohammed Alkamali", fontSize = 14.sp, color = c.goldText.copy(alpha = 0.7f))
                }
                LinkRow(Icons.Filled.Star, "قيّم التطبيق ⭐️") {
                    runCatching {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ctx.packageName)))
                    }.onFailure {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playUrl)))
                    }
                }
                LinkRow(Icons.Filled.OpenInNew, "افتح صفحة التطبيق في Google Play") {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playUrl)))
                }
                LinkRow(Icons.Filled.Email, "تواصل مع المطوّر") {
                    ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:mr.alkamali@outlook.com")))
                }
            }
        }
    }
}

@Composable
private fun SettingRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    val c = LocalHukmColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.card, RoundedCornerShape(12.dp))
            .border(1.dp, c.goldText.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun LinkRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.card, RoundedCornerShape(12.dp))
            .border(1.dp, c.goldText.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = c.goldText, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(10.dp))
        Text(title, fontSize = 15.sp, color = c.goldText)
    }
}
