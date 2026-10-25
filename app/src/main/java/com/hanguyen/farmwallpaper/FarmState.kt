package com.hanguyen.farmwallpaper

import android.content.Context

/** Prototype state: one tree, identified only by when it was planted. */
object FarmState {
    const val ACTION_STATE_CHANGED = "com.hanguyen.farmwallpaper.STATE_CHANGED"
    const val GROW_MILLIS = 60_000L // demo: full growth in 1 minute

    private const val PREFS = "farm"
    private const val KEY_PLANTED_AT = "planted_at"

    fun plantedAt(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_PLANTED_AT, System.currentTimeMillis())
            .also { saveIfMissing(context, it) }

    fun plant(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_PLANTED_AT, System.currentTimeMillis()).apply()
        context.sendBroadcast(
            android.content.Intent(ACTION_STATE_CHANGED).setPackage(context.packageName)
        )
    }

    /** 0f (seed) .. 1f (fully grown). */
    fun growth(context: Context): Float =
        ((System.currentTimeMillis() - plantedAt(context)).toFloat() / GROW_MILLIS).coerceIn(0f, 1f)

    private fun saveIfMissing(context: Context, value: Long) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_PLANTED_AT)) prefs.edit().putLong(KEY_PLANTED_AT, value).apply()
    }
}
