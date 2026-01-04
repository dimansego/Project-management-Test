package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class UpdateTaskUseCase(private val repo: TaskRepository) {
    suspend operator fun invoke(task: TaskEntity): Result<TaskEntity> =
        runCatching {
            require(task.modifiedFields.isNotEmpty()) { "No changes to update" }

            repo.updateTask(task.id, task.projectId, task.asUpdateMap()).asEntity()
        }.mapFailure { it.toTaskFailure() }
}