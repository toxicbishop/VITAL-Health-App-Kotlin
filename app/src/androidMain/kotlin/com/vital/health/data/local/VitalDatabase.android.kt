package com.vital.health.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase



fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<VitalDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("vital_db")
    return Room.databaseBuilder<VitalDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
