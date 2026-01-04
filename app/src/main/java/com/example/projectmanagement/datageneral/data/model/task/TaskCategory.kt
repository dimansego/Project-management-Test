package com.example.projectmanagement.datageneral.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Table no longer used")
@Serializable
data class TaskCategory(
    val id: String? = null,
    @SerialName(PROJECT_ID) val projectId: String,
    val name: String,
    val color: String? = null
) {
    companion object {
        const val TASK_CATEGORIES = "task_categories"
        const val ID = "id"
        const val PROJECT_ID = "project_id"
        const val NAME = "name"
        const val COLOR = "color"
    }
}