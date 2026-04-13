package com.vital.health.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val supabase: SupabaseClient
) {
    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    fun currentUserId(): String? =
        supabase.auth.currentSessionOrNull()?.user?.id

    fun currentUserEmail(): String? =
        supabase.auth.currentSessionOrNull()?.user?.email

    fun currentUserName(): String? =
        supabase.auth.currentSessionOrNull()?.user?.userMetadata
            ?.get("full_name")?.jsonPrimitive?.content

    fun currentUserAvatar(): String? =
        supabase.auth.currentSessionOrNull()?.user?.userMetadata
            ?.get("avatar_url")?.jsonPrimitive?.content

    fun isAuthenticated(): Boolean = currentUserId() != null

    suspend fun updateProfile(name: String, avatarUrl: String?) {
        supabase.auth.updateUser {
            data = buildJsonObject {
                put("full_name", name)
                if (avatarUrl != null) put("avatar_url", avatarUrl)
            }
        }
    }

    suspend fun uploadAvatar(bytes: ByteArray): String {
        val userId = currentUserId() ?: error("Not authenticated")
        val fileName = "$userId/${UUID.randomUUID()}.jpg"
        supabase.storage["avatars"].upload(fileName, bytes) { upsert = true }
        return supabase.storage["avatars"].publicUrl(fileName)
    }

    suspend fun login(userEmail: String, userPass: String) {
        supabase.auth.signInWith(Email) {
            email = userEmail
            password = userPass
        }
    }

    suspend fun signUp(userEmail: String, userPass: String) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPass
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }
}
