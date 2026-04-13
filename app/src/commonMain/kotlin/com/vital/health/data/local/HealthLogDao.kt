package com.vital.health.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllLogs(userId: String): Flow<List<HealthLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HealthLogEntity)

    @Query("SELECT * FROM health_logs WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedLogs(userId: String): List<HealthLogEntity>

    @Query("UPDATE health_logs SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
