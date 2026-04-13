package com.vital.health.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(
    tableName = "health_logs",
    indices = [Index("userId")]
)
data class HealthLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val logType: String,
    val value: String,
    val unit: String,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
