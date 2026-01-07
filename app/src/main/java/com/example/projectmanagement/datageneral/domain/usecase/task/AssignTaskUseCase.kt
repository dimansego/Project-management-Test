package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.*

class AssignTaskUseCase(
    private val repo: TaskRepository
) {
    suspend operator fun invoke(task: TaskEntity, assigneeId: String): Result<TaskEntity> {
        task.assigneeId = assigneeId

        return runCatching {
            repo.updateTask(task.id, task.projectId, task.asUpdateMap()).asEntity()
        }.mapFailure { it.toTaskFailure() }
    }
}