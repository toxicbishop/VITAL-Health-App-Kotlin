package com.vital.health.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vital.health.util.currentTimeMillis
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@Entity(
    tableName = "health_logs",
    indices = [Index("userId")]
)
data class HealthLogEntity @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey val id: String = Uuid.random().toString(),
    val userId: String,
    val logType: String,
    val value: String,
    val unit: String,
    val notes: String?,
    val timestamp: Long = currentTimeMillis(),
    val isSynced: Boolean = false
)
