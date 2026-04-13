package com.vital.health.util
import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
actual class PlatformSettings actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults
    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        // NSUserDefaults returns false if key doesn't exist, we must check for existence if we want a different default
        return if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else defaultValue
    }
    actual fun putString(key: String, value: String) {
        defaults.setObject(value, key)
    }
    actual fun getString(key: String, defaultValue: String?): String? {
        return defaults.stringForKey(key) ?: defaultValue
    }
}