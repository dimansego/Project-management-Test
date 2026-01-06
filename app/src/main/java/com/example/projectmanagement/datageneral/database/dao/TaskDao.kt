package com.example.projectmanagement.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projectmanagement.data.database.entity.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE project_id = :projectId ORDER BY id DESC")
    fun getTasksByProjectId(projectId: String): LiveData<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: String): LiveData<TaskEntity?>
    
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTask(id: String): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY deadline ASC")
    fun getTasksByStatus(status: String): LiveData<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): LiveData<List<TaskEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)
    
    @Update
    suspend fun update(task: TaskEntity)
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: String)
}

