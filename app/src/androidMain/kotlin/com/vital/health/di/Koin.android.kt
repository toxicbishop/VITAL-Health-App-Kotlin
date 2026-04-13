package com.vital.health.di

import com.vital.health.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual val platformModule = module {
    single { getDatabaseBuilder(get()) }
}
