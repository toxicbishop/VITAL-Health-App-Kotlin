package com.vital.health.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory



fun getDatabaseBuilder(): RoomDatabase.Builder<VitalDatabase> {
    val dbFile = NSHomeDirectory() + "/vital_db.db"
    return Room.databaseBuilder<VitalDatabase>(
        name = dbFile,
        factory = { VitalDatabaseConstructor.initialize() }
    )
}
