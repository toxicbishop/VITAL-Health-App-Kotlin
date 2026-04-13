package com.vital.health

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App(
        onboardingDoneStored = false, // TODO: Implement persistent settings for iOS
        onSaveOnboarding = { }
    )
}
