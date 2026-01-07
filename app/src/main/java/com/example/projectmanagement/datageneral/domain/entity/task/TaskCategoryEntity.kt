package com.example.projectmanagement.datageneral.domain.entity.task

import com.example.projectmanagement.datageneral.data.model.task.TaskCategory
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Deprecated("Entity no longer used")
class TaskCategoryEntity(
    val id: String,
    val projectId: String,
    name: String,
    color: String
) : Updatable {
    companion object {
        const val MAX_NAME_LENGTH = 10
    }

    var name = name
        set(value) {
            require(value.isNotBlank()) { "name must not be blank" }
            require(value.length <= MAX_NAME_LENGTH) { "name length must be less than $MAX_NAME_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += TaskCategory.NAME
            }
        }
    var color = color
        set(value) {
            require(value.isNotBlank()) { "color must not be blank" }
            require(Regex("^#[0-9A-Fa-f]{6}\$").matches(value)) { "Invalid color" }
            if (field != value) {
                field = value
                modifiedFields += TaskCategory.COLOR
            }
        }
    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    TaskCategory.NAME -> put(TaskCategory.NAME, JsonPrimitive(name))
                    TaskCategory.COLOR -> put(TaskCategory.COLOR, JsonPrimitive(color))
                }
            }
        }

    init {
        this.name = name
        this.color = color
        modifiedFields.clear()
    }
}

fun TaskCategoryEntity.asDataModel(): TaskCategory {
    return TaskCategory(
        id = this.id,
        projectId = this.projectId,
        name = this.name,
        color = this.color
    )
}

fun TaskCategory.asEntity(): TaskCategoryEntity {
    return TaskCategoryEntity(
        id = requireNotNull(id) {"Task category ID cannot be null"},
        projectId = this.projectId,
        name = this.name,
        color = requireNotNull(color) { "Color cannot be null" }
    )
}