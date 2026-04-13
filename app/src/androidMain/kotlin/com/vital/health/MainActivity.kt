package com.vital.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vital.health.ui.theme.isAppDarkMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("vital_prefs", android.content.Context.MODE_PRIVATE)
        isAppDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        
        val onboardingDoneStored = sharedPrefs.getBoolean("onboarding_done", false)

        setContent {
            App(
                onboardingDoneStored = onboardingDoneStored,
                onSaveOnboarding = { done ->
                    sharedPrefs.edit().putBoolean("onboarding_done", done).apply()
                }
            )
        }
    }
}
