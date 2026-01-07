package com.example.projectmanagement.datageneral.domain.entity.project

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.project.ProjectMember
import java.time.Instant

class ProjectMemberEntity(
    val projectId: String,
    val userId: String,
    val role: ProjectRole,
    val joinedAt: Instant
)

fun ProjectMember.asEntity(): ProjectMemberEntity {
    return ProjectMemberEntity(
        projectId = projectId,
        userId = userId,
        role = ProjectRole.fromValue(requireNotNull(role) {"Role is null"})
            ?: error("Unknown Role Error: Role does not exist"),
        joinedAt = DateTimeConfig.toInstant(requireNotNull(joinedAt) {"Project joined time is null"})
    )
}

fun ProjectMemberEntity.asDataModel(): ProjectMember {
    return ProjectMember(
        projectId = projectId,
        userId = userId,
        role = role.value,
        joinedAt = DateTimeConfig.fromInstant(joinedAt)
    )
}