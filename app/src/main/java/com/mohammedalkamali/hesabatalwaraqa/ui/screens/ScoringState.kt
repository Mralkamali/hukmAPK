package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mohammedalkamali.hesabatalwaraqa.model.GameStore
import com.mohammedalkamali.hesabatalwaraqa.model.GameType
import com.mohammedalkamali.hesabatalwaraqa.model.PersistedGameState
import com.mohammedalkamali.hesabatalwaraqa.model.Round
import com.mohammedalkamali.hesabatalwaraqa.util.AppPrefs
import com.mohammedalkamali.hesabatalwaraqa.util.Ticker
import com.mohammedalkamali.hesabatalwaraqa.util.toIntFlexible
import kotlin.math.abs

/** Port of SwiftUI `ScoringView` state + scoring logic. */
class ScoringState(private val ctx: Context, val game: GameType) {

    private val loaded = GameStore.load(ctx, game)

    var usScore by mutableIntStateOf(loaded?.usScore ?: 0)
    var themScore by mutableIntStateOf(loaded?.themScore ?: 0)
    var usInput by mutableStateOf("")
    var themInput by mutableStateOf("")
    val rounds = mutableStateListOf<Round>().apply { loaded?.rounds?.let { addAll(it) } }
    var winner by mutableStateOf(loaded?.winner)
    var winnerIsUs by mutableStateOf<Boolean?>(null)

    var winTarget by mutableIntStateOf(loaded?.winTarget ?: game.defaultWinTarget)
    var coffeeTarget by mutableIntStateOf(loaded?.coffeeTarget ?: game.defaultZeroTarget)

    var customTeamCount by mutableIntStateOf(loaded?.customTeamCount ?: 2)
    var customTeamNames by mutableStateOf(
        loaded?.customTeamNames ?: listOf("الفريق ١", "الفريق ٢", "الفريق ٣", "الفريق ٤")
    )
    var extraScores by mutableStateOf(loaded?.customExtraScores ?: listOf(0, 0))
    var extraInputs by mutableStateOf(listOf("", ""))
    var customLowLoses by mutableStateOf(loaded?.customLowLoses ?: false)

    var larnsMode by mutableStateOf(loaded?.larnsMode ?: "complete")
    var boundMode by mutableStateOf(loaded?.boundMode ?: "points")
    var usWins by mutableIntStateOf(loaded?.usWins ?: 0)
    var themWins by mutableIntStateOf(loaded?.themWins ?: 0)
    var showThabetLarns by mutableStateOf(loaded?.showThabetLarns ?: false)

    var tarneebBuyerIsUs by mutableStateOf(true)
    var tarneebBid by mutableIntStateOf(7)
    var tarneebTricks by mutableIntStateOf(7)

    /** set from the composable so milestones can trigger the Play in-app review. */
    var onMilestone: (() -> Unit)? = null

    val gameOver: Boolean get() = winner != null
    val lowLoses: Boolean get() = game == GameType.CUSTOM && customLowLoses

    val startCustomSetup: Boolean =
        game == GameType.CUSTOM && (loaded == null || loaded.rounds.isEmpty())
    val startThabetSetup: Boolean =
        game == GameType.HOKM_THABET && (loaded == null || loaded.rounds.isEmpty()) && loaded?.winner == null

    fun persist() {
        GameStore.save(
            ctx,
            PersistedGameState(
                usScore = usScore, themScore = themScore,
                rounds = rounds.toList(), winner = winner,
                winTarget = winTarget, coffeeTarget = coffeeTarget,
                customTeamCount = if (game == GameType.CUSTOM) customTeamCount else null,
                customTeamNames = if (game == GameType.CUSTOM) customTeamNames else null,
                customExtraScores = if (game == GameType.CUSTOM) extraScores else null,
                customLowLoses = if (game == GameType.CUSTOM) customLowLoses else null,
                usWins = usWins, themWins = themWins,
                larnsMode = larnsMode, boundMode = boundMode,
                showThabetLarns = if (game == GameType.HOKM_THABET) showThabetLarns else null
            ),
            game
        )
    }

    private fun tick() {
        if (AppPrefs.soundEnabled) Ticker.tick()
    }

    // MARK: core

    fun addRound(up: Int, tp: Int, note: String = "") {
        usScore += up
        themScore += tp
        rounds.add(
            Round(
                number = rounds.size + 1,
                usPoints = up, themPoints = tp,
                usTotal = usScore, themTotal = themScore, note = note
            )
        )
        tick()
        checkWinner()
    }

    fun checkWinner() {
        when {
            usScore >= winTarget -> declareWinner("لنا", true)
            themScore >= winTarget -> declareWinner("لهم", false)
            game.hasZeroRule && usScore >= coffeeTarget && themScore == 0 ->
                declareWinner("لنا (من الصفر)", true)
            game.hasZeroRule && themScore >= coffeeTarget && usScore == 0 ->
                declareWinner("لهم (من الصفر)", false)
        }
    }

    fun declareWinner(name: String, isUs: Boolean) {
        winner = name
        winnerIsUs = isUs
        AppPrefs.setTotalGamesCompleted(AppPrefs.totalGamesCompleted + 1)
        if (AppPrefs.totalGamesCompleted in intArrayOf(3, 8, 20, 50)) {
            onMilestone?.invoke()
        }
    }

    fun undo() {
        if (rounds.isEmpty()) return
        rounds.removeAt(rounds.lastIndex)
        usScore = rounds.lastOrNull()?.usTotal ?: 0
        themScore = rounds.lastOrNull()?.themTotal ?: 0
        winner = null
        winnerIsUs = null
    }

    fun newGame() {
        usScore = 0; themScore = 0; usInput = ""; themInput = ""
        rounds.clear(); winner = null; winnerIsUs = null
        extraScores = List(extraScores.size) { 0 }
        extraInputs = listOf("", "")
    }

    // MARK: standard (حكم / بلوت)

    fun record() {
        val up = toIntFlexible(usInput)
        val tp = toIntFlexible(themInput)
        if (up == 0 && tp == 0) return
        addRound(up, tp)
        usInput = ""; themInput = ""
    }

    fun kabotFor(isUs: Boolean, pts: Int) {
        if (isUs) addRound(pts, 0, "كبوت لنا") else addRound(0, pts, "كبوت لهم")
        usInput = ""; themInput = ""
    }

    fun mashariFor(isUs: Boolean, pts: Int) {
        if (isUs) addRound(pts, 0, "مشاريع لنا") else addRound(0, pts, "مشاريع لهم")
        usInput = ""; themInput = ""
    }

    // MARK: hokm larns / bound

    fun declareLarns(isUs: Boolean) {
        if (gameOver) return
        if (larnsMode == "cup") {
            if (isUs) usWins += 1 else themWins += 1
            rounds.add(
                Round(
                    number = rounds.size + 1, usPoints = 0, themPoints = 0,
                    usTotal = usScore, themTotal = themScore, note = "لارنس ♛ (كاس)"
                )
            )
            tick()
            return
        }
        val prevUs = usScore
        val prevThem = themScore
        if (isUs) usScore = maxOf(usScore, winTarget) else themScore = maxOf(themScore, winTarget)
        rounds.add(
            Round(
                number = rounds.size + 1,
                usPoints = usScore - prevUs, themPoints = themScore - prevThem,
                usTotal = usScore, themTotal = themScore, note = "لارنس ♛"
            )
        )
        tick()
        declareWinner(if (isUs) "لنا (لارنس)" else "لهم (لارنس)", isUs)
    }

    fun declareBound(isUs: Boolean) {
        if (gameOver) return
        if (boundMode == "cup") {
            if (isUs) usWins += 1 else themWins += 1
            rounds.add(
                Round(
                    number = rounds.size + 1, usPoints = 0, themPoints = 0,
                    usTotal = usScore, themTotal = themScore, note = "باوند (كاس)"
                )
            )
            tick()
        } else {
            addRound(if (isUs) 18 else 0, if (isUs) 0 else 18, "باوند")
        }
    }

    // MARK: حكم ثابت

    fun recordHokmThabet(usWon: Boolean, themWon: Boolean) {
        val tPts = toIntFlexible(themInput) * 10
        val uPts = toIntFlexible(usInput) * 10
        if (tPts <= 0 && uPts <= 0) return
        val prevUs = usScore
        val prevThem = themScore
        themScore += if (themWon) tPts else -tPts
        usScore += if (usWon) uPts else -uPts
        val note = when {
            usWon && themWon -> "الكل فاز"
            !usWon && !themWon -> "الكل خسر"
            else -> ""
        }
        rounds.add(
            Round(
                number = rounds.size + 1,
                usPoints = usScore - prevUs, themPoints = themScore - prevThem,
                usTotal = usScore, themTotal = themScore, note = note
            )
        )
        usInput = ""; themInput = ""
        tick()
        when {
            usScore >= winTarget && themScore >= winTarget -> {
                val isUs = usScore >= themScore
                declareWinner(if (isUs) "لنا" else "لهم", isUs)
            }
            usScore >= winTarget -> declareWinner("لنا", true)
            themScore >= winTarget -> declareWinner("لهم", false)
        }
    }

    // MARK: طرنيب

    val tarneebPreview: String
        get() {
            val bid = tarneebBid
            val bt = tarneebTricks
            val dt = 13 - bt
            val buyer = if (tarneebBuyerIsUs) "لنا" else "لهم"
            val def = if (tarneebBuyerIsUs) "لهم" else "لنا"
            if (bt == 13) {
                val b = if (bid == 13) TARNEEB_KABOUT_BID else TARNEEB_KABOUT_SILENT
                return "$buyer كبوت +$b"
            }
            if (dt == 13) return "$def كبوت +$TARNEEB_DEFENSE_SWEEP  •  $buyer −$bid"
            if (bt >= bid) return "$buyer +$bt  •  $def +$dt"
            return "$buyer فشل −$bid  •  $def +$dt"
        }

    fun recordTarneeb() {
        val bid = tarneebBid
        val bt = tarneebTricks
        val dt = 13 - bt
        var buyerDelta: Int
        var defDelta = 0
        val tag: String
        when {
            bt == 13 -> {
                buyerDelta = if (bid == 13) TARNEEB_KABOUT_BID else TARNEEB_KABOUT_SILENT
                tag = "شراء $bid — كبوت"
            }
            dt == 13 -> {
                buyerDelta = -bid
                defDelta = TARNEEB_DEFENSE_SWEEP
                tag = "شراء $bid — كبوت على المشتري"
            }
            bt >= bid -> {
                buyerDelta = bt
                defDelta = dt
                tag = "شراء $bid — نجح"
            }
            else -> {
                buyerDelta = -bid
                defDelta = dt
                tag = "شراء $bid — فشل"
            }
        }
        val up = if (tarneebBuyerIsUs) buyerDelta else defDelta
        val tp = if (tarneebBuyerIsUs) defDelta else buyerDelta
        addRound(up, tp, (if (tarneebBuyerIsUs) "لنا • " else "لهم • ") + tag)
        tarneebBid = 7
        tarneebTricks = 7
    }

    // MARK: مخصص

    fun recordCustomRound() {
        val up = toIntFlexible(usInput)
        val tp = toIntFlexible(themInput)
        val e0 = if (customTeamCount > 2) toIntFlexible(extraInputs.getOrElse(0) { "" }) else 0
        val e1 = if (customTeamCount > 3) toIntFlexible(extraInputs.getOrElse(1) { "" }) else 0
        if (up == 0 && tp == 0 && e0 == 0 && e1 == 0) return

        usScore += up
        themScore += tp
        val newExtra = extraScores.toMutableList()
        while (newExtra.size < 2) newExtra.add(0)
        if (customTeamCount > 2) newExtra[0] = newExtra[0] + e0
        if (customTeamCount > 3) newExtra[1] = newExtra[1] + e1
        extraScores = newExtra

        val extras = listOf(e0, e1).take(customTeamCount - 2)
        val extraTotals = extraScores.take(customTeamCount - 2)
        rounds.add(
            Round(
                number = rounds.size + 1,
                usPoints = up, themPoints = tp,
                usTotal = usScore, themTotal = themScore,
                extraPoints = extras, extraTotals = extraTotals
            )
        )
        usInput = ""; themInput = ""
        extraInputs = listOf("", "")
        tick()

        val allScores = listOf(usScore, themScore) + extraScores.take(customTeamCount - 2)
        val reached = allScores.indexOfFirst { it >= winTarget }
        if (reached >= 0) {
            val winnerIdx = if (customLowLoses) {
                allScores.indices.minByOrNull { allScores[it] } ?: 0
            } else reached
            declareWinner(customTeamNames.getOrElse(winnerIdx) { "الفريق ${winnerIdx + 1}" }, winnerIdx == 0)
        }
    }

    // MARK: wins reset

    fun resetWins() {
        usWins = 0; themWins = 0
    }

    fun diff(): Int = abs(usScore - themScore)

    companion object {
        const val TARNEEB_KABOUT_BID = 25
        const val TARNEEB_KABOUT_SILENT = 16
        const val TARNEEB_DEFENSE_SWEEP = 16
    }
}
