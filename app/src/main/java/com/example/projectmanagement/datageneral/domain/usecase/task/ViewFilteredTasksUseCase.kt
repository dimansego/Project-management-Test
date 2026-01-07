package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.entity.task.TaskStatus
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.TaskFailure
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ViewFilteredTasksUseCase(
    private val repo: TaskRepository
) {
    suspend operator fun invoke(projectId: String, status: TaskStatus): Result<List<TaskEntity>> =
        runCatching {
            repo.getTasksByStatus(projectId, status.value).map { it.asEntity() }
        }.mapFailure { it.toTaskFailure() }

    suspend operator fun invoke(projectId: String, assigneeId: String): Result<List<TaskEntity>> =
        runCatching {
            repo.getTasksByAssignee(projectId, assigneeId).map { it.asEntity() }
        }.mapFailure { it.toTaskFailure() }
}