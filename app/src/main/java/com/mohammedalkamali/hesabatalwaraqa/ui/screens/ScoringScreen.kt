package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.play.core.review.ReviewManagerFactory
import com.mohammedalkamali.hesabatalwaraqa.model.GameType
import com.mohammedalkamali.hesabatalwaraqa.model.Round
import com.mohammedalkamali.hesabatalwaraqa.ui.components.CustomTeamCard
import com.mohammedalkamali.hesabatalwaraqa.ui.components.DecorativeHeader
import com.mohammedalkamali.hesabatalwaraqa.ui.components.HoldButton
import com.mohammedalkamali.hesabatalwaraqa.ui.components.HukmTextField
import com.mohammedalkamali.hesabatalwaraqa.ui.components.Ltr
import com.mohammedalkamali.hesabatalwaraqa.ui.components.Rtl
import com.mohammedalkamali.hesabatalwaraqa.ui.components.ScreenBorderDecoration
import com.mohammedalkamali.hesabatalwaraqa.ui.components.SegmentedPicker
import com.mohammedalkamali.hesabatalwaraqa.ui.components.StepperRow
import com.mohammedalkamali.hesabatalwaraqa.ui.components.TeamCard
import com.mohammedalkamali.hesabatalwaraqa.ui.components.isRegularWidth
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors
import com.mohammedalkamali.hesabatalwaraqa.util.AppPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val extraTeamColors = listOf(
    Color(0xFF33A6FF), Color(0xFFFF7333),
    Color(0xFF66D98C), Color(0xFFCC66E6)
)

@Composable
fun ScoringScreen(game: GameType, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val compact = !isRegularWidth()
    val c = LocalHukmColors.current
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val st = remember(game) { ScoringState(ctx, game) }

    // Play in-app review on milestone game counts (3 / 8 / 20 / 50)
    LaunchedEffect(st) {
        st.onMilestone = {
            scope.launch {
                delay(2000)
                runCatching {
                    val mgr = ReviewManagerFactory.create(ctx)
                    mgr.requestReviewFlow().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            (ctx as? android.app.Activity)?.let { act ->
                                mgr.launchReviewFlow(act, task.result)
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(st) { onDispose { st.persist() } }

    var showSettings by remember { mutableStateOf(false) }
    var showThabetSetup by remember { mutableStateOf(st.startThabetSetup) }
    var showCustomSetup by remember { mutableStateOf(st.startCustomSetup) }
    var showSettingsHint by remember { mutableStateOf(false) }
    var confirmResetWins by remember { mutableStateOf(false) }
    var dealerAngle by remember { mutableStateOf(0f) }
    var prevRoundCount by remember { mutableIntStateOf(st.rounds.size) }

    var kabotSide by remember { mutableStateOf<Boolean?>(null) }   // true=us
    var mashariSide by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(st.rounds.size) {
        if (st.rounds.size > prevRoundCount) dealerAngle -= 90f
        prevRoundCount = st.rounds.size
    }

    LaunchedEffect(Unit) {
        if (!AppPrefs.settingsHintShown) {
            delay(1500)
            showSettingsHint = true
            delay(4500)
            showSettingsHint = false
            AppPrefs.setSettingsHintShown(true)
        }
    }

    Rtl {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(c.bgDark, c.bg, c.bgDark)))
        ) {
            ScreenBorderDecoration()

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (AppPrefs.showTime) {
                    var now by remember { mutableStateOf(Date()) }
                    LaunchedEffect(Unit) { while (true) { now = Date(); delay(30_000) } }
                    Text(
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
                        fontSize = 12.sp, color = c.goldText.copy(alpha = 0.35f)
                    )
                }

                DecorativeHeader()

                Text(
                    game.title,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = game.accentColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(game.accentColor.copy(alpha = 0.12f))
                        .border(0.5.dp, game.accentColor.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 3.dp)
                )

                // ----- Scores -----
                val usLeads = if (st.lowLoses) st.usScore < st.themScore else st.usScore > st.themScore
                val themLeads = if (st.lowLoses) st.themScore < st.usScore else st.themScore > st.usScore

                if (game == GameType.CUSTOM && st.customTeamCount > 2) {
                    CustomMultiTeam(st)
                } else {
                    val t1 = if (game == GameType.CUSTOM) st.customTeamNames.getOrElse(1) { "لهم" } else "لهم"
                    val t2 = if (game == GameType.CUSTOM) st.customTeamNames.getOrElse(0) { "لنا" } else "لنا"
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Ltr {
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                TeamCard(
                                    name = t1, score = st.themScore, target = st.winTarget,
                                    color = c.teamThem, isLeading = themLeads, compact = compact,
                                    onLarns = if (game == GameType.HOKM) ({ st.declareLarns(false) }) else null,
                                    modifier = Modifier.weight(1f)
                                )
                                TeamCard(
                                    name = t2, score = st.usScore, target = st.winTarget,
                                    color = c.teamUs, isLeading = usLeads, compact = compact,
                                    onLarns = if (game == GameType.HOKM) ({ st.declareLarns(true) }) else null,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        val eq = st.usScore == st.themScore
                        val leadColor = if (eq) c.appGold else if (usLeads) c.teamUs else c.teamThem
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(leadColor.copy(alpha = 0.10f))
                                .border(1.dp, leadColor.copy(alpha = 0.30f), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("الفرق", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = leadColor)
                            Text("${st.diff()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = leadColor)
                        }
                    }
                }

                WinsBar(st, confirmResetWins,
                    onReset = {
                        if (confirmResetWins) { st.resetWins(); confirmResetWins = false }
                        else {
                            confirmResetWins = true
                            scope.launch { delay(3000); confirmResetWins = false }
                        }
                    })

                if (game == GameType.BALOOT) {
                    Ltr {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            ScoreField(st.themInput, { st.themInput = it }, c.teamThem, Modifier.weight(1f), st.gameOver)
                            DealerIndicator(dealerAngle) { dealerAngle -= 90f }
                            ScoreField(st.usInput, { st.usInput = it }, c.teamUs, Modifier.weight(1f), st.gameOver)
                        }
                    }
                }

                if (game == GameType.CUSTOM && st.customLowLoses) {
                    Text(
                        "⚠︎ أقل نقطة أفضل — من يوصل ${st.winTarget} يخسر",
                        fontSize = 12.sp, color = c.goldText.copy(alpha = 0.5f), textAlign = TextAlign.Center
                    )
                }

                // ----- Input area + round log -----
                Column(
                    Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when (game) {
                        GameType.HOKM_THABET -> HokmThabetInputRow(st, compact)
                        GameType.TARNEEB -> TarneebInputRow(st)
                        GameType.CUSTOM -> CustomInputRow(st)
                        GameType.HOKM -> {
                            StandardInputRow(st)
                            HokmSpecialRow(st, compact)
                        }
                        GameType.BALOOT -> BalootBonusRow(st, onKabot = { kabotSide = it }, onMashari = { mashariSide = it })
                    }
                    RoundLog(st, Modifier.weight(1f))
                }

                // ----- Bottom buttons -----
                Row(
                    Modifier.padding(top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PillButton("تغيير اللعبة", Icons.AutoMirrored.Filled.ArrowBack, faint = true) {
                        focus.clearFocus(); onBack()
                    }
                    Box {
                        PillButton("الإعدادات", Icons.Filled.Settings) {
                            focus.clearFocus(); showSettings = true; showSettingsHint = false
                        }
                        if (showSettingsHint) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-58).dp)
                            ) {
                                Text(
                                    "من هنا تغير\nإعدادات اللعبة",
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                                    color = c.navyDark,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(c.appGold)
                                        .padding(horizontal = 14.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (showSettings) {
                SettingsOverlay(
                    st = st,
                    onClose = { showSettings = false },
                    onBack = { showSettings = false; onBack() },
                    onCustomSetup = { showSettings = false; showCustomSetup = true }
                )
            }

            if (st.gameOver) {
                GameOverOverlay(
                    st = st,
                    onSettings = { st.winner = null; st.winnerIsUs = null; showSettings = true }
                )
            }
        }
    }

    // ----- Dialogs -----
    if (showThabetSetup) {
        Dialog(onDismissRequest = { showThabetSetup = false }, properties = DialogProperties(usePlatformDefaultWidth = true)) {
            Box(Modifier.clip(RoundedCornerShape(24.dp))) {
                HokmThabetSetupSheet(winTarget = st.winTarget, onStart = { st.winTarget = it; showThabetSetup = false })
            }
        }
    }
    if (showCustomSetup) {
        Dialog(onDismissRequest = { showCustomSetup = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.padding(16.dp).clip(RoundedCornerShape(24.dp))) {
                CustomGameSetupSheet(
                    winTarget = st.winTarget,
                    teamCount = st.customTeamCount,
                    teamNames = st.customTeamNames,
                    lowLoses = st.customLowLoses,
                    onChange = { wt, tc, tn, ll ->
                        st.winTarget = wt; st.customTeamCount = tc; st.customTeamNames = tn; st.customLowLoses = ll
                    },
                    onStart = { showCustomSetup = false }
                )
            }
        }
    }
    kabotSide?.let { side ->
        Dialog(onDismissRequest = { kabotSide = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.padding(16.dp).clip(RoundedCornerShape(20.dp))) {
                BonusSheet(
                    title = if (side) "كبوت لنا" else "كبوت لهم",
                    withBase = true,
                    onRecord = { total -> st.kabotFor(side, total); kabotSide = null }
                )
            }
        }
    }
    mashariSide?.let { side ->
        Dialog(onDismissRequest = { mashariSide = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.padding(16.dp).clip(RoundedCornerShape(20.dp))) {
                BonusSheet(
                    title = if (side) "مشاريع لنا" else "مشاريع لهم",
                    withBase = false,
                    onRecord = { total -> st.mashariFor(side, total); mashariSide = null }
                )
            }
        }
    }
}

// ============================================================================
// Score input field
// ============================================================================

@Composable
private fun ScoreField(
    value: String,
    onChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
) {
    HukmTextField(
        value = value, onValueChange = onChange, modifier = modifier,
        borderColor = color.copy(alpha = 0.5f), enabled = !disabled, fontSize = 22.sp
    )
}

// ============================================================================
// Wins bar
// ============================================================================

@Composable
private fun WinsBar(st: ScoringState, confirmReset: Boolean, onReset: () -> Unit) {
    val c = LocalHukmColors.current
    Ltr {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(c.subtle)
                .border(0.5.dp, c.goldText.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .padding(horizontal = 28.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(Icons.Filled.EmojiEvents, null, tint = c.teamThem.copy(alpha = 0.75f), modifier = Modifier.size(14.dp))
                Text("${st.themWins}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.teamThem)
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (confirmReset) Color.Red.copy(alpha = 0.15f) else c.subtle)
                    .clickable { onReset() }
                    .padding(horizontal = if (confirmReset) 8.dp else 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (confirmReset) {
                    Text("تأكيد؟", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red.copy(alpha = 0.85f))
                } else {
                    Icon(Icons.Filled.Refresh, "تصفير", tint = c.goldText.copy(alpha = 0.45f), modifier = Modifier.size(13.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${st.usWins}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.teamUs)
                Icon(Icons.Filled.EmojiEvents, null, tint = c.teamUs.copy(alpha = 0.75f), modifier = Modifier.size(14.dp))
            }
        }
    }
}

// ============================================================================
// Standard input (حكم)
// ============================================================================

@Composable
private fun StandardInputRow(st: ScoringState) {
    Ltr {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            ScoreField(st.themInput, { st.themInput = it }, LocalHukmColors.current.teamThem, Modifier.weight(1f), st.gameOver)
            RecordButton("سجل", enabled = !st.gameOver) { st.record() }
            ScoreField(st.usInput, { st.usInput = it }, LocalHukmColors.current.teamUs, Modifier.weight(1f), st.gameOver)
        }
    }
}

@Composable
private fun RecordButton(text: String, enabled: Boolean = true, width: Int = 76, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .width(width.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (enabled) c.appGold else c.appGold.copy(alpha = 0.4f))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.navyDark)
    }
}

// ============================================================================
// Hokm — larns / bound
// ============================================================================

@Composable
private fun HokmSpecialRow(st: ScoringState, compact: Boolean) {
    val c = LocalHukmColors.current
    Ltr {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)) {
                HoldButton("باوند", c.teamThem, badge = if (st.boundMode == "cup") "كاس" else "١٨", disabled = st.gameOver, compact = compact) { st.declareBound(false) }
                HoldButton("لارنس", c.teamThem, badge = "♛", disabled = st.gameOver, compact = compact) { st.declareLarns(false) }
            }
            Spacer(Modifier.width(92.dp))
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)) {
                HoldButton("لارنس", c.teamUs, badge = "♛", disabled = st.gameOver, compact = compact) { st.declareLarns(true) }
                HoldButton("باوند", c.teamUs, badge = if (st.boundMode == "cup") "كاس" else "١٨", disabled = st.gameOver, compact = compact) { st.declareBound(true) }
            }
        }
    }
}

// ============================================================================
// Baloot bonus buttons
// ============================================================================

@Composable
private fun BalootBonusRow(st: ScoringState, onKabot: (Boolean) -> Unit, onMashari: (Boolean) -> Unit) {
    val c = LocalHukmColors.current
    Ltr {
        Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    BonusBtn("كبوت لهم", c.teamThem, Modifier.weight(1f), st.gameOver) { onKabot(false) }
                    Spacer(Modifier.width(76.dp))
                    BonusBtn("كبوت لنا", c.teamUs, Modifier.weight(1f), st.gameOver) { onKabot(true) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    BonusBtn("مشاريع لهم", c.teamThem, Modifier.weight(1f), st.gameOver) { onMashari(false) }
                    Spacer(Modifier.width(76.dp))
                    BonusBtn("مشاريع لنا", c.teamUs, Modifier.weight(1f), st.gameOver) { onMashari(true) }
                }
            }
            RecordButton("سجل", enabled = !st.gameOver) { st.record() }
        }
    }
}

@Composable
private fun BonusBtn(title: String, color: Color, modifier: Modifier, disabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable(enabled = !disabled) { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// ============================================================================
// حكم ثابت input
// ============================================================================

@Composable
private fun HokmThabetInputRow(st: ScoringState, compact: Boolean) {
    val c = LocalHukmColors.current
    val green = Color(0xFF33C74D)
    val red = Color(0xFFF24747)
    val circle = if (compact) 52.dp else 70.dp
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Ltr {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    ScoreField(st.themInput, { st.themInput = it }, c.teamThem, Modifier.fillMaxWidth(), st.gameOver)
                    SmallResultBtn("فاز لهم", c.teamThem, !st.gameOver) { st.recordHokmThabet(usWon = false, themWon = true) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircleResultBtn("الكل\nفاز", green, circle, !st.gameOver) { st.recordHokmThabet(true, true) }
                    CircleResultBtn("الكل\nخسر", red, circle, !st.gameOver) { st.recordHokmThabet(false, false) }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    ScoreField(st.usInput, { st.usInput = it }, c.teamUs, Modifier.fillMaxWidth(), st.gameOver)
                    SmallResultBtn("فاز لنا", c.teamUs, !st.gameOver) { st.recordHokmThabet(usWon = true, themWon = false) }
                }
            }
        }
        if (st.showThabetLarns) {
            Ltr {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        HoldButton("لارنس", c.teamThem, badge = "♛", disabled = st.gameOver, compact = compact) { st.declareLarns(false) }
                    }
                    Spacer(Modifier.width(76.dp))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        HoldButton("لارنس", c.teamUs, badge = "♛", disabled = st.gameOver, compact = compact) { st.declareLarns(true) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallResultBtn(text: String, color: Color, enabled: Boolean, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.navyDark)
    }
}

@Composable
private fun CircleResultBtn(text: String, color: Color, size: androidx.compose.ui.unit.Dp, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.13f))
            .border(1.5.dp, color.copy(alpha = 0.45f), CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
    }
}

// ============================================================================
// طرنيب input
// ============================================================================

@Composable
private fun TarneebInputRow(st: ScoringState) {
    val c = LocalHukmColors.current
    Column(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(top = 6.dp).verticalScroll(rememberScrollState())) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(true to "المشتري لنا", false to "المشتري لهم").forEach { (isUs, label) ->
                val on = st.tarneebBuyerIsUs == isUs
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (on) (if (isUs) c.teamUs else c.teamThem) else c.card)
                        .clickable { st.tarneebBuyerIsUs = isUs }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (on) c.navyDark else c.goldText)
                }
            }
        }
        Ltr {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                (7..13).forEach { n ->
                    val on = st.tarneebBid == n
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (on) c.appGold else c.card)
                            .clickable {
                                st.tarneebBid = n
                                if (n == 13) st.tarneebTricks = 13
                                else if (st.tarneebTricks < n) st.tarneebTricks = n
                            }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (n == 13) "كبوت" else "$n",
                            fontSize = if (n == 13) 10.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (on) c.navyDark else c.goldText
                        )
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            RoundIconBtn("−") { if (st.tarneebTricks > 0) st.tarneebTricks -= 1 }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("أكلات المشتري: ${st.tarneebTricks}", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("الدفاع: ${13 - st.tarneebTricks}", fontSize = 11.sp, color = c.goldText.copy(alpha = 0.45f))
            }
            RoundIconBtn("+") { if (st.tarneebTricks < 13) st.tarneebTricks += 1 }
        }
        Text(st.tarneebPreview, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.goldText.copy(alpha = 0.85f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        FullWidthGoldButton("سجّل الجولة", !st.gameOver) { st.recordTarneeb() }
    }
}

@Composable
private fun RoundIconBtn(sym: String, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier.size(34.dp).clip(CircleShape).background(c.goldText.copy(alpha = 0.10f)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text(sym, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.goldText) }
}

@Composable
private fun FullWidthGoldButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) c.appGold else c.appGold.copy(alpha = 0.4f))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.navyDark) }
}

// ============================================================================
// مخصص
// ============================================================================

@Composable
private fun CustomMultiTeam(st: ScoringState) {
    val count = st.customTeamCount
    val scores = listOf(st.usScore, st.themScore) + st.extraScores.take(count - 2)
    Ltr {
        if (count == 3) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                (0 until 3).forEach { i ->
                    CustomTeamCard(st.customTeamNames.getOrElse(i) { "الفريق ${i + 1}" }, scores.getOrElse(i) { 0 }, st.winTarget, extraTeamColors[i], Modifier.weight(1f))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    (0 until 2).forEach { i ->
                        CustomTeamCard(st.customTeamNames.getOrElse(i) { "الفريق ${i + 1}" }, scores.getOrElse(i) { 0 }, st.winTarget, extraTeamColors[i], Modifier.weight(1f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    (2 until count).forEach { i ->
                        CustomTeamCard(st.customTeamNames.getOrElse(i) { "الفريق ${i + 1}" }, scores.getOrElse(i) { 0 }, st.winTarget, extraTeamColors[i], Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomInputRow(st: ScoringState) {
    val c = LocalHukmColors.current
    val count = st.customTeamCount
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CustomField(st.customTeamNames.getOrElse(0) { "الفريق ١" }, st.usInput, { st.usInput = it }, extraTeamColors[0], Modifier.weight(1f))
            CustomField(st.customTeamNames.getOrElse(1) { "الفريق ٢" }, st.themInput, { st.themInput = it }, extraTeamColors[1], Modifier.weight(1f))
            if (count > 2) CustomField(st.customTeamNames.getOrElse(2) { "الفريق ٣" }, st.extraInputs.getOrElse(0) { "" }, { st.extraInputs = listOf(it, st.extraInputs.getOrElse(1) { "" }) }, extraTeamColors[2], Modifier.weight(1f))
            if (count > 3) CustomField(st.customTeamNames.getOrElse(3) { "الفريق ٤" }, st.extraInputs.getOrElse(1) { "" }, { st.extraInputs = listOf(st.extraInputs.getOrElse(0) { "" }, it) }, extraTeamColors[3], Modifier.weight(1f))
        }
        FullWidthGoldButton("سجّل الجولة", !st.gameOver) { st.recordCustomRound() }
    }
}

@Composable
private fun CustomField(name: String, value: String, onChange: (String) -> Unit, color: Color, modifier: Modifier) {
    val c = LocalHukmColors.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        HukmTextField(value, onChange, Modifier.fillMaxWidth(), borderColor = color.copy(alpha = 0.4f), fontSize = 18.sp)
    }
}

// ============================================================================
// Round log
// ============================================================================

@Composable
private fun RoundLog(st: ScoringState, modifier: Modifier = Modifier) {
    val c = LocalHukmColors.current
    Ltr {
        Box(
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(c.subtle)
                .border(1.dp, c.goldText.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
        ) {
            if (st.rounds.isEmpty()) {
                Text(
                    "أدخل النقاط واضغط «سجل»",
                    fontSize = 13.sp, color = c.goldText.copy(alpha = 0.25f),
                    modifier = Modifier.align(Alignment.Center).padding(8.dp)
                )
            } else {
                val reversed = st.rounds.reversed()
                LazyColumn(Modifier.fillMaxSize()) {
                    items(reversed) { r ->
                        RoundRow(r, c)
                        if (r.id != st.rounds.firstOrNull()?.id) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(c.goldText.copy(alpha = 0.08f)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundRow(r: Round, c: com.mohammedalkamali.hesabatalwaraqa.ui.theme.HukmColors) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${r.themTotal}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.teamThem)
                Text("+${r.themPoints}", fontSize = 10.sp, color = c.teamThem.copy(alpha = 0.55f))
            }
            Box(Modifier.width(1.dp).height(32.dp).background(c.goldText.copy(alpha = 0.18f)))
            Text("${r.number}", fontSize = 10.sp, color = c.goldText.copy(alpha = 0.2f), modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            Box(Modifier.width(1.dp).height(32.dp).background(c.goldText.copy(alpha = 0.18f)))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${r.usTotal}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.teamUs)
                Text("+${r.usPoints}", fontSize = 10.sp, color = c.teamUs.copy(alpha = 0.55f))
            }
        }
        if (r.note.isNotEmpty()) {
            Text(r.note, fontSize = 10.sp, color = c.goldText.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), textAlign = TextAlign.Center)
        }
    }
}

// ============================================================================
// Dealer indicator
// ============================================================================

@Composable
private fun DealerIndicator(angle: Float, onRotate: () -> Unit) {
    val c = LocalHukmColors.current
    val anim by animateFloatAsState(angle, spring(dampingRatio = 0.75f), label = "dealer")
    Box(
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(c.goldText.copy(alpha = 0.08f))
            .border(1.5.dp, c.goldText.copy(alpha = 0.30f), CircleShape)
            .clickable { onRotate() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.ArrowUpward, "الموزّع", tint = c.goldText, modifier = Modifier.size(16.dp).rotate(anim))
    }
}

// ============================================================================
// Bottom pill button
// ============================================================================

@Composable
private fun PillButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    faint: Boolean = false,
    onClick: () -> Unit,
) {
    val c = LocalHukmColors.current
    val fg = if (faint) c.goldText.copy(alpha = 0.75f) else c.goldText
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(c.goldText.copy(alpha = if (faint) 0.07f else 0.10f))
            .border(1.dp, c.goldText.copy(alpha = if (faint) 0.20f else 0.28f), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(14.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ============================================================================
// Settings overlay
// ============================================================================

@Composable
private fun SettingsOverlay(
    st: ScoringState,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onCustomSetup: () -> Unit,
) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .width(320.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(c.card)
                .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) { }
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Close, "إغلاق", tint = c.goldText, modifier = Modifier.size(20.dp).clickable { onClose() })
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.clickable { onBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = c.goldText.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                    Text("تغيير اللعبة", fontSize = 12.sp, color = c.goldText.copy(alpha = 0.8f))
                }
            }

            SettingsActionBtn("تراجع") { st.undo(); onClose() }
            SettingsActionBtn("لعبة جديدة") {
                if (st.usScore > st.themScore) st.usWins += 1
                else if (st.themScore > st.usScore) st.themWins += 1
                st.newGame(); onClose()
            }
            if (st.game == GameType.CUSTOM) {
                SettingsActionBtn("إعداد اللعبة المخصصة") { onCustomSetup() }
            }

            Divider(c)
            StepperRow("هدف الفوز: ${st.winTarget}", st.winTarget, 1..9999, { st.winTarget = it })
            if (st.game.hasZeroRule) {
                StepperRow("من الصفر: ${st.coffeeTarget}", st.coffeeTarget, 1..9999, { st.coffeeTarget = it })
            }

            if (st.game == GameType.HOKM) {
                Divider(c)
                Text("اللارنس", fontSize = 14.sp)
                SegmentedPicker(
                    listOf("لعبة جديدة + فوز" to "complete", "كاس + تكملة" to "cup"),
                    st.larnsMode, { st.larnsMode = it }, Modifier.fillMaxWidth().height(36.dp)
                )
                Text("الباوند", fontSize = 14.sp)
                SegmentedPicker(
                    listOf("١٨ نقطة" to "points", "كاس" to "cup"),
                    st.boundMode, { st.boundMode = it }, Modifier.fillMaxWidth().height(36.dp)
                )
            }
            if (st.game == GameType.HOKM_THABET) {
                Divider(c)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("زر اللارنس", fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    Switch(st.showThabetLarns, { st.showThabetLarns = it }, colors = SwitchDefaults.colors(checkedTrackColor = c.appGold))
                }
                if (st.showThabetLarns) {
                    Text("اللارنس", fontSize = 14.sp)
                    SegmentedPicker(
                        listOf("لعبة جديدة + فوز" to "complete", "كاس + تكملة" to "cup"),
                        st.larnsMode, { st.larnsMode = it }, Modifier.fillMaxWidth().height(36.dp)
                    )
                }
            }

            Divider(c)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("لنا: ${st.usWins}", fontSize = 12.sp, color = c.teamUs)
                Spacer(Modifier.weight(1f))
                Text("لهم: ${st.themWins}", fontSize = 12.sp, color = c.teamThem)
            }
        }
    }
}

@Composable
private fun Divider(c: com.mohammedalkamali.hesabatalwaraqa.ui.theme.HukmColors) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(c.goldText.copy(alpha = 0.15f)))
}

@Composable
private fun SettingsActionBtn(text: String, onClick: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.appGold)
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = c.navyDark)
    }
}

// ============================================================================
// Game over overlay
// ============================================================================

@Composable
private fun GameOverOverlay(st: ScoringState, onSettings: () -> Unit) {
    val c = LocalHukmColors.current
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(horizontal = 28.dp)
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(c.card)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🏆", fontSize = 50.sp)
            st.winner?.let { Text(it, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = c.goldText, textAlign = TextAlign.Center) }
            SettingsActionBtn("لعبة جديدة") {
                st.winnerIsUs?.let { if (it) st.usWins += 1 else st.themWins += 1 }
                st.newGame()
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.goldText.copy(alpha = 0.10f))
                    .border(1.dp, c.goldText.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { st.winner = null; st.winnerIsUs = null }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("أكمل", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = c.goldText)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.clickable { st.undo() }) {
                    Icon(Icons.AutoMirrored.Filled.Undo, null, tint = c.goldText.copy(alpha = 0.45f), modifier = Modifier.size(14.dp))
                    Text("تراجع", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.goldText.copy(alpha = 0.45f))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.clickable { onSettings() }) {
                    Icon(Icons.Filled.Tune, null, tint = c.goldText.copy(alpha = 0.45f), modifier = Modifier.size(14.dp))
                    Text("الإعدادات", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.goldText.copy(alpha = 0.45f))
                }
            }
        }
    }
}

// ============================================================================
// Bonus sheet (كبوت / مشاريع)
// ============================================================================

private data class Bonus(val key: String, val label: String, val sun: Int, val hokm: Int)

private val bonusTable = listOf(
    Bonus("sara", "سرا", 4, 2),
    Bonus("50", "خمسين", 10, 5),
    Bonus("100", "مية", 20, 10),
    Bonus("100ak", "أربع آكك (أربعمية)", 40, 10),
    Bonus("balot", "بلوت", 2, 2),
)

@Composable
private fun BonusSheet(title: String, withBase: Boolean, onRecord: (Int) -> Unit) {
    val c = LocalHukmColors.current
    var mode by remember { mutableStateOf<String?>(null) }   // "sun" | "hokm"
    var counts by remember { mutableStateOf(mapOf<String, Int>()) }

    val visible = bonusTable.filter { if (mode == "sun") it.key != "balot" else true }
    val bonusScore = visible.sumOf { b ->
        val pts = if (mode == "sun") b.sun else b.hokm
        pts * (counts[b.key] ?: 0)
    }
    val base = if (!withBase) 0 else if (mode == "sun") 44 else 25
    val total = base + bonusScore

    Rtl {
        Column(
            Modifier
                .background(c.card)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = c.goldText, modifier = Modifier.padding(top = 20.dp, bottom = 14.dp))
            Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("sun" to "صن", "hokm" to "حكم").forEach { (m, label) ->
                    val on = mode == m
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (on) c.appGold else c.goldText.copy(alpha = 0.10f))
                            .clickable { mode = m; counts = emptyMap() }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (on) c.navyDark else c.appGold)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            if (mode != null) {
                visible.forEach { b ->
                    val pts = if (mode == "sun") b.sun else b.hokm
                    val count = counts[b.key] ?: 0
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(b.label, fontSize = 14.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            if (count > 0) "+${pts * count}" else "+$pts",
                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = if (count > 0) c.appGold else c.goldText.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(10.dp))
                        RoundIconBtn("−") { if (count > 0) counts = counts + (b.key to count - 1) }
                        Text("$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        RoundIconBtn("+") { counts = counts + (b.key to count + 1) }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(c.goldText.copy(alpha = 0.1f)))
                }
                Box(
                    Modifier
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.appGold)
                        .clickable { onRecord(total) }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("سجل", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = c.navyDark)
                        Spacer(Modifier.weight(1f))
                        Text(
                            if (withBase || total > 0) "$total نقطة" else "اختر إضافات",
                            fontSize = 13.sp, color = c.navyDark
                        )
                    }
                }
            }
        }
    }
}
