package com.example.projectmanagement.datageneral.data.model.meeting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: String? = null,
    @SerialName(MEETING_ID) val meetingId: String,
//    @SerialName(MEETING_NOTE_ID) val meetingNoteId: String? = null,
    @SerialName(PROJECT_ID) val projectId: String,
    val url: String,
//    val type: String? = null,
    @SerialName(UPLOADED_BY) val uploadedBy: String,
    @SerialName(CREATED_AT) val createdAt: String? = null
) {
    companion object {
        const val ATTACHMENTS = "attachments"
        const val ID = "id"
        const val MEETING_ID = "meeting_id"
//        const val MEETING_NOTE_ID = "meeting_note_id"
        const val PROJECT_ID = "project_id"
        const val URL = "url"
//        const val TYPE = "type"
        const val UPLOADED_BY = "uploaded_by"
        const val CREATED_AT = "created_at"
    }
}
