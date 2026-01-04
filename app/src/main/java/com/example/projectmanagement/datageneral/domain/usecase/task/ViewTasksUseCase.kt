package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.TaskFailure
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ViewTasksUseCase(private val repo: TaskRepository) {
    suspend operator fun invoke(projectId: String): Result<List<TaskEntity>> =
        runCatching {
            repo.getTasksByProjectId(projectId).map { it.asEntity() }
        }.mapFailure { it.toTaskFailure() }

    suspend operator fun invoke(projectId: String, taskId: String): Result<TaskEntity> =
        runCatching {
            repo.getTaskById(taskId, projectId)?.asEntity() ?: throw TaskFailure.NotFound()
        }.mapFailure { it.toTaskFailure() }
}