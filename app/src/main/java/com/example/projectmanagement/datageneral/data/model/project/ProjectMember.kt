package com.example.projectmanagement.datageneral.data.model.project

import com.example.projectmanagement.datageneral.data.model.user.AppUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectMember(
    @SerialName(PROJECT_ID) val projectId: String,
    @SerialName(USER_ID) val userId: String,
    val role: String? = null,
    @SerialName(JOINED_AT) val joinedAt: String? = null,

    // ADD THIS: This allows Supabase to "nest" the user details here
    // The SerialName MUST match the table name in your database
    @SerialName("app_users") val userDetails: AppUser? = null
) {
    companion object {
        const val PROJECT_MEMBERS = "project_members"
        const val PROJECT_ID = "project_id"
        const val USER_ID = "user_id"
        const val ROLE = "role"
        const val JOINED_AT = "joined_at"
    }
}