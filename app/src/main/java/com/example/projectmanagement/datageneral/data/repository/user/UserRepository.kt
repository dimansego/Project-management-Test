package com.example.projectmanagement.datageneral.data.repository.user

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException

class UserRepository(private val client: SupabaseClient, private val authRepo: AuthRepository) {

    suspend fun getAllUsers(): List<AppUser> {
        return client.db.from(AppUser.APP_USERS).select().decodeList<AppUser>()
    }

    suspend fun createUser(appUser: AppUser): AppUser? {
        return client.db.from(AppUser.APP_USERS)
            .insert(appUser)
            .decodeSingleOrNull<AppUser>()
    }

    suspend fun linkAuthUser(user: UserInfo, name: String? = null): AppUser? {
        try {
            android.util.Log.d("UserRepository", "Linking auth user: ${user.email}, authId: ${user.id}")

            // Use provided name, fallback to metadata, then email
            val userName = name
                ?: (user.userMetadata?.get("full_name") as? String)
                ?: user.email?.substringBefore("@") // Fallback to email username

            android.util.Log.d("UserRepository", "Creating profile with name: $userName")

            val newUser = AppUser(
                authId = user.id,
                email = user.email!!,
                name = userName
            )

            android.util.Log.d("UserRepository", "Attempting to create user profile in app_users table...")
            val createdUser = createUser(newUser)

            if (createdUser == null) {
                android.util.Log.w("UserRepository", "createUser returned null, checking if user already exists...")
                // Check if user already exists (might have been created in a previous attempt)
                val existingUser = getCurrentUser()
                if (existingUser != null) {
                    android.util.Log.d("UserRepository", "Found existing user profile")
                    return existingUser
                }
                android.util.Log.e("UserRepository", "Failed to create user profile and user not found")
                return null
            }

            android.util.Log.d("UserRepository", "User profile created successfully: ${createdUser.id}")
            return createdUser
        } catch (e: RestException) {
            // RLS policy violation
            android.util.Log.e("UserRepository", "RLS Policy Error: ${e.message}", e)
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("row-level security policy", ignoreCase = true)) {
                throw Exception(
                    "Row Level Security (RLS) policy violation. " +
                    "Please run the SQL in FIX_RLS_POLICY.sql in your Supabase SQL Editor. " +
                    "Error: ${e.message}",
                    e
                )
            }
            throw e
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error linking auth user: ${e.message}", e)
            // Re-throw with more context if it's an RLS issue
            if (e.message?.contains("row-level security", ignoreCase = true) == true) {
                throw Exception(
                    "Database security policy error. Please check Supabase RLS policies. " +
                    "See FIX_RLS_POLICY.sql for the fix. Original error: ${e.message}",
                    e
                )
            }
            throw e
        }
    }


    suspend fun getCurrentUser(): AppUser? {
        val authUser = authRepo.currentAuthUser ?: return null

        return client.db.from(AppUser.APP_USERS).select {
            filter {
                AppUser::authId eq authUser.id
            }
        }.decodeSingleOrNull<AppUser>()
    }

    suspend fun getUserById(userId: String): AppUser? {
        return client.db.from(AppUser.APP_USERS).select {
            filter {
                AppUser::id eq userId
            }
        }.decodeSingleOrNull<AppUser>()
    }
}