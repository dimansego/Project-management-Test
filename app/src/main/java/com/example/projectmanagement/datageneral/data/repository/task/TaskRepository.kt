package com.example.projectmanagement.datageneral.data.repository.task

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.project.Project
import com.example.projectmanagement.datageneral.data.model.task.Task
import kotlinx.serialization.json.JsonObject

open class TaskRepository(private val client: SupabaseClient) {
    suspend fun getAllTasks(): List<Task> {
        return client.db.from(Task.TASKS).select().decodeList<Task>()
    }

    suspend fun getTaskById(id: String, projectId: String): Task? {
        return client.db.from(Task.TASKS).select {
            filter {
                and {
                    Task::id eq id
                    Task::projectId eq projectId
                }
            }
        }.decodeSingleOrNull<Task>()
    }

    suspend fun getTaskByName(name: String, projectId: String): Task? {
        return client.db.from(Task.TASKS).select {
            filter {
                and {
                    Task::title ilike name
                    Task::projectId eq projectId
                }
            }
        }.decodeSingleOrNull<Task>()
    }

    suspend fun getTasksByProjectId(projectId: String): List<Task> {
        return client.db.from(Task.TASKS).select() {
            filter {
                Task::projectId eq projectId
            }
        }.decodeList<Task>()
    }

    open suspend fun createTask(task: Task): Task {
        return client.db.from(Task.TASKS).insert(task) {
            select()
        }.decodeSingle<Task>()
    }

    open suspend fun deleteTask(taskId: String, projectId: String) {
        client.db.from(Task.TASKS).delete {
            filter {
                and {
                    Task::id eq taskId
                    Task::projectId eq projectId
                }
            }
        }
    }

    open suspend fun updateTask(taskId: String, projectId: String, updates: JsonObject): Task {
        return client.db.from(Task.TASKS).update(updates) {
            filter {
                and {
                    Task::id eq taskId
                    Task::projectId eq projectId
                }
            }
            select()
        }.decodeSingle<Task>()
    }

//    suspend fun getTasksByTaskCategoryId(categoryId: String, projectId: String): List<Task> {
//        return client.db.from(Task.TASKS).select() {
//            filter {
//                and {
//                    Task::categoryId eq categoryId
//                    Task::projectId eq projectId
//                }
//            }
//        }.decodeList<Task>()
//    }

    suspend fun getTasksByAssignee(projectId: String, assigneeId: String): List<Task> {
        return client.db.from(Task.TASKS).select {
            filter {
                and {
                    Task::projectId eq projectId
                    Task::assigneeId eq assigneeId
                }
            }
        }.decodeList<Task>()
    }

    suspend fun getTasksByStatus(projectId: String, status: String): List<Task> {
        return client.db.from(Task.TASKS).select {
            filter {
                and {
                    Task::projectId eq projectId
                    Task::status eq status
                }
            }
        }.decodeList<Task>()
    }
}