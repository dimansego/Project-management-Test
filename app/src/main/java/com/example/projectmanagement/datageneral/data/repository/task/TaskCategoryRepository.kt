package com.example.projectmanagement.datageneral.data.repository.task

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.task.TaskCategory

@Deprecated("Table ${TaskCategory.TASK_CATEGORIES} no longer used")
class TaskCategoryRepository(private val client: SupabaseClient) {
//    suspend fun getAllTaskCategories(): List<TaskCategory> {
//        return client.db.from(TaskCategory.TASK_CATEGORIES).select().decodeList<TaskCategory>()
//    }

    suspend fun getTaskCategoryById(id: String, projectId: String): TaskCategory? {
        return client.db.from(TaskCategory.TASK_CATEGORIES).select {
            filter {
                and {
                    TaskCategory::id eq id
                    TaskCategory::projectId eq projectId
                }
            }
        }.decodeSingleOrNull<TaskCategory>()
    }

    suspend fun getTaskCategoryByName(name: String, projectId: String): TaskCategory? {
        return client.db.from(TaskCategory.TASK_CATEGORIES).select {
            filter {
                and {
                    TaskCategory::name ilike "%$name%"
                    TaskCategory::projectId eq projectId
                }
            }
        }.decodeSingleOrNull<TaskCategory>()
    }

    suspend fun createTaskCategory(taskCategory: TaskCategory): TaskCategory {
        return client.db.from(TaskCategory.TASK_CATEGORIES).insert(taskCategory){
            select()
        }.decodeSingle<TaskCategory>()
    }

    suspend fun getTaskCategoriesByProjectId(projectId: String): List<TaskCategory> {
        return client.db.from(TaskCategory.TASK_CATEGORIES).select {
            filter {
                TaskCategory::projectId eq projectId
            }
        }.decodeList<TaskCategory>()
    }

    suspend fun deleteTaskCategory(id: String, projectId: String) {
        client.db.from(TaskCategory.TASK_CATEGORIES).delete {
            filter {
                and {
                    TaskCategory::id eq id
                    TaskCategory::projectId eq projectId
                }
            }
        }
    }
}