package com.vital.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital.health.data.remote.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class AuthViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authManager.sessionStatus
        .map { it is SessionStatus.Authenticated }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authManager.isAuthenticated()
        )

    val userEmail: StateFlow<String?> = authManager.sessionStatus
        .map { authManager.currentUserEmail() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authManager.currentUserEmail())

    val userName: StateFlow<String> = authManager.sessionStatus
        .map { deriveName() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), deriveName())

    val userAvatarUrl: StateFlow<String?> = authManager.sessionStatus
        .map { authManager.currentUserAvatar() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authManager.currentUserAvatar())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private fun deriveName(): String =
        authManager.currentUserName()
            ?: authManager.currentUserEmail()?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: "User"

    fun updateProfile(name: String, photoBytes: ByteArray?) {
        viewModelScope.launch {
            try {
                val avatarUrl = photoBytes?.let { authManager.uploadAvatar(it) }
                authManager.updateProfile(name, avatarUrl)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authManager.login(email, pass)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authManager.signUp(email, pass)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Sign up failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUpAndSetProfile(
        email: String,
        pass: String,
        name: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authManager.signUp(email, pass)
                if (authManager.isAuthenticated()) {
                    authManager.updateProfile(name, null)
                    onSuccess()
                } else {
                    _errorMessage.value = "Check your email to confirm the account, then sign in."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Sign up failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authManager.logout()
            } catch (e: Exception) {
                _errorMessage.value = "Logout failed"
            }
        }
    }
}
