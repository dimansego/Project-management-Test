package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.model.task.Task
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class CreateTaskUseCase(private val repo: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<TaskEntity> =
        runCatching {
            repo.createTask(task).asEntity()
        }.mapFailure { it.toTaskFailure() }
}