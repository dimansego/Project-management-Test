package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.model.task.TaskDependency
import com.example.projectmanagement.datageneral.data.repository.task.TaskDependencyRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskDependencyEntity
import com.example.projectmanagement.datageneral.domain.entity.task.TaskDependencyType
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class AddTaskDependencyUseCase(
    private val repo: TaskDependencyRepository
) {
    suspend operator fun invoke(
        predecessorId: String,
        successorId: String,
        projectId: String,
        dependencyType: TaskDependencyType,
        lag: Long = 288000): Result<TaskDependencyEntity> {
        return runCatching {
            val taskDependency = TaskDependency(
                predecessorId = predecessorId,
                projectId = projectId,
                successorId = successorId,
                dependencyType = dependencyType.value,
                lag = lag
            )

            repo.createDependency(taskDependency).asEntity()
        }.mapFailure { it.toTaskFailure() }
    }
}