package com.example.projectmanagement.datageneral.domain.entity.user

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

class AppUserEntity(
    val id: String,
    val authId: String,
    val email: String,
    name: String,
    val createdAt: Instant
) : Updatable {
    companion object {
        const val MAX_NAME_LENGTH = 32
    }

    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "name cannot be blank" }
            require(value.length <= MAX_NAME_LENGTH) { "name length must be <= $MAX_NAME_LENGTH" }
            if (field != value) {
                field = value
                modifiedFields += AppUser.NAME
            }
        }
    override val modifiedFields: MutableSet<String> = mutableSetOf()

    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            if (AppUser.NAME in modifiedFields) {
                put(AppUser.NAME, JsonPrimitive(name))
            }
        }

    init {
        this.name = name
        modifiedFields.clear()
    }
}

fun AppUserEntity.asDataModel(): AppUser {
    return AppUser(
        id = id,
        authId = authId,
        email = email,
        name = name,
        createdAt = DateTimeConfig.fromInstant(this.createdAt)
    )
}

fun AppUser.asEntity(): AppUserEntity {
    return AppUserEntity(
        id = requireNotNull(id) { "User ID cannot be null." },
        authId = authId,
        email = email,
        name = requireNotNull(name) { "User Name cannot be null." },
        createdAt = DateTimeConfig.toInstant(requireNotNull(createdAt) {"User created time is null"} )
    )
}