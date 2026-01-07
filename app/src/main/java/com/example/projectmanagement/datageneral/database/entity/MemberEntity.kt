package com.example.projectmanagement.datageneral.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_members")
data class MemberEntity(
    @PrimaryKey val userId: String,
    val projectId: String,
    val name: String,
    val email: String,
    val role: String
)
