package com.example.projectmanagement.datageneral.domain.entity.task

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.task.TaskDependency
import java.time.Instant

class TaskDependencyEntity(
    val projectId: String,
    val predecessorId: String,
    val successorId: String,
    val dependencyType: TaskDependencyType,
    val lag: Long,
    val createdAt: Instant
) {
    companion object {
        const val MIN_LAG = 0
        const val MAX_LAG = 7_776_000
    }

    init {
        require(predecessorId != successorId) { "A task cannot depend on itself" }
        require(lag in MIN_LAG..MAX_LAG) {"Lag must be between $MIN_LAG and $MAX_LAG seconds"}
    }
}

fun TaskDependencyEntity.asDataModel(): TaskDependency {
    return TaskDependency(
        projectId = projectId,
        predecessorId = predecessorId,
        successorId = successorId,
        dependencyType = dependencyType.value,
        lag = lag,
        createdAt = DateTimeConfig.fromInstant(createdAt)
    )
}

fun TaskDependency.asEntity(): TaskDependencyEntity {
    return TaskDependencyEntity(
        projectId = projectId,
        predecessorId = predecessorId,
        successorId = successorId,
        dependencyType = TaskDependencyType.fromValue(dependencyType)
            ?: error("Invalid dependency type: $dependencyType"),
        lag = lag ?: 0,
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt))
    )
}