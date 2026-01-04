package com.example.projectmanagement.datageneral.domain.usecase.user

import com.example.projectmanagement.datageneral.domain.usecase.user.AuthResponse
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.toUserAuthFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure
import kotlinx.coroutines.delay

class SignUpUserUseCase(private val authRepository: AuthRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, name: String? = null): Result<AuthResponse> = runCatching {
        android.util.Log.d("SignUpUserUseCase", "Starting signup for email: $email")

        val newUserInfo = authRepository.signUp(email, password, name)
            ?: throw Exception("Sign up failed. User may already exist or network error occurred.")
        
        android.util.Log.d("SignUpUserUseCase", "User signed up successfully, creating profile...")
        
        // Pass the name explicitly to ensure it's used in profile creation
        // Try to create user profile - if it fails due to RLS, we'll still allow registration
        // since the auth account is created successfully
        var appUser: AppUser? = null
        try {
            appUser = userRepository.linkAuthUser(newUserInfo, name)
            if (appUser == null) {
                android.util.Log.w("SignUpUserUseCase", "createUser returned null, checking if user already exists...")
                appUser = userRepository.getCurrentUser()
            }
        } catch (e: Exception) {
            android.util.Log.w("SignUpUserUseCase", "Profile creation failed (likely RLS issue): ${e.message}")
            // If profile creation fails but auth succeeded, try to get existing user
            // or create a temporary user object for the response
            appUser = userRepository.getCurrentUser()
            if (appUser == null) {
                android.util.Log.w("SignUpUserUseCase", "Could not get user profile, but auth account created. User may need to sign in after fixing RLS.")
                // Create a temporary response - user can sign in after RLS is fixed
                // Don't throw error - auth account is created successfully
            }
        }
        
        android.util.Log.d("SignUpUserUseCase", "Getting session...")
        
        // Wait a bit for session to be available (Supabase might need a moment)
        var session = authRepository.currentSession
        var retries = 0
        while (session == null && retries < 3) {
            android.util.Log.d("SignUpUserUseCase", "Session not available yet, waiting... (retry $retries)")
            kotlinx.coroutines.delay(500) // Wait 500ms
            session = authRepository.currentSession
            retries++
        }
        
        // Always return success if auth account was created, even if profile creation failed
        // User can sign in and profile will be created on first login if RLS is fixed
        if (session != null) {
            android.util.Log.d("SignUpUserUseCase", "Signup completed successfully with session")
            val userId = appUser?.id ?: newUserInfo.id
            return@runCatching AuthResponse(token = session.accessToken, userId = userId)
        } else if (appUser != null) {
            // Profile exists but no session - might need email confirmation
            android.util.Log.w("SignUpUserUseCase", "Session not available but profile exists - email confirmation may be needed")
            return@runCatching AuthResponse(
                token = "pending_email_confirmation",
                userId = appUser.id!!
            )
        } else {
            // Auth account created but no profile and no session
            // Still return success - user can sign in and profile will be created
            android.util.Log.w("SignUpUserUseCase", "Auth account created but no profile/session. User can sign in to create profile.")
            return@runCatching AuthResponse(
                token = "auth_created_profile_pending",
                userId = newUserInfo.id
            )
        }
    }.mapFailure { error ->
        android.util.Log.e("SignUpUserUseCase", "Signup failed: ${error.message}", error)
        // Log the full stack trace for debugging
        error.printStackTrace()
        error.toUserAuthFailure()
    }
}

class SignInUserUseCase(private val authRepository: AuthRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> = runCatching {
        android.util.Log.d("SignInUserUseCase", "Starting signin for email: $email")
        
        authRepository.signIn(email, password)
        
        android.util.Log.d("SignInUserUseCase", "Sign in successful, getting session...")
        val session = authRepository.currentSession ?: throw Exception("Sign in failed. Check credentials or email confirmation.")
        
        android.util.Log.d("SignInUserUseCase", "Session obtained, getting user profile...")
        var user = userRepository.getCurrentUser()
        
        // If user profile doesn't exist, try to create it (might have been created during signup but failed due to RLS)
        if (user == null) {
            android.util.Log.w("SignInUserUseCase", "User profile not found, attempting to create...")
            val authUser = authRepository.currentAuthUser
            if (authUser != null) {
                try {
                    // Try to create profile on first login
                    user = userRepository.linkAuthUser(authUser, authUser.email?.substringBefore("@"))
                    android.util.Log.d("SignInUserUseCase", "User profile created on first login")
                } catch (e: Exception) {
                    android.util.Log.e("SignInUserUseCase", "Could not create user profile: ${e.message}")
                    // If profile creation fails, still allow login - profile can be created later
                    // Use auth ID as userId
                    return@runCatching AuthResponse(
                        token = session.accessToken,
                        userId = authUser.id
                    )
                }
            }
        }
        
        if (user == null) {
            // Even if profile doesn't exist, allow login with auth ID
            val authUser = authRepository.currentAuthUser
            if (authUser != null) {
                android.util.Log.w("SignInUserUseCase", "Using auth ID as userId - profile will be created later")
                return@runCatching AuthResponse(
                    token = session.accessToken,
                    userId = authUser.id
                )
            }
            throw Exception("User not found after sign in. Please try registering again.")
        }
        
        android.util.Log.d("SignInUserUseCase", "Sign in completed successfully")
        AuthResponse(token = session.accessToken, userId = user.id!!)
    }.mapFailure { error ->
        android.util.Log.e("SignInUserUseCase", "Sign in failed: ${error.message}", error)
        error.toUserAuthFailure()
    }
}

class SignOutUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        authRepository.signOut()
    }.mapFailure { it.toUserAuthFailure() }
}