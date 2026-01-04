package com.example.projectmanagement.datageneral.domain.usecase.task.exception

import io.github.jan.supabase.exceptions.*

sealed class TaskFailure(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class Network(cause: Throwable? = null) : TaskFailure("Network error while performing task operation", cause)
    class NotFound(cause: Throwable? = null) : TaskFailure("Task not found", cause)
    class Unauthorized(cause: Throwable? = null) : TaskFailure("You do not have permission to perform this action", cause)
    class Validation(message: String, cause: Throwable? = null) : TaskFailure("Invalid task data: $message", cause)
    class Unknown(message: String?, cause: Throwable? = null) : TaskFailure(message ?: "Unknown error occurred", cause)
}

fun Throwable.toTaskFailure(): TaskFailure =
    when (this) {
        is UnauthorizedRestException -> TaskFailure.Unauthorized(this)
        is NotFoundRestException -> TaskFailure.NotFound(this)
        is BadRequestRestException -> TaskFailure.Validation(message ?: "Invalid request.", this)
        is HttpRequestException -> TaskFailure.Network(this)
        is SupabaseEncodingException -> TaskFailure.Validation("Encoding/decoding error: ${message ?: "n/a"}", this)
        is UnknownRestException -> TaskFailure.Unknown(this.message, this)
        is RestException -> TaskFailure.Unknown(this.message, this)
        else -> TaskFailure.Unknown(this.message, this)
    }
