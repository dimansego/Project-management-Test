package com.example.projectmanagement.datageneral.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["project_id"])]
)
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "project_id")
    val projectId: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "status")
    val status: String, // Store as String, convert to/from TaskStatus enum
    
    @ColumnInfo(name = "priority")
    val priority: String, // Store as String, convert to/from TaskPriority enum
    
    @ColumnInfo(name = "deadline")
    val deadline: String,
    
    @ColumnInfo(name = "assignee_name")
    val assigneeName: String,
    
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

