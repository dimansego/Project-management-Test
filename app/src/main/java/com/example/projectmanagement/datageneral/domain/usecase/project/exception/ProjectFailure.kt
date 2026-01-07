package com.example.projectmanagement.datageneral.domain.usecase.project.exception

import io.github.jan.supabase.exceptions.*

sealed class ProjectFailure(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    class Unauthorized(cause: Throwable? = null) : ProjectFailure("Not authorized for this project", cause)
    class NotFound(cause: Throwable? = null) : ProjectFailure("Project not found", cause)
    class Validation(message: String, cause: Throwable? = null) : ProjectFailure(message, cause)
    class DuplicateInviteCode(cause: Throwable? = null) : ProjectFailure("Invite code already exists", cause)
    class Network(cause: Throwable? = null) : ProjectFailure(cause = cause)
    class Unknown(cause: Throwable? = null) : ProjectFailure(cause = cause)
}

fun Throwable.toProjectFailure(): ProjectFailure =
    when (this) {
        is UnauthorizedRestException -> ProjectFailure.Unauthorized(this)
        is NotFoundRestException -> ProjectFailure.NotFound(this)
        is BadRequestRestException -> ProjectFailure.Validation(message ?: "Invalid project request", this)
        is HttpRequestException -> ProjectFailure.Network(this)
        is SupabaseEncodingException -> ProjectFailure.Validation("Encoding/decoding error", this)
        is RestException -> ProjectFailure.Unknown(this)
        else -> ProjectFailure.Unknown(this)
    }