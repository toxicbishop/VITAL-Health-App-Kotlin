package com.vital.health

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

private fun loadConfig(key: String): String {
    return readEnvironmentValue(key)
        ?: readInfoPlistValue(key)
        ?: error("Missing required iOS configuration value: $key")
}

private fun readEnvironmentValue(key: String): String? {
    return NSProcessInfo.processInfo.environment[key] as? String
}

private fun readInfoPlistValue(key: String): String? {
    return NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String
}

actual object Config {
    actual val SUPABASE_URL: String = loadConfig("SUPABASE_URL")
    actual val SUPABASE_KEY: String = loadConfig("SUPABASE_KEY")
}
