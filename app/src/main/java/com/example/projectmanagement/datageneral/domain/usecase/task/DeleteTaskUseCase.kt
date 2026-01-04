package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class DeleteTaskUseCase(private val repo: TaskRepository) {
    suspend operator fun invoke(task: TaskEntity): Result<Unit> =
        runCatching {
            repo.deleteTask(task.id, task.projectId)
        }.mapFailure { it.toTaskFailure() }
}