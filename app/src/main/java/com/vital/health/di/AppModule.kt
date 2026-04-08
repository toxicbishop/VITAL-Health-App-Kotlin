package com.vital.health.di

import com.vital.health.BuildConfig
import android.content.Context
import androidx.room.*
import com.vital.health.data.local.HealthLogDao
import com.vital.health.data.local.VitalDatabase
import com.vital.health.data.remote.AuthManager
import com.vital.health.data.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabase(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VitalDatabase {
        return Room.databaseBuilder(
            context,
            VitalDatabase::class.java,
            "vital_db"
        ).build()
    }

    @Provides
    fun provideHealthDao(db: VitalDatabase): HealthLogDao = db.healthLogDao()

    @Provides
    @Singleton
    fun provideAuthManager(supabase: SupabaseClient): AuthManager = AuthManager(supabase)

    @Provides
    @Singleton
    fun provideRepository(dao: HealthLogDao, supabase: SupabaseClient): HealthRepository = HealthRepository(dao, supabase)
}
