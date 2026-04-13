package com.vital.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vital.health.ui.theme.isAppDarkMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val settings = com.vital.health.util.PlatformSettings()
        isAppDarkMode = settings.getBoolean("dark_mode", false)
        
        val onboardingDoneStored = settings.getBoolean("onboarding_done", false)

        setContent {
            App(
                onboardingDoneStored = onboardingDoneStored,
                onSaveOnboarding = { done ->
                    settings.putBoolean("onboarding_done", done)
                }
            )
        }
    }
}
