package com.example.projectmanagement.datageneral.data.model.meeting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meeting(
    val id: String? = null,
    @SerialName(PROJECT_ID) val projectId: String,
    val title: String,
    val description: String? = null,
    @SerialName(START_TIME) val startTime: String,
    @SerialName(END_TIME) val endTime: String,
    val location: String,
    @SerialName(CREATED_BY) val createdBy: String,
    @SerialName(CREATED_AT) val createdAt: String? = null,
    @SerialName(UPDATED_AT) val updatedAt: String? = null,
    val notes: String? = null,
    @SerialName(NOTES_UPDATED_AT) val notesUpdatedAt: String? = null,
    @SerialName(NOTES_UPDATED_BY) val notesUpdatedBy: String? = null
) {
    companion object {
        const val MEETINGS = "meetings"
        const val ID = "id"
        const val PROJECT_ID = "project_id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val LOCATION = "location"
        const val CREATED_BY = "created_by"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val NOTES = "notes"
        const val NOTES_UPDATED_AT = "notes_updated_at"
        const val NOTES_UPDATED_BY = "notes_updated_by"
    }
}
