package com.vital.health.util

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual class PlatformSettings : KoinComponent {
    private val context: Context by inject()
    private val prefs = context.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }
}
