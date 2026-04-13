package com.vital.health.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection

@Database(entities = [HealthLogEntity::class], version = 2, exportSchema = false)
@ConstructedBy(VitalDatabaseConstructor::class)
abstract class VitalDatabase : RoomDatabase() {
    abstract fun healthLogDao(): HealthLogDao
}

// The following is required for Room KMP
expect object VitalDatabaseConstructor : RoomDatabaseConstructor<VitalDatabase>

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.prepare("ALTER TABLE health_logs ADD COLUMN userId TEXT NOT NULL DEFAULT ''").use { it.step() }
        connection.prepare("CREATE INDEX index_health_logs_userId ON health_logs (userId)").use { it.step() }
    }
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<VitalDatabase>
): VitalDatabase {
    return builder
        .addMigrations(MIGRATION_1_2)
        .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
        .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
        .build()
}
