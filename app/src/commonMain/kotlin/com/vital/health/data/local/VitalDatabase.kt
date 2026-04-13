package com.vital.health.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [HealthLogEntity::class], version = 2, exportSchema = false)
@ConstructedBy(VitalDatabaseConstructor::class)
abstract class VitalDatabase : RoomDatabase() {
    abstract fun healthLogDao(): HealthLogDao
}

// The following is required for Room KMP
expect object VitalDatabaseConstructor : RoomDatabaseConstructor<VitalDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<VitalDatabase>
): VitalDatabase {
    return builder
        .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
        .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
        .build()
}
