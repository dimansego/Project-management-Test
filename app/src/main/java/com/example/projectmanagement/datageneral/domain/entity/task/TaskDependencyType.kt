package com.example.projectmanagement.datageneral.domain.entity.task

enum class TaskDependencyType(val value: String) {
    START_TO_START("start_to_start"),
    START_TO_FINISH("start_to_finish"),
    FINISH_TO_START("finish_to_start"),
    FINISH_TO_FINISH("finish_to_finish");

    companion object {
        fun fromValue(value: String): TaskDependencyType? =
            entries.firstOrNull { it.value == value }
    }
}