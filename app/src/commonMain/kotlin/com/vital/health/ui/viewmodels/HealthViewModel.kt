package com.vital.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.data.repository.HealthRepository
import com.vital.health.data.repository.SyncResult

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class HealthViewModel(
    private val repository: HealthRepository
) : ViewModel() {

    val healthLogs: StateFlow<List<HealthLogEntity>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    fun clearStatus() { _status.value = null }

    fun addLog(type: String, value: String, unit: String, notes: String? = null) {
        viewModelScope.launch {
            report(repository.addLog(type, value, unit, notes), "Saved")
        }
    }

    fun sync() {
        viewModelScope.launch { report(repository.syncWithRemote(), "Synced") }
    }

    fun backup() {
        viewModelScope.launch { report(repository.backupToSupabase(), "Backup complete") }
    }

    fun restore() {
        viewModelScope.launch { report(repository.restoreFromSupabase(), "Restore complete") }
    }

    private fun report(result: SyncResult, successMessage: String) {
        _status.value = when (result) {
            SyncResult.Success -> successMessage
            SyncResult.NotSignedIn -> "Sign in to continue"
            is SyncResult.Failure -> result.message
        }
    }
}
