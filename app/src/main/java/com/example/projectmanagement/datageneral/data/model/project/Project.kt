package com.example.projectmanagement.datageneral.data.model.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    @SerialName(INVITE_CODE) val inviteCode: String? = null,
    @SerialName(INVITE_EXPIRES_AT) val inviteExpiresAt: String? = null,
//    @SerialName(IS_INVITE_ACTIVE) val isInviteActive: Boolean? = null,
    @SerialName(OWNER_ID) val ownerId: String,
    @SerialName(CREATED_AT) val createdAt: String? = null
) {
    companion object {
        const val PROJECTS = "projects"
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val INVITE_CODE = "invite_code"
        const val INVITE_EXPIRES_AT = "invite_expires_at"
//        const val IS_INVITE_ACTIVE = "is_invite_active"
        const val OWNER_ID = "owner_id"
        const val CREATED_AT = "created_at"
    }
}
