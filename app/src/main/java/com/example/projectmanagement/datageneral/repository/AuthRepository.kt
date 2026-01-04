package com.example.projectmanagement.data.repository

import com.example.projectmanagement.data.database.dao.UserDao
import com.example.projectmanagement.data.database.entity.UserEntity
import com.example.projectmanagement.data.model.User

class AuthRepository(
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): User? {
        val userEntity = userDao.getUserByEmailAndPassword(email, password)
        return userEntity?.toDomain()
    }
    
    suspend fun register(name: String, email: String, password: String): User {
        val userEntity = UserEntity(
            name = name,
            email = email,
            password = password
        )
        val id = userDao.insert(userEntity)
        return userEntity.copy(id = id.toInt()).toDomain()
    }
    
    suspend fun isEmailExists(email: String): Boolean {
        return userDao.emailExists(email)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }
    
    // Extension function for mapping
    private fun UserEntity.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            password = password
        )
    }
}

