package com.example.projectmanagement.datageneral.data.repository.meeting

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.meeting.Attachment
import io.github.jan.supabase.postgrest.query.Order

class AttachmentRepository(private val client: SupabaseClient) {
    suspend fun getAttachmentsByMeetingId(meetingId: String, projectId: String): List<Attachment> {
        return client.db.from(Attachment.ATTACHMENTS).select {
            filter {
                and {
                    Attachment::meetingId eq meetingId
                    Attachment::projectId eq projectId
                }
            }
            order(Attachment.CREATED_AT, Order.DESCENDING)
        }.decodeList<Attachment>()
    }

//    suspend fun getAttachmentsByNoteId(noteId: String, projectId: String): List<Attachment> {
//        return client.db.from(Attachment.ATTACHMENTS).select {
//            filter {
//                and {
//                    Attachment::meetingNoteId eq noteId
//                    Attachment::projectId eq projectId
//                }
//            }
//            order(Attachment.CREATED_AT, Order.DESCENDING)
//        }.decodeList<Attachment>()
//    }

//    suspend fun getAttachmentsByType(meetingId: String, projectId: String, type: String): List<Attachment> {
//        return client.db.from(Attachment.ATTACHMENTS).select {
//            filter {
//                and {
//                    Attachment::meetingId eq meetingId
//                    Attachment::projectId eq projectId
//                    Attachment::type eq type
//                }
//            }
//        }.decodeList<Attachment>()
//    }

    suspend fun createAttachment(attachment: Attachment): Attachment {
        return client.db.from(Attachment.ATTACHMENTS).insert(attachment) {
            select()
        }.decodeSingle<Attachment>()
    }

    suspend fun deleteAttachment(attachmentId: String, projectId: String) {
        client.db.from(Attachment.ATTACHMENTS).delete {
            filter {
                Attachment::id eq attachmentId
            }
        }
    }
}