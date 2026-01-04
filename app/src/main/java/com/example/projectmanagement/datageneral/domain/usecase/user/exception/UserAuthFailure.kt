package com.example.projectmanagement.datageneral.domain.usecase.user.exception

import io.github.jan.supabase.exceptions.*

sealed class UserAuthFailure(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    class InvalidCredentials(cause: Throwable? = null) : UserAuthFailure("Invalid email or password", cause)
    class Unauthorized(cause: Throwable? = null) : UserAuthFailure("Unauthorized", cause)
    class NotFound(cause: Throwable? = null) : UserAuthFailure("User not found", cause)
    class Network(cause: Throwable? = null) : UserAuthFailure("Network error", cause)
    class Validation(message: String, cause: Throwable? = null) : UserAuthFailure(message, cause)
    class Unknown(cause: Throwable? = null) : UserAuthFailure("Unknown authentication error", cause)
}

fun Throwable.toUserAuthFailure(): UserAuthFailure =
    when (this) {
        is UnauthorizedRestException -> {
            // Check if it's an RLS policy violation
            val message = this.message ?: ""
            if (message.contains("row-level security policy", ignoreCase = true) ||
                message.contains("violates row-level security", ignoreCase = true)) {
                UserAuthFailure.Validation(
                    "Database security policy error. Please check Supabase RLS policies for app_users table.",
                    this
                )
            } else {
                UserAuthFailure.InvalidCredentials(this)
            }
        }
        is BadRequestRestException -> UserAuthFailure.Validation(message ?: "Invalid request", this)
        is HttpRequestException -> UserAuthFailure.Network(this)
        is SupabaseEncodingException -> UserAuthFailure.Validation("Encoding/decoding error", this)
        is NotFoundRestException -> UserAuthFailure.NotFound(this) // usually misconfigured auth
        is UnknownRestException, is RestException -> UserAuthFailure.Unknown(this)
        else -> UserAuthFailure.Unknown(this)
    }
