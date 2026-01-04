package com.example.projectmanagement.datageneral.data.model.task

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Task(
    val id: String? = null,
    @SerialName(PROJECT_ID) val projectId: String,
    val title: String,
    val description: String? = null,
    @SerialName(ASSIGNEE_ID) val assigneeId: String? = null,
//    @SerialName(CATEGORY_ID) val categoryId: String? = null,
    val status: String? = null,
    val priority: Int? = null,
    @SerialName(ESTIMATED_HOURS) val estimatedHour: Int = 8,
    @SerialName(DUE_DATE) val dueDate: String,
    @SerialName(CREATED_AT) val createdAt: String? = null,
    @SerialName(UPDATED_AT) val updatedAt: String? = null
) {
    companion object {
        const val TASKS = "tasks"
        const val ID = "id"
        const val PROJECT_ID = "project_id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val ASSIGNEE_ID = "assignee_id"
//        const val CATEGORY_ID = "category_id"
        const val STATUS = "status"
        const val PRIORITY = "priority"
        const val ESTIMATED_HOURS = "estimated_hours"
        const val DUE_DATE = "due_date"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}
