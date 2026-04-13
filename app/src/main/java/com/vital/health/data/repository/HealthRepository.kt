package com.vital.health.data.repository

import com.vital.health.data.local.HealthLogDao
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.data.remote.AuthManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SyncResult {
    data object Success : SyncResult
    data class Failure(val message: String) : SyncResult
    data object NotSignedIn : SyncResult
}

@Singleton
class HealthRepository @Inject constructor(
    private val localDao: HealthLogDao,
    private val supabase: SupabaseClient,
    private val authManager: AuthManager
) {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allLogs: Flow<List<HealthLogEntity>> = authManager.sessionStatus
        .map { authManager.currentUserId() }
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else localDao.getAllLogs(uid)
        }

    suspend fun addLog(type: String, value: String, unit: String, notes: String?): SyncResult {
        val uid = authManager.currentUserId() ?: return SyncResult.NotSignedIn
        localDao.insertLog(
            HealthLogEntity(
                userId = uid,
                logType = type,
                value = value,
                unit = unit,
                notes = notes
            )
        )
        return SyncResult.Success
    }

    suspend fun syncWithRemote(): SyncResult = pushUnsynced()

    suspend fun backupToSupabase(): SyncResult = pushUnsynced()

    suspend fun restoreFromSupabase(): SyncResult {
        val uid = authManager.currentUserId() ?: return SyncResult.NotSignedIn
        return try {
            val remoteLogs = supabase.postgrest["health_logs"]
                .select {
                    filter { eq("userId", uid) }
                }
                .decodeList<HealthLogEntity>()
            remoteLogs.forEach { log ->
                localDao.insertLog(log.copy(userId = uid, isSynced = true))
            }
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Failure(e.message ?: "Restore failed")
        }
    }

    private suspend fun pushUnsynced(): SyncResult {
        val uid = authManager.currentUserId() ?: return SyncResult.NotSignedIn
        val unsynced = localDao.getUnsyncedLogs(uid)
        var firstError: String? = null
        unsynced.forEach { log ->
            try {
                supabase.postgrest["health_logs"].insert(log)
                localDao.markAsSynced(log.id)
            } catch (e: Exception) {
                if (firstError == null) firstError = e.message ?: "Sync failed"
            }
        }
        return firstError?.let { SyncResult.Failure(it) } ?: SyncResult.Success
    }
}
