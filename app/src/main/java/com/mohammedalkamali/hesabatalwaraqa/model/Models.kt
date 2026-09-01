package com.mohammedalkamali.hesabatalwaraqa.model

import androidx.compose.ui.graphics.Color
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.AppGold
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.TeamUs
import kotlinx.serialization.Serializable
import java.util.UUID

enum class WinMode { HIGH_WINS, LOW_LOSES, WINS_COUNT }

enum class GameType(val title: String) {
    HOKM("كوت (حكم متحرك)"),
    HOKM_THABET("حكم ثابت"),
    BALOOT("بلوت"),
    TARNEEB("طرنيب"),
    CUSTOM("مخصص");

    val defaultWinTarget: Int
        get() = when (this) {
            HOKM -> 54
            HOKM_THABET -> 500
            BALOOT -> 152
            TARNEEB -> 61
            CUSTOM -> 100
        }

    val defaultZeroTarget: Int
        get() = if (this == HOKM) 36 else 0

    val hasZeroRule: Boolean get() = defaultZeroTarget > 0

    val winMode: WinMode get() = WinMode.HIGH_WINS

    val suits: String
        get() = when (this) {
            HOKM -> "♣ ♠"
            HOKM_THABET -> "♠ ★"
            BALOOT -> "♠ ♥"
            TARNEEB -> "♥ ♠"
            CUSTOM -> "★ ✦"
        }

    val ruleText: String
        get() = when (this) {
            HOKM -> "اوصل ٥٤ • من الصفر ٣٦"
            HOKM_THABET -> "نقاط × ١٠ • فوز ولا خسارة"
            BALOOT -> "اوصل ١٥٢ • من الصفر ١٠١"
            TARNEEB -> "اختر المشتري والأكلات • اوصل ٦١"
            CUSTOM -> "لعبة مخصصة — اضبط القوانين"
        }

    val accentColor: Color
        get() = when (this) {
            HOKM -> TeamUs
            HOKM_THABET -> Color(0xFFF2C72E)
            BALOOT -> AppGold
            TARNEEB -> Color(0xFF33B88C)
            CUSTOM -> Color(0xFFB380F2)
        }

    /** stable key used for persistence (matches Swift rawValue) */
    val storeKey: String get() = title
}

@Serializable
data class Round(
    val id: String = UUID.randomUUID().toString(),
    val number: Int,
    val usPoints: Int,
    val themPoints: Int,
    val usTotal: Int,
    val themTotal: Int,
    val note: String = "",
    val extraPoints: List<Int> = emptyList(),
    val extraTotals: List<Int> = emptyList(),
)

@Serializable
data class PersistedGameState(
    val usScore: Int,
    val themScore: Int,
    val rounds: List<Round>,
    val winner: String? = null,
    val winTarget: Int,
    val coffeeTarget: Int,
    val customTeamCount: Int? = null,
    val customTeamNames: List<String>? = null,
    val customExtraScores: List<Int>? = null,
    val customLowLoses: Boolean? = null,
    val usWins: Int? = null,
    val themWins: Int? = null,
    val larnsMode: String? = null,
    val boundMode: String? = null,
    val showThabetLarns: Boolean? = null,
)
