package com.example.projectmanagement.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.projectmanagement.data.database.dao.ProjectDao
import com.example.projectmanagement.data.database.dao.TaskDao
import com.example.projectmanagement.data.database.entity.ProjectEntity
import com.example.projectmanagement.data.database.entity.TaskEntity
import com.example.projectmanagement.data.model.Project
import com.example.projectmanagement.data.model.Task
import com.example.projectmanagement.data.model.TaskPriority
import com.example.projectmanagement.data.model.TaskStatus

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) {
    fun getAllProjects(): LiveData<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getProjectById(id: String): LiveData<Project?> {
        return projectDao.getProjectById(id).map { it?.toDomain() }
    }

    suspend fun getProject(id: String): Project? {
        return projectDao.getProject(id)?.toDomain()
    }
    
    suspend fun insertProject(project: Project) {
        projectDao.insert(project.toEntity())
    }
    
    suspend fun updateProject(project: Project) {
        projectDao.update(project.toEntity())
    }
    
    suspend fun deleteProject(id: String) {
        projectDao.delete(id)
    }

    suspend fun clearAllProjects() {
        projectDao.clearAll()
    }
    
    fun getTasksByProjectId(projectId: String): LiveData<List<Task>> {
        return taskDao.getTasksByProjectId(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getTaskById(id: String): LiveData<Task?> {
        return taskDao.getTaskById(id).map { it?.toDomain() }
    }
    
    suspend fun getTask(id: String): Task? {
        return taskDao.getTask(id)?.toDomain()
    }
    
    fun getTasksByStatus(status: TaskStatus): LiveData<List<Task>> {
        return taskDao.getTasksByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun insertTask(task: Task) {
        taskDao.insert(task.toEntity())
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }
    
    suspend fun deleteTask(id: String) {
        taskDao.delete(id)
    }
    
    // Extension functions for mapping
    private fun ProjectEntity.toDomain(): Project {
        return Project(
            id = id,
            title = title,
            description = description,
            inviteCode = inviteCode,
            createdAt = createdAt
        )
    }
    
    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            title = title,
            description = description,
            inviteCode = inviteCode,
            createdAt = createdAt
        )
    }
    
    private fun TaskEntity.toDomain(): Task {
        return Task(
            id = id,
            projectId = projectId,
            title = title,
            description = description,
            status = TaskStatus.valueOf(status),
            priority = TaskPriority.valueOf(priority),
            deadline = deadline,
            assigneeName = assigneeName
        )
    }
    
    private fun Task.toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            projectId = projectId,
            title = title,
            description = description,
            status = status.name,
            priority = priority.name,
            deadline = deadline,
            assigneeName = assigneeName
        )
    }
}
