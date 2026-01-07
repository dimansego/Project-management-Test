package com.example.projectmanagement.datageneral.domain.usecase.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String
)


