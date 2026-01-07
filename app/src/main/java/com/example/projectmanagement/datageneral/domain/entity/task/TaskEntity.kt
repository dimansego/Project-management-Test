package com.example.projectmanagement.datageneral.domain.entity.task

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.task.Task
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant
import java.time.ZoneId

class TaskEntity(
    val id: String,
    val projectId: String,
    title: String,
    description: String = "",
    assigneeId: String?,
//    categoryId: String?,
    status: TaskStatus,
    priority: Int,
    val estimatedHour: Int = 8,
    dueDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
) : Updatable {
    companion object {
        const val MAX_TITLE_LENGTH = 20
        const val MAX_DESCRIPTION_LENGTH = 500
    }

    var title = title
        set(value) {
            require(value.isNotBlank()) { "title cannot be blank" }
            require(value.length <= MAX_TITLE_LENGTH) { "title length must be less than $MAX_TITLE_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Task.TITLE
            }
        }

    var description = description
        set(value) {
            require(value.length <= MAX_DESCRIPTION_LENGTH) { "description must be less than $MAX_DESCRIPTION_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Task.DESCRIPTION
            }
        }

    var assigneeId = assigneeId
        set(value) {
            if (field != value) {
                field = value
                modifiedFields += Task.ASSIGNEE_ID
            }
        }

//    var categoryId = categoryId
//        set(value) {
//            if (field != value) {
//                field = value
//                modifiedFields += Task.CATEGORY_ID
//                updatedAt = Instant.now()
//            }
//        }

    var status = status
        set(value) {
            if (field != value) {
                field = value
                modifiedFields += Task.STATUS
            }
        }

    var priority = priority
        set(value) {
            require(value in 1..3) { "priority must be between 1 (high) and 3 (low)" }
            if (field != value) {
                field = value
                modifiedFields += Task.PRIORITY
            }
        }

    var dueDate = dueDate
        set(value) {
            val localDateTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()
            require(value >= LocalDate(localDateTime.year, localDateTime.monthValue, localDateTime.dayOfMonth)) {
                "due date must be in the future"
            }
            if (field != value) {
                field = value
                modifiedFields += Task.DUE_DATE
            }
        }

    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    Task.TITLE -> put(Task.TITLE, JsonPrimitive(title))
                    Task.DESCRIPTION -> put(Task.DESCRIPTION, JsonPrimitive(description))
                    Task.ASSIGNEE_ID -> put(Task.ASSIGNEE_ID, JsonPrimitive(assigneeId))
//                    Task.CATEGORY_ID -> put(Task.CATEGORY_ID, JsonPrimitive(categoryId))
                    Task.STATUS -> put(Task.STATUS, JsonPrimitive(status.value))
                    Task.PRIORITY -> put(Task.PRIORITY, JsonPrimitive(priority))
                    Task.DUE_DATE -> put(Task.DUE_DATE, JsonPrimitive(dueDate.toString()))
                }
            }
        }

    init {
        this.title = title
        this.description = description
        this.assigneeId = assigneeId
//        this.categoryId = categoryId
        this.status = status
        this.priority = priority
        this.dueDate = dueDate
        modifiedFields.clear()
    }
}

fun TaskEntity.asDataModel(): Task {
    return Task(
        id = id,
        projectId = projectId,
        title = title,
        description = description,
        assigneeId = assigneeId,
//        categoryId = categoryId,
        status = status.value,
        priority = priority,
        estimatedHour = estimatedHour,
        dueDate = dueDate.toString(),
        createdAt = DateTimeConfig.fromInstant(createdAt),
        updatedAt = DateTimeConfig.fromInstant(updatedAt)
    )
}

fun Task.asEntity(): TaskEntity {
    return TaskEntity(
        id = requireNotNull(id) { "Task ID cannot be null" },
        projectId = projectId,
        title = title,
        description = description ?: "",
        assigneeId = assigneeId,
//        categoryId = categoryId,
        status = TaskStatus.fromValue(requireNotNull(status) {"Task Status is null"})
            ?: error("Unknown Status Error: Task Status $status does not exist"),
        priority = requireNotNull(priority) { "Priority cannot be null" },
        estimatedHour = estimatedHour,
        dueDate = LocalDate.parse(dueDate),
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) {"Task created time is null"}),
        updatedAt = DateTimeConfig.toInstant(requireNotNull(updatedAt) {"Task updated time is null"})
    )
}
