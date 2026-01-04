package com.example.projectmanagement.datageneral.domain.entity.task

enum class TaskStatus(val value: String) {
    TODO("todo"),
    DOING("doing"),
    DONE("done"),
    BLOCKED("blocked"),
    CANCELLED("cancelled");

    companion object {
        fun fromValue(value: String): TaskStatus? =
            entries.firstOrNull { it.value == value }
    }
}