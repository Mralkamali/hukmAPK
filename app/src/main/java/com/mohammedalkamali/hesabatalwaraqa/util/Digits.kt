package com.mohammedalkamali.hesabatalwaraqa.util

/** Accepts Eastern-Arabic (٠-٩), Persian (۰-۹) and Latin digits — matches Swift toInt/parseAnyDigits. */
fun normalizeDigits(text: String): String = buildString {
    for (ch in text) {
        val v = ch.code
        when {
            v in 0x0660..0x0669 -> append(('0' + (v - 0x0660)))
            v in 0x06F0..0x06F9 -> append(('0' + (v - 0x06F0)))
            else -> append(ch)
        }
    }
}

fun toIntFlexible(text: String): Int = normalizeDigits(text.trim()).toIntOrNull() ?: 0

fun parseAnyDigits(text: String): Int? = normalizeDigits(text.trim()).toIntOrNull()
