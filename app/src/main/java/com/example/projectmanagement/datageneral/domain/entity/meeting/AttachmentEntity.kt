package com.example.projectmanagement.datageneral.domain.entity.meeting

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.meeting.Attachment
import java.time.Instant

class AttachmentEntity(
    val id: String,
    val meetingId: String,
//    val meetingNoteId: String?,
    val projectId: String,
    val url: String,
//    val type: AttachmentType?,
    val uploadedBy: String,
    val createdAt: Instant
) {
    init {
//        require(meetingId != null) { "Attachment must belong to a meeting" }
        require(url.isNotBlank()) { "URL must not be blank" }
//        require(type in AttachmentType.entries) { "Invalid attachment type. Must be one of: ${AttachmentType.entries}" }
    }
}

fun AttachmentEntity.asDataModel(): Attachment {
    return Attachment(
        id = this.id,
        url = this.url,
//        type = this.type?.value,
        uploadedBy = this.uploadedBy,
        meetingId = meetingId,
//        meetingNoteId = meetingNoteId,
        projectId = projectId,
        createdAt = DateTimeConfig.fromInstant(this.createdAt)
    )
}

fun Attachment.asEntity(): AttachmentEntity {
    return AttachmentEntity(
        id = requireNotNull(this.id) { "Attachment id cannot be null" },
        uploadedBy = this.uploadedBy,
        meetingId = meetingId,
//        meetingNoteId = this.meetingNoteId,
        url = this.url,
//        type = type?.let {AttachmentType.fromValue(it) ?: error("Unknown attachment type: $it. Must be one of ${AttachmentType.entries}")},
        projectId = projectId,
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) {"Note created time is null"})
    )
}