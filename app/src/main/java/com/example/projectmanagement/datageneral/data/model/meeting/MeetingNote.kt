package com.example.projectmanagement.datageneral.data.model.meeting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Table no longer used")
@Serializable
data class MeetingNote(
    val id: String? = null,
    @SerialName(MEETING_ID) val meetingId: String,
    @SerialName(AUTHOR_ID) val authorId: String,
    @SerialName(PROJECT_ID) val projectId: String,
    val notes: String? = null,
    @SerialName(CREATED_AT) val createdAt: String? = null,
    @SerialName(UPDATED_AT) val updatedAt: String? = null
) {
    companion object {
        const val MEETING_NOTES = "meeting_notes"
        const val ID = "id"
        const val MEETING_ID = "meeting_id"
        const val PROJECT_ID = "project_id"
        const val AUTHOR_ID = "author_id"
        const val NOTES = "notes"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}