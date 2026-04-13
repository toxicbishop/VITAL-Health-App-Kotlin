package com.vital.health.di

import com.vital.health.Config
import com.vital.health.data.local.HealthLogDao
import com.vital.health.data.local.VitalDatabase
import com.vital.health.data.local.getRoomDatabase
import com.vital.health.data.remote.AuthManager
import com.vital.health.data.repository.HealthRepository
import com.vital.health.ui.viewmodels.AuthViewModel
import com.vital.health.ui.viewmodels.HealthViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule)
    }

// Called by iOS
fun initKoin() = initKoin {}

expect val platformModule: Module

val commonModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = Config.SUPABASE_URL,
            supabaseKey = Config.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }

    single<VitalDatabase> { getRoomDatabase(get()) }
    single<HealthLogDao> { get<VitalDatabase>().healthLogDao() }
    
    singleOf(::AuthManager)
    singleOf(::HealthRepository)
    
    factoryOf(::AuthViewModel)
    factoryOf(::HealthViewModel)
}
