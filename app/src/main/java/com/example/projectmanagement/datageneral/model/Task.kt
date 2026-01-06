package com.example.projectmanagement.datageneral.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val deadline: String,
    val assigneeName: String
)

