package com.mohammedalkamali.hesabatalwaraqa.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Reactive wrapper over SharedPreferences — the @AppStorage equivalent. */
object AppPrefs {
    private const val PREFS = "hukm_prefs"
    private lateinit var sp: android.content.SharedPreferences

    fun init(context: Context) {
        if (::sp.isInitialized) return
        sp = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        soundEnabled = sp.getBoolean("soundEnabled", true)
        showTime = sp.getBoolean("showTime", false)
        totalGamesCompleted = sp.getInt("totalGamesCompleted", 0)
        settingsHintShown = sp.getBoolean("settingsHintShown", false)
        appTheme = sp.getString("appTheme", "system") ?: "system"
    }

    var soundEnabled by mutableStateOf(true)
        private set
    var showTime by mutableStateOf(false)
        private set
    var totalGamesCompleted by mutableStateOf(0)
        private set
    var settingsHintShown by mutableStateOf(false)
        private set
    var appTheme by mutableStateOf("system")
        private set

    fun setSoundEnabled(v: Boolean) { soundEnabled = v; sp.edit().putBoolean("soundEnabled", v).apply() }
    fun setShowTime(v: Boolean) { showTime = v; sp.edit().putBoolean("showTime", v).apply() }
    fun setTotalGamesCompleted(v: Int) { totalGamesCompleted = v; sp.edit().putInt("totalGamesCompleted", v).apply() }
    fun setSettingsHintShown(v: Boolean) { settingsHintShown = v; sp.edit().putBoolean("settingsHintShown", v).apply() }
    fun setAppTheme(v: String) { appTheme = v; sp.edit().putString("appTheme", v).apply() }
}
