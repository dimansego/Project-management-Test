package com.example.projectmanagement.datageneral.data.repository.meeting

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject

class MeetingRepository(private val client: SupabaseClient) {
    suspend fun getMeetingsByProjectId(projectId: String): List<Meeting> {
        return client.db.from(Meeting.MEETINGS).select {
            filter {
                Meeting::projectId eq projectId
            }
            order(Meeting.START_TIME, Order.ASCENDING)
        }.decodeList<Meeting>()
}

    suspend fun getMeetingById(meetingId: String, projectId: String): Meeting? {
        return client.db.from(Meeting.MEETINGS).select {
            filter {
                and {
                    Meeting::projectId eq projectId
                    Meeting::id eq meetingId
                }
            }
        }.decodeSingleOrNull<Meeting>()
    }

    suspend fun getUpcomingMeetings(projectId: String, currentIsoTime: String): List<Meeting> {
        return client.db.from(Meeting.MEETINGS).select {
            filter {
                Meeting::projectId eq projectId
                Meeting::startTime gte currentIsoTime
            }
            order(Meeting.START_TIME, Order.ASCENDING)
            //limit(5)
        }.decodeList<Meeting>()
    }

    suspend fun createMeeting(meeting: Meeting): Meeting {
        return client.db.from(Meeting.MEETINGS).insert(meeting) {
            select()
        }.decodeSingle<Meeting>()
    }

    suspend fun updateMeeting(meetingId: String, projectId: String, updates: JsonObject): Meeting {
        return client.db.from(Meeting.MEETINGS).update(updates) {
            filter {
                and {
                    Meeting::projectId eq projectId
                    Meeting::id eq meetingId
                }
            }
            select()
        }.decodeSingle<Meeting>()
    }

    suspend fun deleteMeeting(meetingId: String, projectId: String) {
        client.db.from(Meeting.MEETINGS).delete {
            filter {
                and {
                    Meeting::projectId eq projectId
                    Meeting::id eq meetingId
                }
            }
        }
    }
}