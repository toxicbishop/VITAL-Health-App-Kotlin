package com.vital.health

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vital.health.ui.screens.AuthScreen
import com.vital.health.ui.screens.DashboardScreen
import com.vital.health.ui.screens.OnboardingScreen
import com.vital.health.ui.theme.VitalTheme
import com.vital.health.ui.viewmodels.AuthViewModel
import com.vital.health.ui.viewmodels.HealthViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(
    onboardingDoneStored: Boolean,
    onSaveOnboarding: (Boolean) -> Unit
) {
    VitalTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val authViewModel: AuthViewModel = koinViewModel()
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val isLoading by authViewModel.isLoading.collectAsState()
            val errorMessage by authViewModel.errorMessage.collectAsState()
            
            var onboardingDone by remember { mutableStateOf(onboardingDoneStored) }

            if (isLoggedIn) {
                val viewModel: HealthViewModel = koinViewModel()
                val logs by viewModel.healthLogs.collectAsState()
                val userName by authViewModel.userName.collectAsState()
                val userEmail by authViewModel.userEmail.collectAsState()
                val userAvatarUrl by authViewModel.userAvatarUrl.collectAsState()

                DashboardScreen(
                    logs = logs,
                    userName = userName,
                    userEmail = userEmail ?: "",
                    userAvatarUrl = userAvatarUrl,
                    onSaveProfile = { newName, photoBytes ->
                        authViewModel.updateProfile(newName, photoBytes)
                    },
                    onAddLog = { type, value, unit, notes ->
                        viewModel.addLog(type, value, unit, notes)
                    },
                    onSync = { viewModel.sync() },
                    onBackup = { viewModel.backup() },
                    onRestore = { viewModel.restore() },
                    onLogout = { authViewModel.logout() }
                )
            } else if (!onboardingDone) {
                OnboardingScreen(
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onComplete = { email, password, name ->
                        authViewModel.signUpAndSetProfile(email, password, name) {
                            onSaveOnboarding(true)
                            onboardingDone = true
                        }
                    }
                )
            } else {
                AuthScreen(
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onLogin = { email, pass -> authViewModel.login(email, pass) },
                    onSignUp = { email, pass -> authViewModel.signUp(email, pass) }
                )
            }
        }
    }
}
