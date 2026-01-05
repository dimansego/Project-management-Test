package com.example.projectmanagement.datageneral.repository

import android.util.Log
import com.example.projectmanagement.datageneral.data.model.project.Project as SupabaseProject
import com.example.projectmanagement.datageneral.data.model.task.Task as SupabaseTask
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.repository.meeting.MeetingRepository
import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository as SupabaseProjectRepository
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository as SupabaseTaskRepository
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.data.model.Project
import com.example.projectmanagement.data.model.Task
import com.example.projectmanagement.data.model.TaskPriority
import com.example.projectmanagement.data.model.TaskStatus
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.data.repository.ProjectRepository as RoomProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

/**
 * Unified repository that syncs data between Room (local) and Supabase (remote)
 */
class SupabaseSyncRepository(
    private val roomRepository: RoomProjectRepository,
    private val supabaseProjectRepo: SupabaseProjectRepository,
    private val supabaseTaskRepo: SupabaseTaskRepository,
    private val meetingRepository: MeetingRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    
    // ========== PROJECT SYNC ==========
    
    suspend fun insertProjectAndSync(project: Project): Project {
        return withContext(Dispatchers.IO) {
            // Generate UUID for new project if not already set
            val projectWithId = if (project.id.isEmpty() || project.id == "0") {
                project.copy(id = UUID.randomUUID().toString())
            } else {
                project
            }
            
            // Cloud-first: Save to Supabase first and verify success
            var supabaseProject: SupabaseProject? = null
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser == null) {
                    throw IllegalStateException("User not authenticated")
                }
                
                val supabaseProjectToInsert = projectWithId.toSupabaseProject(currentUser.id!!)
                supabaseProject = supabaseProjectRepo.insertProject(supabaseProjectToInsert)
                
                // Log success
                Log.d("SupabaseSync", "Project successfully saved to Supabase: ${supabaseProject.id}")
                
            } catch (e: Exception) {
                // Enhanced error logging
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                    e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                    e.message?.contains("400") == true -> "Bad Request: Invalid data format"
                    else -> "Error: ${e.message}"
                }
                Log.e("SupabaseSync", "Failed to save project to Supabase: $errorMessage", e)
                throw Exception("Failed to sync project to Supabase: $errorMessage", e)
            }
            
            // Only save to Room if Supabase succeeded
            val finalProject = if (supabaseProject != null) {
                // Use the project returned from Supabase (with invite_code and other fields)
                Project(
                    id = supabaseProject.id ?: projectWithId.id,
                    title = supabaseProject.title,
                    description = supabaseProject.description ?: "",
                    inviteCode = supabaseProject.inviteCode,
                    createdAt = supabaseProject.createdAt
                )
            } else {
                projectWithId
            }
            
            roomRepository.insertProject(finalProject)
            finalProject
        }
    }
    
    suspend fun insertTaskAndSync(task: Task): Task {
        return withContext(Dispatchers.IO) {
            // Generate UUID for new task if not already set
            val taskWithId = if (task.id.isEmpty() || task.id == "0") {
                task.copy(id = UUID.randomUUID().toString())
            } else {
                task
            }
            
            // Cloud-first: Save to Supabase first and verify success
            var supabaseTask: SupabaseTask? = null
            try {
                val supabaseTaskToInsert = taskWithId.toSupabaseTask()
                supabaseTask = supabaseTaskRepo.createTask(supabaseTaskToInsert)
                
                // Log success
                Log.d("SupabaseSync", "Task successfully saved to Supabase: ${supabaseTask.id}")
                
            } catch (e: Exception) {
                // Enhanced error logging
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                    e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                    e.message?.contains("400") == true -> "Bad Request: Invalid data format"
                    else -> "Error: ${e.message}"
                }
                Log.e("SupabaseSync", "Failed to save task to Supabase: $errorMessage", e)
                throw Exception("Failed to sync task to Supabase: $errorMessage", e)
            }
            
            // Only save to Room if Supabase succeeded
            val finalTask = if (supabaseTask != null) {
                // Use the task returned from Supabase
                Task(
                    id = supabaseTask.id ?: taskWithId.id,
                    projectId = supabaseTask.projectId,
                    title = supabaseTask.title,
                    description = supabaseTask.description ?: "",
                    status = com.example.projectmanagement.data.model.TaskStatus.valueOf(
                        supabaseTask.status?.uppercase() ?: "TODO"
                    ),
                    priority = when (supabaseTask.priority) {
                        1 -> com.example.projectmanagement.data.model.TaskPriority.HIGH
                        2 -> com.example.projectmanagement.data.model.TaskPriority.MEDIUM
                        3 -> com.example.projectmanagement.data.model.TaskPriority.LOW
                        else -> com.example.projectmanagement.data.model.TaskPriority.MEDIUM
                    },
                    deadline = supabaseTask.dueDate,
                    assigneeName = "" // Assignee name not in Supabase model
                )
            } else {
                taskWithId
            }
            
            roomRepository.insertTask(finalTask)
            finalTask
        }
    }
    
    // Mapping functions
    private fun Project.toSupabaseProject(ownerId: String): SupabaseProject {
        // Use existing invite code if available, otherwise generate new one
        val inviteCode = inviteCode ?: generateUniqueInviteCode()
        // Use existing creation date if available, otherwise use current date
        val createdAt = createdAt ?: java.time.LocalDate.now().toString()
        return SupabaseProject(
            id = if (id.isEmpty() || id == "0") null else id, // Use UUID if set, otherwise let Supabase generate
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
            id = if (id.isEmpty() || id == "0") null else id, // Use UUID if set, otherwise let Supabase generate
            projectId = projectId, // Already String UUID
            title = title,
            description = description,
            assigneeId = null,
            status = status.name.lowercase(),
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
    fun getProjectById(id: String) = roomRepository.getProjectById(id)
    suspend fun getProject(id: String) = roomRepository.getProject(id)
    suspend fun getAllSupabaseProjects() = supabaseProjectRepo.getAllProjects()
    fun getAllTasks() = roomRepository.getAllTasks()
    fun getTaskById(id: String) = roomRepository.getTaskById(id)
    suspend fun getTask(id: String) = roomRepository.getTask(id)
    fun getTasksByProjectId(projectId: String) = roomRepository.getTasksByProjectId(projectId)
    suspend fun updateProject(project: Project) = withContext(Dispatchers.IO) {
        // Cloud-first: Update in Supabase first
        try {
            val currentUser = authRepository.currentAuthUser
            if (currentUser == null) {
                throw IllegalStateException("User not authenticated")
            }
            
            val supabaseProject = project.toSupabaseProject(currentUser.id)
            val updatedProject = supabaseProjectRepo.updateProject(project.id, 
                kotlinx.serialization.json.buildJsonObject {
                    put("title", supabaseProject.title)
                    put("description", supabaseProject.description)
                    put("invite_code", supabaseProject.inviteCode)
                }
            )
            
            Log.d("SupabaseSync", "Project successfully updated in Supabase: ${updatedProject.id}")
            
            // Update in Room with Supabase response
            val updatedDomainProject = Project(
                id = updatedProject.id ?: project.id,
                title = updatedProject.title,
                description = updatedProject.description ?: "",
                inviteCode = updatedProject.inviteCode,
                createdAt = updatedProject.createdAt
            )
            roomRepository.updateProject(updatedDomainProject)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                else -> "Error: ${e.message}"
            }
            Log.e("SupabaseSync", "Failed to update project in Supabase: $errorMessage", e)
            throw Exception("Failed to update project in Supabase: $errorMessage", e)
        }
    }
    
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        // Cloud-first: Update in Supabase first
        try {
            val supabaseTask = task.toSupabaseTask()
            val updatedTask = supabaseTaskRepo.updateTask(task.id, task.projectId,
                kotlinx.serialization.json.buildJsonObject {
                    put("title", supabaseTask.title)
                    put("description", supabaseTask.description)
                    put("status", supabaseTask.status)
                    put("priority", supabaseTask.priority)
                    put("due_date", supabaseTask.dueDate)
                }
            )
            
            Log.d("SupabaseSync", "Task successfully updated in Supabase: ${updatedTask.id}")
            
            // Update in Room with Supabase response
            val updatedDomainTask = Task(
                id = updatedTask.id ?: task.id,
                projectId = updatedTask.projectId,
                title = updatedTask.title,
                description = updatedTask.description ?: "",
                status = com.example.projectmanagement.data.model.TaskStatus.valueOf(
                    updatedTask.status?.uppercase() ?: "TODO"
                ),
                priority = when (updatedTask.priority) {
                    1 -> com.example.projectmanagement.data.model.TaskPriority.HIGH
                    2 -> com.example.projectmanagement.data.model.TaskPriority.MEDIUM
                    3 -> com.example.projectmanagement.data.model.TaskPriority.LOW
                    else -> com.example.projectmanagement.data.model.TaskPriority.MEDIUM
                },
                deadline = updatedTask.dueDate,
                assigneeName = task.assigneeName
            )
            roomRepository.updateTask(updatedDomainTask)
            updatedDomainTask
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                else -> "Error: ${e.message}"
            }
            Log.e("SupabaseSync", "Failed to update task in Supabase: $errorMessage", e)
            throw Exception("Failed to update task in Supabase: $errorMessage", e)
        }
    }
    
    suspend fun deleteTask(taskId: String, projectId: String) = withContext(Dispatchers.IO) {
        // Cloud-first: Delete from Supabase first
        try {
            supabaseTaskRepo.deleteTask(taskId, projectId)
            Log.d("SupabaseSync", "Task successfully deleted from Supabase: $taskId")
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                e.message?.contains("400") == true -> "Bad Request: Invalid task ID"
                else -> "Error: ${e.message}"
            }
            Log.e("SupabaseSync", "Failed to delete task from Supabase: $errorMessage", e)
            throw Exception("Failed to delete task from Supabase: $errorMessage", e)
        }
        
        // Then delete from Room
        roomRepository.deleteTask(taskId)
    }
    
    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        // Cloud-first: Delete from Supabase first
        try {
            supabaseProjectRepo.deleteProject(id)
            Log.d("SupabaseSync", "Project successfully deleted from Supabase: $id")
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                e.message?.contains("400") == true -> "Bad Request: Invalid project ID"
                else -> "Error: ${e.message}"
            }
            Log.e("SupabaseSync", "Failed to delete project from Supabase: $errorMessage", e)
            throw Exception("Failed to delete project from Supabase: $errorMessage", e)
        }
        
        // Then delete from Room
        roomRepository.deleteProject(id)
    }

    suspend fun joinProject(inviteCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // FIX: Call the new function in the repo instead of accessing .client directly
                val supabaseProject = supabaseProjectRepo.joinProjectByCode(inviteCode)

                if (supabaseProject != null) {
                    // Sync to Room (Local DB)
                    // Note: We use !! on ID because the RPC guarantees a valid project return
                    val domainProject = com.example.projectmanagement.data.model.Project(
                        id = supabaseProject.id ?: UUID.randomUUID().toString(),
                        title = supabaseProject.title,
                        description = supabaseProject.description ?: "",
                        inviteCode = supabaseProject.inviteCode,
                        createdAt = supabaseProject.createdAt
                    )

                    roomRepository.insertProject(domainProject)
                    return@withContext true
                }

                return@withContext false

            } catch (e: Exception) {
                // Error handling
                val msg = e.message ?: ""
                when {
                    msg.contains("INVALID_CODE") -> Log.e("Join", "Invalid Invite Code")
                    msg.contains("ALREADY_MEMBER") -> Log.e("Join", "You are already a member")
                    msg.contains("CODE_EXPIRED") -> Log.e("Join", "Invite Code Expired")
                    else -> Log.e("Join", "Error: $msg")
                }
                return@withContext false
            }
        }
    }
    
    // ========== MEETING METHODS ==========
    
    suspend fun getMeetingsByProjectId(projectId: String): List<Meeting> {
        return withContext(Dispatchers.IO) {
            meetingRepository.getMeetingsByProjectId(projectId)
        }
    }
    
    suspend fun getMeetingsByAuthId(authId: String): List<Meeting> {
        return withContext(Dispatchers.IO) {
            meetingRepository.getMeetingsByCreatedBy(authId)
        }
    }

    suspend fun getAllMeetingsForCurrentUser(): List<Meeting> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser == null) return@withContext emptyList()

                // This calls your existing getMeetingsByAuthId method
                // which filters meetings where created_by == userId
                meetingRepository.getMeetingsByCreatedBy(currentUser.id!!)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Failed to fetch all meetings: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun insertMeetingAndSync(meeting: Meeting): Meeting {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Validate Auth via Supabase
                val currentUser = userRepository.getCurrentUser()
                    if(currentUser == null) {
                        throw IllegalStateException("User not authenticated in Supabase")
                    }

                val meetingToInsert = meeting.copy(
                    id = null, // Ensure ID is null so Supabase generates a UUID
                    createdBy = currentUser.id!!
                )

                val result = meetingRepository.createMeeting(meetingToInsert)

                Log.d("SupabaseSync", "Meeting successfully saved: ${result.id}")
                result
            } catch (e: Exception) {
                val errorMessage = when {
                    // If is_project_member fails, Supabase returns a 403
                    e.message?.contains("403") == true ->
                        "Access Denied: You are not a member of this project."
                    else -> e.message ?: "Unknown Error"
                }
                Log.e("SupabaseSync", "Failed to sync meeting: $errorMessage", e)
                throw Exception(errorMessage, e)
            }
        }
    }

    suspend fun deleteMeeting(meetingId: String, projectId: String) {
        return withContext(Dispatchers.IO) {
            try {
                // CRITICAL FIX: If projectId is empty (global view),
                // we cannot use the 'deleteMeeting(id, projectId)' composite filter.
                if (projectId.isEmpty()) {
                    // You should add a deleteByIdOnly method to your meetingRepository
                    // Or handle it by fetching the meeting first to get its projectId
                    throw Exception("Cannot delete meeting: Missing Project ID context.")
                }

                meetingRepository.deleteMeeting(meetingId, projectId)
                Log.d("SupabaseSync", "Meeting successfully deleted: $meetingId")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown Error"
                Log.e("SupabaseSync", "Failed to delete meeting: $errorMessage", e)
                throw Exception("Failed to delete meeting: $errorMessage", e)
            }
        }
    }

    suspend fun getMeetingById(meetingId: String, projectId: String): Meeting? {
        // SAFETY: Prevent invalid UUID crash
        if (projectId.isEmpty() || meetingId.isEmpty()) return null

        return withContext(Dispatchers.IO) {
            try {
                meetingRepository.getMeetingById(meetingId, projectId)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Failed to get meeting by ID: ${e.message}", e)
                throw Exception("Failed to get meeting: ${e.message}", e)
            }
        }
    }

    suspend fun updateMeetingAndSync(meetingId: String, projectId: String, title: String, description: String?, location: String, startTimeIso: String, endTimeIso: String): Meeting {
        return withContext(Dispatchers.IO) {
            try {
                val updates = buildJsonObject {
                    put(Meeting.TITLE, title.trim())
                    put(Meeting.LOCATION, location.trim())
                    put(Meeting.START_TIME, startTimeIso)
                    put(Meeting.END_TIME, endTimeIso)
                    if (description != null && description.isNotEmpty()) {
                        put(Meeting.DESCRIPTION, description.trim())
                    }
                }
                val result = meetingRepository.updateMeeting(meetingId, projectId, updates)
                Log.d("SupabaseSync", "Meeting successfully updated: ${result.id}")
                result
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Unauthorized: Check your authentication"
                    e.message?.contains("403") == true -> "Forbidden: You don't have permission"
                    e.message?.contains("400") == true -> "Bad Request: Invalid data format"
                    else -> e.message ?: "Unknown Error"
                }
                Log.e("SupabaseSync", "Failed to update meeting: $errorMessage", e)
                throw Exception("Failed to update meeting: $errorMessage", e)
            }
        }
    }

    // == LOGIN INITIAL PROJECT FETCH

    suspend fun initialSync() {
        syncProjectsFromSupabase()
        syncTasksFromSupabase()
    }

    private suspend fun syncProjectsFromSupabase() {
        val supabaseProjects = supabaseProjectRepo.getAllProjects()

        for (supabaseProject in supabaseProjects) {
            roomRepository.insertProject(Project(
                id = supabaseProject.id!!,
                title = supabaseProject.title,
                description = supabaseProject.description ?: "",
                inviteCode = supabaseProject.inviteCode,
                createdAt = supabaseProject.createdAt
            ))
        }
    }

    // == LOGIN INITIAL TASK FETCH

    private suspend fun syncTasksFromSupabase() {
        val tasks = supabaseTaskRepo.getAllTasks()

        for (task in tasks) {
            roomRepository.insertTask(Task(
                id = task.id!!,
                projectId = task.projectId,
                title = task.title,
                description = task.description ?: "",
                status = TaskStatus.valueOf(task.status!!.uppercase()),
                priority = when (task.priority) {
                    1 -> TaskPriority.HIGH
                    2 -> TaskPriority.MEDIUM
                    3 -> TaskPriority.LOW
                    else -> TaskPriority.MEDIUM
                },
                deadline = task.dueDate,
                assigneeName = task.assigneeId ?: ""
            ))
        }
    }

}
