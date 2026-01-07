package com.example.projectmanagement.datageneral.data.repository.project

import android.util.Log
import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.project.Project
import com.example.projectmanagement.datageneral.data.model.project.ProjectMember
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import com.example.projectmanagement.datageneral.data.model.project.Project as SupabaseProject
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.JsonObject

class ProjectRepository(private val client: SupabaseClient) {
    suspend fun getAllProjects(): List<Project> {
        return client.db.from(Project.PROJECTS).select().decodeList<Project>()
    }

    suspend fun getProjectMembers(projectId: String): List<ProjectMember> {
        return client.db.from(ProjectMember.PROJECT_MEMBERS)
            .select(io.github.jan.supabase.postgrest.query.Columns.raw("*, app_users(*)")) {
                // Make sure "app_users" here matches @SerialName("app_users")
                filter {
                    ProjectMember::projectId eq projectId
                }
            }.decodeList<ProjectMember>()
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
    suspend fun leaveProject(projectId: String, userId: String) {
        client.db.from(ProjectMember.PROJECT_MEMBERS).delete { //
            filter {
                ProjectMember::projectId eq projectId //
                ProjectMember::userId eq userId //
            }
        }
    }
    
    private fun generateUniqueInviteCode(): String {
        // Generate a random alphanumeric code (8 characters)
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }

    suspend fun updateProject(projectId: String, updates: JsonObject): SupabaseProject {
        // .update() must be followed by .select() to return data for decodeSingle()
        return client.db.from(SupabaseProject.PROJECTS).update(updates) {
            filter {
                SupabaseProject::id eq projectId
            }
            // This forces Supabase to return the updated row in the response body
            select()
        }.decodeSingle<SupabaseProject>()
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
    suspend fun joinProjectByCode(inviteCode: String): Project? {
        return try {
            val params = mapOf("p_invite_code" to inviteCode)
            val result = client.db.rpc("join_project_by_code", params).data
            Log.e(null, result)
            getProjectById(result.substring(1, result.length - 1))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}