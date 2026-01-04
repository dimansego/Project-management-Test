package com.example.projectmanagement.datageneral.data.repository.task

import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.model.task.Task
import com.example.projectmanagement.datageneral.data.model.task.TaskDependency

open class TaskDependencyRepository(private val client: SupabaseClient) {
//    suspend fun getAllDependencies(): List<TaskDependency> {
//        return client.db.from(TaskDependency.TASK_DEPENDENCIES).select().decodeList<TaskDependency>()
//    }

    suspend fun getDependenciesByProjectId(projectId: String): List<TaskDependency> {
        return client.db.from(TaskDependency.TASK_DEPENDENCIES).select {
            filter {
                TaskDependency::projectId eq projectId
            }
        }.decodeList<TaskDependency>()
    }

    suspend fun getPredecessorsOf(successorId: String, projectId: String): List<Pair<Task, String>> {
        val response = client.db.from(TaskDependency.TASK_DEPENDENCIES).select() {
            filter {
                and {
                    TaskDependency::successorId eq successorId
                    TaskDependency::projectId eq projectId
                }
            }
        }.decodeList<TaskDependency>()

        if (response.isEmpty()) return emptyList()

        val predIds = response.map { it.predecessorId }
        val preds = client.db.from(Task.TASKS).select() {
            filter {
                Task::id isIn predIds
            }
        }.decodeList<Task>().associateBy { it.id }

        return response.mapNotNull { dep -> preds[dep.predecessorId]?.let { it to dep.dependencyType } }
    }

    suspend fun getSuccessorsOf(predecessorId: String, projectId: String): List<Pair<Task, String>> {
        val response = client.db.from(TaskDependency.TASK_DEPENDENCIES).select() {
            filter {
                and {
                    TaskDependency::predecessorId eq predecessorId
                    TaskDependency::projectId eq projectId
                }
            }
        }.decodeList<TaskDependency>()

        if (response.isEmpty()) return emptyList()

        val sucIds = response.map { it.successorId }
        val sucs = client.db.from(Task.TASKS).select() {
            filter {
                Task::id isIn sucIds
            }
        }.decodeList<Task>().associateBy { it.id }

        return response.mapNotNull { dep -> sucs[dep.successorId]?.let { it to dep.dependencyType } }
    }

    open suspend fun createDependency(dependency: TaskDependency): TaskDependency {
        return client.db.from(TaskDependency.TASK_DEPENDENCIES).insert(dependency) {
            select()
        }.decodeSingle<TaskDependency>()
    }

    open suspend fun checkStartViolations(taskId: String, projectId: String): Boolean {
        val preds = this.getPredecessorsOf(taskId, projectId)

        for ((pred, depType) in preds) {
            when (depType) {
                "finish_to_start" -> {
                    if (pred.status != "done") return true
                }
                "start_to_start" -> {
                    if (pred.status == "todo" || pred.status == "blocked") return true
                }
            }
        }

        return false
    }

    open suspend fun checkFinishViolations(taskId: String, projectId: String): Boolean {
        val preds = getPredecessorsOf(taskId, projectId)

        for ((pred, depType) in preds) {
            when (depType) {
                "finish_to_finish" -> {
                    if (pred.status != "done") return true
                }
                "start_to_finish" -> {
                    if (pred.status == "todo" || pred.status == "blocked") return true
                }
            }
        }

        return false
    }
}