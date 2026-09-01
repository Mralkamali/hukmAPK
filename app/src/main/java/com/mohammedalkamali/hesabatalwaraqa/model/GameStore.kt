package com.mohammedalkamali.hesabatalwaraqa.model

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Mirrors the Swift GameStore singleton — one JSON blob in SharedPreferences. */
object GameStore {
    private const val PREFS = "hukm_prefs"
    private const val KEY = "hukmGameStates_v1"

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private var states: MutableMap<String, PersistedGameState> = mutableMapOf()
    private var loaded = false

    private fun prefs(ctx: Context) =
        ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun ensureLoaded(ctx: Context) {
        if (loaded) return
        loaded = true
        val raw = prefs(ctx).getString(KEY, null) ?: return
        runCatching {
            states = json.decodeFromString<Map<String, PersistedGameState>>(raw).toMutableMap()
        }
    }

    fun load(ctx: Context, game: GameType): PersistedGameState? {
        ensureLoaded(ctx)
        return states[game.storeKey]
    }

    fun save(ctx: Context, state: PersistedGameState, game: GameType) {
        ensureLoaded(ctx)
        states[game.storeKey] = state
        runCatching {
            val text = json.encodeToString<Map<String, PersistedGameState>>(states)
            prefs(ctx).edit().putString(KEY, text).apply()
        }
    }
}
