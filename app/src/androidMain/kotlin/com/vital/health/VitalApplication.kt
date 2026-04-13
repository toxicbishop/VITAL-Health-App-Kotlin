package com.vital.health

import android.app.Application
import com.vital.health.di.initKoin
import org.koin.android.ext.koin.androidContext

class VitalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@VitalApplication)
        }
    }
}
