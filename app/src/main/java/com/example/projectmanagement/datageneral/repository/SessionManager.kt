package com.example.projectmanagement.datageneral.repository

import com.example.projectmanagement.datageneral.model.User

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

