package com.example.projectmanagement.datageneral.domain.usecase.task.exception

sealed class DependencyFailure(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class TaskDependencyViolation(message: String, cause: Throwable? = null) : DependencyFailure(message, cause)
    class IllegalTransition(message: String, cause: Throwable? = null) : DependencyFailure(message, cause)
}