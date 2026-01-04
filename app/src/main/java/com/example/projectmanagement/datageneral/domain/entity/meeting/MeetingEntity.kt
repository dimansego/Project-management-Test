package com.example.projectmanagement.datageneral.domain.entity.meeting

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

class MeetingEntity(
    val id: String,
    val projectId: String,
    title: String,
    description: String = "",
    startTime: Instant,
    endTime: Instant,
    location: String,
    val createdBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    notes: String = "",
    val notesUpdatedAt: Instant,
    notesUpdatedBy: String
) : Updatable {
    companion object {
        const val MAX_TITLE_LENGTH = 40
        const val MAX_DESCRIPTION_LENGTH = 500
        const val MAX_NOTE_LENGTH = 5000
    }

    var title = title
        set(value) {
            require(value.isNotBlank()) { "Title must not be blank" }
            require(value.length <= MAX_TITLE_LENGTH) { "Title must not exceed $MAX_TITLE_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.TITLE
            }
        }

    var description = description
        set(value) {
            require(value.length <= MAX_DESCRIPTION_LENGTH) { "Description must not exceed $MAX_DESCRIPTION_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.DESCRIPTION
            }
        }

    var location = location
        set(value) {
            require(value.isNotBlank()) { "Location must not be blank" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.LOCATION
            }
        }

    var startTime = startTime
        set(value) {
            require(value < endTime) { "Start time must be less than $endTime" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.START_TIME
            }
        }

    var endTime = endTime
        set(value) {
            require(value > startTime) { "End time must be greater than $startTime" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.END_TIME
            }
        }

    var notes = notes
        set(value) {
            require(value.length <= MAX_NOTE_LENGTH) { "Notes must not exceed $MAX_NOTE_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Meeting.NOTES
            }
        }

    var notesUpdatedBy = notesUpdatedBy
        set(value) {
            if (field != value) {
                field = value
                modifiedFields += Meeting.NOTES_UPDATED_BY
            }
        }

    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    Meeting.TITLE -> put(Meeting.TITLE, JsonPrimitive(title))
                    Meeting.DESCRIPTION -> put(Meeting.DESCRIPTION, JsonPrimitive(description))
                    Meeting.LOCATION -> put(Meeting.LOCATION, JsonPrimitive(location))
                    Meeting.START_TIME -> put(Meeting.START_TIME, JsonPrimitive(DateTimeConfig.fromInstant(startTime)))
                    Meeting.END_TIME -> put(Meeting.END_TIME, JsonPrimitive(DateTimeConfig.fromInstant(endTime)))
                    Meeting.NOTES -> put(Meeting.NOTES, JsonPrimitive(notes))
                    Meeting.NOTES_UPDATED_BY -> put(Meeting.NOTES_UPDATED_BY, JsonPrimitive(notesUpdatedBy))
                }
            }
        }

    init {
        this.title = title
        this.description = description
        this.location = location
        this.startTime = startTime
        this.endTime = endTime
        this.notes = notes
        this.notesUpdatedBy = notesUpdatedBy
        modifiedFields.clear()
    }
}

fun MeetingEntity.asDataModel(): Meeting {
    return Meeting(
        id = this.id,
        projectId = this.projectId,
        title = this.title,
        description = this.description,
        startTime = DateTimeConfig.fromInstant(startTime),
        endTime = DateTimeConfig.fromInstant(endTime),
        location = this.location,
        createdBy = this.createdBy,
        createdAt = DateTimeConfig.fromInstant(createdAt),
        updatedAt = DateTimeConfig.fromInstant(updatedAt),
        notes = this.notes,
        notesUpdatedBy = this.notesUpdatedBy,
        notesUpdatedAt = DateTimeConfig.fromInstant(notesUpdatedAt)
    )
}

fun Meeting.asEntity(): MeetingEntity {
    return MeetingEntity(
        id = requireNotNull(this.id) { "Meeting id cannot be null" },
        projectId = this.projectId,
        createdBy = this.createdBy,
        title = this.title,
        description = this.description ?: "",
        location = this.location,
        startTime = DateTimeConfig.toInstant(startTime),
        endTime = DateTimeConfig.toInstant(endTime),
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) { "Meeting created time is null" }),
        updatedAt = DateTimeConfig.toInstant(requireNotNull(updatedAt) { "Meeting updated time is null" }),
        notes = this.notes ?: "",
        notesUpdatedAt = DateTimeConfig.toInstant(requireNotNull(notesUpdatedAt) { "Meeting note updated time is null" }),
        notesUpdatedBy = requireNotNull(this.notesUpdatedBy) { "Meeting note updated by is null" },
    )
}