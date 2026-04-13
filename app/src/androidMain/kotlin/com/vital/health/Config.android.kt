package com.vital.health

actual object Config {
    actual val SUPABASE_URL: String = readConfigValue("SUPABASE_URL")
    actual val SUPABASE_KEY: String = readConfigValue("SUPABASE_KEY")

    private fun readConfigValue(fieldName: String): String {
        val value = runCatching {
            val buildConfigClass = Class.forName("com.vital.health.BuildConfig")
            buildConfigClass.getField(fieldName).get(null) as? String
        }.getOrNull()

        return value?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Missing Android config for '$fieldName'. Provide it via generated BuildConfig fields or another injected configuration source."
            )
    }
}
