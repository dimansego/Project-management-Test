package com.example.projectmanagement.datageneral.domain.usecase.meeting.exception

import io.github.jan.supabase.exceptions.*

sealed class MeetingFailure(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    class Unauthorized(cause: Throwable? = null) : MeetingFailure("Unauthorized for this meeting", cause)
    class NotFound(cause: Throwable? = null) : MeetingFailure("Meeting not found")
    class Validation(message: String, cause: Throwable? = null) : MeetingFailure(message, cause)
    class Network(cause: Throwable? = null) : MeetingFailure(cause = cause)
    class Unknown(cause: Throwable? = null) : MeetingFailure(cause = cause)
}

fun Throwable.toMeetingFailure(): MeetingFailure =
    when (this) {
        is UnauthorizedRestException -> MeetingFailure.Unauthorized(this)
        is NotFoundRestException -> MeetingFailure.NotFound(this)
        is BadRequestRestException -> MeetingFailure.Validation(message ?: "Invalid meeting request", this)
        is SupabaseEncodingException -> MeetingFailure.Validation("Encoding/decoding error: ${message ?: "n/a"}", this)
        is HttpRequestException -> MeetingFailure.Network(this)
        is UnknownRestException -> MeetingFailure.Unknown(this)
        is RestException -> MeetingFailure.Unknown(this)
        else -> MeetingFailure.Unknown(this)
    }