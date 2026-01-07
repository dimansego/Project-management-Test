package com.example.projectmanagement.datageneral.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDependency(
    @SerialName(PROJECT_ID) val projectId: String,
    @SerialName(PREDECESSOR_ID) val predecessorId: String,
    @SerialName(SUCCESSOR_ID) val successorId: String,
    @SerialName(DEPENDENCY_TYPE) val dependencyType: String,
    val lag: Long? = null,
    @SerialName(CREATED_AT) val createdAt: String? = null
) {
    companion object {
        const val TASK_DEPENDENCIES = "task_dependencies"
        const val PROJECT_ID = "project_id"
        const val PREDECESSOR_ID = "predecessor_id"
        const val SUCCESSOR_ID = "successor_id"
        const val DEPENDENCY_TYPE = "dependency_type"
        const val LAG = "lag"
        const val CREATED_AT = "created_at"
    }
}