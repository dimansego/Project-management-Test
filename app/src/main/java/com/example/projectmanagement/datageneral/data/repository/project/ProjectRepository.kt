package com.example.projectmanagement.datageneral.data.repository.project

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.project.Project
import com.example.projectmanagement.datageneral.data.model.project.ProjectMember
import kotlinx.serialization.json.JsonObject

class ProjectRepository(private val client: SupabaseClient) {
    suspend fun getAllProjects(): List<Project> {
        return client.db.from(Project.PROJECTS).select().decodeList<Project>()
    }

    suspend fun getProjectById(id: String): Project? {
        return client.db.from(Project.PROJECTS).select() {
            filter {
                Project::id eq id
            }
        }.decodeSingleOrNull<Project>()
    }

    suspend fun getProjectByName(name: String): List<Project> {
        return client.db.from(Project.PROJECTS).select() {
            filter {
                Project::title ilike "%$name%"
            }
        }.decodeList<Project>()
    }

    suspend fun getProjectByInviteCode(inviteCode: String): Project? {
        return client.db.from(Project.PROJECTS).select() {
            filter {
                Project::inviteCode eq inviteCode
            }
        }.decodeSingleOrNull()
    }

    suspend fun insertProject(project: Project): Project {
        // Generate unique invite code if not provided
        val projectWithInviteCode = if (project.inviteCode.isNullOrEmpty()) {
            project.copy(inviteCode = generateUniqueInviteCode())
        } else {
            project
        }
        return client.db.from(Project.PROJECTS).insert(projectWithInviteCode) {
            select()
        }.decodeSingle<Project>()
    }
    
    private fun generateUniqueInviteCode(): String {
        // Generate a random alphanumeric code (8 characters)
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }

    suspend fun updateProject(projectId: String, updates: JsonObject): Project {
        return client.db.from(Project.PROJECTS).update(updates) {
            filter {
                Project::id eq projectId
            }
        }.decodeSingle<Project>()
    }

    suspend fun deleteProject(projectId: String) {
        client.db.from(Project.PROJECTS).delete {
            filter {
                Project::id eq projectId
            }
        }
    }

    suspend fun joinProject(inviteCode: String, userId: String): Project? {
        return try {
            // First, find the project by invite code
            val project = getProjectByInviteCode(inviteCode)
            if (project != null) {
                // Add user to project_members table
                client.db.from(ProjectMember.PROJECT_MEMBERS).insert(
                    ProjectMember(
                        projectId = project.id!!,
                        userId = userId,
                        role = "member",
                        joinedAt = null
                    )
                )
                project
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}