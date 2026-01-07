package com.example.projectmanagement.datageneral.domain.entity.project

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.project.Project
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

class ProjectEntity(
    val id: String,
    title: String,
    description: String = "",
    val inviteCode: String,
    inviteExpireDate: Instant,
//    isInviteActive: Boolean,
    val ownerId: String,
    val createdAt: Instant
) : Updatable {

    companion object {
        const val MAX_TITLE_LENGTH = 60
        const val MAX_DESCRIPTION_LENGTH = 5000
    }

    var title = title
        set(value) {
            require(value.isNotBlank()) { "title cannot be blank" }
            require(value.length <= MAX_TITLE_LENGTH) { "title length must be <= $MAX_TITLE_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Project.TITLE
            }
        }

    var description = description
        set(value) {
            require(value.length <= MAX_DESCRIPTION_LENGTH) { "description must be <= $MAX_DESCRIPTION_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += Project.DESCRIPTION
            }
        }

    var inviteExpireDate = inviteExpireDate
        set(value) {
            require(value >= Instant.now()) { "Expiration must be in the future" }
            if (field != value) {
                field = value
                modifiedFields += Project.INVITE_EXPIRES_AT
//                isInviteActive = true
            }
        }

//    var isInviteActive = isInviteActive
//        set(value) {
//            if (field != value) {
//                field = value
//                modifiedFields += Project.IS_INVITE_ACTIVE
//            }
//        }

    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    Project.TITLE -> put(Project.TITLE, JsonPrimitive(title))
                    Project.DESCRIPTION -> put(Project.DESCRIPTION, JsonPrimitive(description))
                    Project.INVITE_EXPIRES_AT -> put(Project.INVITE_EXPIRES_AT, JsonPrimitive(DateTimeConfig.fromInstant(inviteExpireDate)))
//                    Project.IS_INVITE_ACTIVE -> put(Project.IS_INVITE_ACTIVE, JsonPrimitive(isInviteActive))
                }
            }
        }

    init {
        this.title = title
        this.description = description
        //this.isInviteActive = isInviteActive
        this.inviteExpireDate = inviteExpireDate
        modifiedFields.clear()
    }
}

fun ProjectEntity.asDataModel(): Project {
    return Project(
        id= id,
        title= title,
        description= description,
        inviteCode= inviteCode,
        inviteExpiresAt= inviteExpireDate.toString(),
//        isInviteActive= isInviteActive,
        ownerId= ownerId,
        createdAt= createdAt.toString(),
    )
}

fun Project.asEntity(): ProjectEntity {
    return ProjectEntity(
        id = requireNotNull(id) { "Project ID cannot be null." },
        title = title,
        description = description ?: "",
        inviteCode = requireNotNull(inviteCode) { "Missing invite code "},
        inviteExpireDate = DateTimeConfig.toInstant(requireNotNull(inviteExpiresAt) {"Project invitation expiration date is null"} ),
//        isInviteActive = requireNotNull(isInviteActive) { "Project Active invitation status is null" },
        ownerId = ownerId,
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) {"Project created time is null"} )
    )
}