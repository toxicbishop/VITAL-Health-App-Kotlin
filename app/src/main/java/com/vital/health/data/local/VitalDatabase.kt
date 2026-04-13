package com.vital.health.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [HealthLogEntity::class], version = 2, exportSchema = false)
abstract class VitalDatabase : RoomDatabase() {
    abstract fun healthLogDao(): HealthLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE health_logs ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_health_logs_userId ON health_logs(userId)")
            }
        }
    }
}
