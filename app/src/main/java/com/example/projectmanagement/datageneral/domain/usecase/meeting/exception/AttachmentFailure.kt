package com.example.projectmanagement.datageneral.domain.usecase.meeting.exception

import io.github.jan.supabase.exceptions.*

sealed class AttachmentFailure(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    class Unauthorized(cause: Throwable? = null) : AttachmentFailure("Unauthorized to access attachment", cause)
    class NotFound(cause: Throwable? = null) : AttachmentFailure("Attachment not found", cause)
    class Validation(message: String, cause: Throwable? = null) : AttachmentFailure(message, cause)
    class Network(cause: Throwable? = null) : AttachmentFailure(cause = cause)
    class Unknown(cause: Throwable? = null) : AttachmentFailure(cause = cause)
}

fun Throwable.toAttachmentFailure(): AttachmentFailure =
    when (this) {
        is UnauthorizedRestException -> AttachmentFailure.Unauthorized(this)
        is NotFoundRestException -> AttachmentFailure.NotFound(this)
        is BadRequestRestException -> AttachmentFailure.Validation(message ?: "Invalid attachment request", this)
        is SupabaseEncodingException -> AttachmentFailure.Validation("Encoding/decoding error: ${message ?: "n/a"}", this)
        is HttpRequestException -> AttachmentFailure.Network(this)
        is UnknownRestException, is RestException -> AttachmentFailure.Unknown(this)
        else -> AttachmentFailure.Unknown(this)
    }
