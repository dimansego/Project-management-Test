package com.example.projectmanagement.data.repository

import com.example.projectmanagement.data.model.User

object SessionManager {
    private var currentUser: User? = null
    
    fun setCurrentUser(user: User?) {
        currentUser = user
    }
    
    fun getCurrentUser(): User? = currentUser
    
    fun clearSession() {
        currentUser = null
    }
}

