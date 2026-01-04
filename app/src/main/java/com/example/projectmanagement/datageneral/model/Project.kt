package com.example.projectmanagement.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val title: String,
    val description: String,
    val inviteCode: String? = null,
    val createdAt: String? = null
)

