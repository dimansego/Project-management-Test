package com.example.projectmanagement.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "invite_code")
    val inviteCode: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

