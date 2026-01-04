package com.example.projectmanagement.datageneral.data.repository.project

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.project.ProjectMember
import kotlinx.serialization.json.*

class ProjectMemberRepository(private val client: SupabaseClient) {
    suspend fun addMember(
        projectId: String,
        userId: String,
        role: String? = null
    ): ProjectMember {
        val payload = buildJsonObject {
            put(ProjectMember.PROJECT_ID, projectId)
            put(ProjectMember.USER_ID, userId)
            role?.let {
                put(ProjectMember.ROLE, it)
            }
        }

        return client.db.from(ProjectMember.PROJECT_MEMBERS)
            .insert(payload) {
                select()   // ensure returned row is fetched
            }.decodeSingle<ProjectMember>()
    }

    suspend fun getMembersByProjectId(projectId: String): List<ProjectMember> {
        return client.db.from(ProjectMember.PROJECT_MEMBERS).select {
            filter {
                ProjectMember::projectId eq projectId
            }
        }.decodeList<ProjectMember>()
    }

    suspend fun removeMember(projectId: String, userId: String) {
        client.db.from(ProjectMember.PROJECT_MEMBERS).delete {
            filter {
                and {
                    ProjectMember::projectId eq projectId
                    ProjectMember::userId eq userId
                }
            }
        }
    }

    suspend fun isMember(projectId: String, userId: String): Boolean {
        val result = client.db.from(ProjectMember.PROJECT_MEMBERS)
            .select {
                filter {
                    and {
                        ProjectMember::projectId eq projectId
                        ProjectMember::userId eq userId
                    }
                }
                limit(1)
            }.decodeList<ProjectMember>()

        return result.isNotEmpty()
    }

    suspend fun updateRole(projectId: String, userId: String, newRole: String) {
        val payload = buildJsonObject {
            put(ProjectMember.ROLE, newRole)
        }

        client.db.from(ProjectMember.PROJECT_MEMBERS).update(payload) {
            filter {
                and {
                    ProjectMember::projectId eq projectId
                    ProjectMember::userId eq userId
                }
            }
        }
    }
}
