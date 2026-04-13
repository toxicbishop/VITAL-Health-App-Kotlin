package com.vital.health

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.GlobalContext
import platform.UIKit.UIViewController
import com.vital.health.di.initKoin
import com.vital.health.util.PlatformSettings

fun MainViewController(): UIViewController {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }

    val settings = PlatformSettings()
    val onboardingDone = settings.getBoolean("onboarding_done", false)

    return ComposeUIViewController {
        App(
            onboardingDoneStored = onboardingDone,
            onSaveOnboarding = { done ->
                settings.putBoolean("onboarding_done", done)
            }
        )
    }
}
