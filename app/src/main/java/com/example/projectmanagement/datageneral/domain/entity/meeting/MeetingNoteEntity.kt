package com.example.projectmanagement.datageneral.domain.entity.meeting

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.meeting.MeetingNote
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

@Deprecated("Entity no longer used")
class MeetingNoteEntity(
    val id: String,
    val meetingId: String,
    val projectId: String,
    val authorId: String,
    notes: String = "Type your notes here",
    val createdAt: Instant,
    updatedAt: Instant
) : Updatable {

    companion object {
        const val MAX_NOTE_LENGTH = 5000
    }

    var notes = notes
        set(value) {
            require(value.length <= MAX_NOTE_LENGTH) { "Note length must be less than $MAX_NOTE_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += MeetingNote.NOTES
                updatedAt = Instant.now()
            }
        }

    var updatedAt = updatedAt
        private set(value) {
            if (field != value) {
                field = value
                modifiedFields += MeetingNote.UPDATED_AT
            }
        }

    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    MeetingNote.NOTES -> put(MeetingNote.NOTES, JsonPrimitive(notes))
                    MeetingNote.UPDATED_AT -> put(MeetingNote.UPDATED_AT, JsonPrimitive(DateTimeConfig.fromInstant(updatedAt)))
                }
            }
        }

    init {
        this.notes = notes
        modifiedFields.clear()
    }
}

fun MeetingNoteEntity.asDataModel(): MeetingNote {
    return MeetingNote(
        id = this.id,
        meetingId = this.meetingId,
        projectId = this.projectId,
        authorId = this.authorId,
        notes = this.notes,
        createdAt = DateTimeConfig.fromInstant(this.createdAt),
        updatedAt = DateTimeConfig.fromInstant(this.updatedAt)
    )
}

fun MeetingNote.asEntity(): MeetingNoteEntity {
    return MeetingNoteEntity(
        id = requireNotNull(this.id) { "Note id cannot be null" },
        meetingId = this.meetingId,
        projectId = this.projectId,
        authorId = this.authorId,
        notes = this.notes ?: "Type your notes here",
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) {"Note created time is null"}),
        updatedAt = DateTimeConfig.toInstant(requireNotNull(updatedAt) {"Note updated time is null"})
    )
}