package com.example.projectmanagement.datageneral.data.repository.meeting

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.meeting.MeetingInvitation
import kotlinx.serialization.json.JsonObject

class MeetingInvitationRepository(private val client: SupabaseClient) {
    suspend fun createInvitation(invitation: MeetingInvitation): MeetingInvitation {
        return client.db
            .from(MeetingInvitation.MEETING_INVITATIONS)
            .insert(invitation) {
                select()
            }
            .decodeSingle<MeetingInvitation>()
    }

    suspend fun getByMeeting(meetingId: String, projectId: String): List<MeetingInvitation> {
        return client.db.from(MeetingInvitation.MEETING_INVITATIONS)
            .select {
                filter {
                    and {
                        MeetingInvitation::meetingId eq meetingId
                        MeetingInvitation::projectId eq projectId
                    }
                }
            }
            .decodeList<MeetingInvitation>()
    }

    suspend fun getByUserInProject(userId: String, projectId: String): List<MeetingInvitation> {
        return client.db.from(MeetingInvitation.MEETING_INVITATIONS)
            .select {
                filter {
                    and {
                        MeetingInvitation::userId eq userId
                        MeetingInvitation::projectId eq projectId
                    }
                }
            }
            .decodeList<MeetingInvitation>()
    }

    suspend fun getByProject(projectId: String): List<MeetingInvitation> {
        return client.db.from(MeetingInvitation.MEETING_INVITATIONS)
            .select {
                filter { MeetingInvitation::projectId eq projectId }
            }
            .decodeList<MeetingInvitation>()
    }

    suspend fun updateInvitation(
        meetingId: String,
        userId: String,
        projectId: String,
        updateFields: JsonObject
    ): MeetingInvitation {
        return client.db.from(MeetingInvitation.MEETING_INVITATIONS)
            .update(updateFields) {
                filter {
                    and {
                        MeetingInvitation::meetingId eq meetingId
                        MeetingInvitation::userId eq userId
                        MeetingInvitation::projectId eq projectId
                    }
                }
                select()
            }
            .decodeSingle<MeetingInvitation>()
    }

    suspend fun deleteInvitation(meetingId: String, userId: String, projectId: String) {
        client.db.from(MeetingInvitation.MEETING_INVITATIONS).delete {
            filter {
                and {
                    MeetingInvitation::meetingId eq meetingId
                    MeetingInvitation::userId eq userId
                    MeetingInvitation::projectId eq projectId
                }
            }
        }
    }
}
