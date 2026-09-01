package com.mohammedalkamali.hesabatalwaraqa.util

import android.media.AudioManager
import android.media.ToneGenerator

/** Short tick played on each recorded round — analogue of AudioServicesPlaySystemSound(1104). */
object Ticker {
    private val tone by lazy { ToneGenerator(AudioManager.STREAM_SYSTEM, 70) }

    fun tick() {
        runCatching { tone.startTone(ToneGenerator.TONE_PROP_BEEP, 90) }
    }
}
