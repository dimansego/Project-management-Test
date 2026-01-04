package com.example.projectmanagement.datageneral.data.model.meeting

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MeetingInvitation(
    @SerialName(MEETING_ID) val meetingId: String,
    @SerialName(USER_ID) val userId: String,
    val status: String ?= null,
    @SerialName(INVITED_AT) val invitedAt: String ?= null,
    @SerialName(RESPONDED_AT) val respondedAt: String? = null,
    @SerialName(PROJECT_ID) val projectId: String
) {
    companion object {
        const val MEETING_INVITATIONS = "meeting_invitations"
        const val MEETING_ID = "meeting_id"
        const val USER_ID = "user_id"
        const val STATUS = "status"
        const val INVITED_AT = "invited_at"
        const val RESPONDED_AT = "responded_at"
        const val PROJECT_ID = "project_id"
    }
}
