package com.example.projectmanagement.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projectmanagement.data.database.dao.ProjectDao
import com.example.projectmanagement.data.database.dao.TaskDao
import com.example.projectmanagement.data.database.dao.UserDao
import com.example.projectmanagement.data.database.entity.ProjectEntity
import com.example.projectmanagement.data.database.entity.TaskEntity
import com.example.projectmanagement.data.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, ProjectEntity::class, TaskEntity::class],
    version = 7, // Increased to 7 for invite_code field and removal of due_date/assignee columns
    exportSchema = false
)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    
    companion object {
        const val DATABASE_NAME = "project_database"
    }
}

