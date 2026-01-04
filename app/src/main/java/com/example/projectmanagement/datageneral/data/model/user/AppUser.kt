package com.example.projectmanagement.datageneral.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    val id: String? = null,
    @SerialName(AUTH_ID) val authId: String,
    val email: String,
    val name: String? = null,
    @SerialName(CREATED_AT) val createdAt: String? = null
) {
    companion object {
        const val APP_USERS = "app_users"
        const val ID = "id"
        const val AUTH_ID = "auth_id"
        const val EMAIL = "email"
        const val NAME = "name"
        const val CREATED_AT = "created_at"
    }
}
