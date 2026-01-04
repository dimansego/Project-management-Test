package com.example.projectmanagement.datageneral.domain.usecase.task

import com.example.projectmanagement.datageneral.data.repository.task.TaskDependencyRepository
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.domain.entity.task.TaskStatus
import com.example.projectmanagement.datageneral.domain.entity.task.TaskEntity
import com.example.projectmanagement.datageneral.domain.entity.task.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.DependencyFailure
import com.example.projectmanagement.datageneral.domain.usecase.task.exception.toTaskFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ChangeTaskStatusUseCase(
    private val repo: TaskRepository,
    private val dependencyRepo: TaskDependencyRepository
) {
    suspend operator fun invoke(task: TaskEntity, requested: TaskStatus): Result<TaskEntity> {
        val nextStatus = when (requested) {

            TaskStatus.DOING -> {
                if (dependencyRepo.checkStartViolations(task.id, task.projectId))
                    TaskStatus.BLOCKED
                else
                    TaskStatus.DOING
            }

            TaskStatus.DONE -> {
                if (dependencyRepo.checkFinishViolations(task.id, task.projectId))
                    return Result.failure(DependencyFailure.TaskDependencyViolation("Task cannot finish due to unmet dependencies"))
                TaskStatus.DONE
            }

            TaskStatus.CANCELLED -> TaskStatus.CANCELLED
            TaskStatus.TODO -> TaskStatus.TODO

            // BLOCKED is never requested
            TaskStatus.BLOCKED -> return Result.failure(DependencyFailure.IllegalTransition("Cannot transition to blocked state"))
        }

        task.status = nextStatus

        return runCatching {
            repo.updateTask(task.id, task.projectId, task.asUpdateMap()).asEntity()
        }.mapFailure { it.toTaskFailure() }

    }

}