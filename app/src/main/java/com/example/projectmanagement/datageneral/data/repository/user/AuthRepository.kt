package com.example.projectmanagement.datageneral.data.repository.user

import com.example.projectmanagement.datageneral.core.SupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
// Add these imports
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(private val client: SupabaseClient) {

    val currentSession
        get() = client.auth.currentSessionOrNull()

    val currentAuthUser
        get() = client.auth.currentUserOrNull()

    suspend fun signUp(emailInput: String, passwordInput: String, name: String? = null): UserInfo? {
        return client.auth.signUpWith(Email) {
            email = emailInput
            password = passwordInput

            // --- FIX STARTS HERE ---
            // Only assign data if name is not null
            if (name != null) {
                data = buildJsonObject {
                    put("full_name", name)
                }
            }
            // --- FIX ENDS HERE ---
        }
    }

    suspend fun signIn(emailInput: String, passwordInput: String) {
        client.auth.signInWith(Email) {
            email = emailInput
            password = passwordInput
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }
}
