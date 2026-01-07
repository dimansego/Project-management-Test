package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.repository.SessionManager
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepository: AuthRepository, private val userRepository: UserRepository,private val projectRepository: ProjectRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<AppUser?>()
    val currentUser: LiveData<AppUser?> = _currentUser

    
    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess
    
    init {
        loadCurrentUser()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // 1. Sign out from Supabase cloud
                authRepository.signOut()

                // 2. CRITICAL: Clear local Room database
                // This prevents the next user from seeing the old user's data
                projectRepository.clearAllProjects()

                // 3. Clear local session state
                SessionManager.clearSession()

                _logoutSuccess.postValue(true)
            } catch (e: Exception) {
                // Fallback: clear local data anyway even if network fails
                projectRepository.clearAllProjects()
                SessionManager.clearSession()
                _logoutSuccess.postValue(true)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userRepository.getCurrentUser()
        }
    }
}

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val projectRepository: com.example.projectmanagement.datageneral.repository.ProjectRepository // Add this
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepository, userRepository, projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
