package com.vital.health

actual object Config {
    actual val SUPABASE_URL: String = readConfigValue("SUPABASE_URL")
    actual val SUPABASE_KEY: String = readConfigValue("SUPABASE_KEY")

    private fun readConfigValue(fieldName: String): String {
        val value = when (fieldName) {
            "SUPABASE_URL" -> BuildConfig.SUPABASE_URL
            "SUPABASE_KEY" -> BuildConfig.SUPABASE_KEY
            else -> throw IllegalArgumentException("Unsupported Android config field '$fieldName'")
        }

        return value.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Missing Android config for '$fieldName'. Provide it via generated BuildConfig fields or another injected configuration source."
            )
    }
}
