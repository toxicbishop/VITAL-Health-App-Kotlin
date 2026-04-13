package com.vital.health

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Medication & vitals reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "vital_reminders"
    }
}
