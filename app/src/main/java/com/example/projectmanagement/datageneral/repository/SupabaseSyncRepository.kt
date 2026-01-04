package com.example.projectmanagement.datageneral.repository

import com.example.projectmanagement.datageneral.data.model.project.Project as SupabaseProject
import com.example.projectmanagement.datageneral.data.model.task.Task as SupabaseTask
import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository as SupabaseProjectRepository
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository as SupabaseTaskRepository
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.data.model.Project
import com.example.projectmanagement.data.model.Task
import com.example.projectmanagement.data.repository.ProjectRepository as RoomProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unified repository that syncs data between Room (local) and Supabase (remote)
 */
class SupabaseSyncRepository(
    private val roomRepository: RoomProjectRepository,
    private val supabaseProjectRepo: SupabaseProjectRepository,
    private val supabaseTaskRepo: SupabaseTaskRepository,
    private val authRepository: AuthRepository
) {
    
    // ========== PROJECT SYNC ==========
    
    suspend fun insertProjectAndSync(project: Project): Long {
        return withContext(Dispatchers.IO) {
            // Cloud-first: Try to save to Supabase first
            try {
                val currentUser = authRepository.currentAuthUser
                if (currentUser != null) {
                    val supabaseProject = project.toSupabaseProject(currentUser.id)
                    supabaseProjectRepo.insertProject(supabaseProject)
                }
            } catch (e: Exception) {
                // Log error but don't fail - still save to Room for offline support
                e.printStackTrace()
                // Continue to save to Room even if Supabase fails
            }
            
            // Save to Room (always, even if Supabase failed)
            roomRepository.insertProject(project)
        }
    }
    
    suspend fun insertTaskAndSync(task: Task): Long {
        return withContext(Dispatchers.IO) {
            // Cloud-first: Try to save to Supabase first
            try {
                val supabaseTask = task.toSupabaseTask()
                supabaseTaskRepo.createTask(supabaseTask)
            } catch (e: Exception) {
                // Log error but don't fail - still save to Room for offline support
                e.printStackTrace()
                // Continue to save to Room even if Supabase fails
            }
            
            // Save to Room (always, even if Supabase failed)
            roomRepository.insertTask(task)
        }
    }
    
    // Mapping functions
    private fun Project.toSupabaseProject(ownerId: String): SupabaseProject {
        // Use existing invite code if available, otherwise generate new one
        val inviteCode = inviteCode ?: generateUniqueInviteCode()
        // Use existing creation date if available, otherwise use current date
        val createdAt = createdAt ?: java.time.LocalDate.now().toString()
        return SupabaseProject(
            id = null, // Let Supabase generate ID
            title = title,
            description = description,
            inviteCode = inviteCode,
            inviteExpiresAt = null,
            ownerId = ownerId,
            createdAt = createdAt
        )
    }
    
    private fun generateUniqueInviteCode(): String {
        // Generate a random alphanumeric code (8 characters)
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }
    
    private fun Task.toSupabaseTask(): SupabaseTask {
        return SupabaseTask(
            id = null, // Let Supabase generate ID
            projectId = projectId.toString(), // Convert Int to String - NOTE: This assumes projectId maps to a Supabase project
            title = title,
            description = description,
            assigneeId = null, // TODO: Map assigneeName to assigneeId
            status = status.name.lowercase(), // Convert enum to string
            priority = when (priority) {
                com.example.projectmanagement.data.model.TaskPriority.HIGH -> 1
                com.example.projectmanagement.data.model.TaskPriority.MEDIUM -> 2
                com.example.projectmanagement.data.model.TaskPriority.LOW -> 3
            },
            estimatedHour = 8,
            dueDate = deadline,
            createdAt = null,
            updatedAt = null
        )
    }
    
    // Delegate other methods to Room repository
    fun getAllProjects() = roomRepository.getAllProjects()
    fun getProjectById(id: Int) = roomRepository.getProjectById(id)
    suspend fun getProject(id: Int) = roomRepository.getProject(id)
    suspend fun getAllSupabaseProjects() = supabaseProjectRepo.getAllProjects()
    fun getAllTasks() = roomRepository.getAllTasks()
    fun getTaskById(id: Int) = roomRepository.getTaskById(id)
    fun getTasksByProjectId(projectId: Int) = roomRepository.getTasksByProjectId(projectId)
    suspend fun updateProject(project: Project) = withContext(Dispatchers.IO) {
        // Cloud-first: Update in Supabase first
        try {
            val currentUser = authRepository.currentAuthUser
            if (currentUser != null) {
                val supabaseProject = project.toSupabaseProject(currentUser.id)
                // TODO: Get project ID and update in Supabase
                // For now, update in Room
                roomRepository.updateProject(project)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        // Cloud-first: Update in Supabase first
        try {
            val supabaseTask = task.toSupabaseTask()
            // TODO: Update task in Supabase using task ID
            // For now, update in Room
            roomRepository.updateTask(task)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun deleteTask(taskId: Int, projectId: Int) = withContext(Dispatchers.IO) {
        // Cloud-first: Delete from Supabase first
        try {
            supabaseTaskRepo.deleteTask(taskId.toString(), projectId.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Fail if Supabase delete fails
        }
        
        // Then delete from Room
        roomRepository.deleteTask(taskId)
    }
    
    suspend fun deleteProject(id: Int) = withContext(Dispatchers.IO) {
        // Cloud-first: Delete from Supabase first
        try {
            supabaseProjectRepo.deleteProject(id.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Fail if Supabase delete fails
        }
        
        // Then delete from Room
        roomRepository.deleteProject(id)
    }
    
    suspend fun joinProject(inviteCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = authRepository.currentAuthUser
                if (currentUser != null) {
                    val project = supabaseProjectRepo.joinProject(inviteCode, currentUser.id)
                    if (project != null) {
                        // TODO: Sync project to Room database
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
