package com.example.projectmanagement.datageneral.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val inviteCode: String? = null,
    val createdAt: String? = null
)

