package com.example.projectmanagement.datageneral.data.repository.meeting

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.meeting.MeetingNote
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject

@Deprecated("Table ${MeetingNote.MEETING_NOTES} no longer used")
class MeetingNoteRepository(private val client: SupabaseClient) {
    suspend fun getNotesByMeetingId(meetingId: String, projectId: String): List<MeetingNote> {
        return client.db.from(MeetingNote.MEETING_NOTES).select {
            filter {
                and {
                    MeetingNote::projectId eq projectId
                    MeetingNote::meetingId eq meetingId
                }
            }
            order(MeetingNote.CREATED_AT, Order.DESCENDING)
        }.decodeList<MeetingNote>()
    }

    suspend fun getNotesByProjectId(projectId: String): List<MeetingNote> {
        return client.db.from(MeetingNote.MEETING_NOTES).select {
            filter {
                MeetingNote::projectId eq projectId
            }
            order(MeetingNote.CREATED_AT, Order.DESCENDING)
        }.decodeList<MeetingNote>()
    }

    suspend fun addNote(note: MeetingNote): MeetingNote {
        return client.db.from(MeetingNote.MEETING_NOTES).insert(note) {
            select()
        }.decodeSingle<MeetingNote>()
    }

    suspend fun updateNote(noteId: String, projectId: String, updates: JsonObject): MeetingNote {

        return client.db.from(MeetingNote.MEETING_NOTES).update(
            updates
        ) {
            filter {
                and {
                    MeetingNote::id eq noteId
                    MeetingNote::projectId eq projectId
                }
            }
            select()
        }.decodeSingle<MeetingNote>()
    }

    suspend fun deleteNote(noteId: String, projectId: String) {
        client.db.from(MeetingNote.MEETING_NOTES).delete {
            filter {
                and {
                    MeetingNote::projectId eq projectId
                    MeetingNote::id eq noteId
                }
            }
        }
    }

}