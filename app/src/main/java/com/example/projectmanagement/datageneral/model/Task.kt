package com.example.projectmanagement.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val projectId: Int,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val deadline: String,
    val assigneeName: String
)

