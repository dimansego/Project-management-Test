package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.data.model.Project
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import kotlinx.coroutines.launch

data class MemberUi(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val role: String?
)

class MembersViewModel(
    private val syncRepository: SupabaseSyncRepository
) : ViewModel() {
    
    private val _members = MutableLiveData<List<MemberUi>>()
    val members: LiveData<List<MemberUi>> = _members
    
    private val _project = MutableLiveData<Project?>()
    val project: LiveData<Project?> = _project
    
    private val _inviteCode = MutableLiveData<String?>()
    val inviteCode: LiveData<String?> = _inviteCode
    
    fun loadMembers(projectId: Int) {
        viewModelScope.launch {
            try {
                // TODO: Load members from repository
                // For now, return empty list
                _members.value = emptyList()
            } catch (e: Exception) {
                _members.value = emptyList()
            }
        }
    }
    
    fun loadProject(projectId: Int) {
        viewModelScope.launch {
            try {
                // Get project from Room first
                val fetchedProject = syncRepository.getProject(projectId)
                _project.value = fetchedProject
                
                // Get invite code from Supabase project
                try {
                    val supabaseProjects = syncRepository.getAllSupabaseProjects()
                    val supabaseProject = supabaseProjects.find { 
                        // Try to match by title since IDs might differ
                        it.title == fetchedProject?.title 
                    }
                    _inviteCode.value = supabaseProject?.inviteCode ?: fetchedProject?.inviteCode
                } catch (e: Exception) {
                    // Fallback to Room invite code if Supabase fetch fails
                    _inviteCode.value = fetchedProject?.inviteCode
                }
            } catch (e: Exception) {
                _project.value = null
                _inviteCode.value = null
            }
        }
    }
}

class MembersViewModelFactory(
    private val syncRepository: SupabaseSyncRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MembersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MembersViewModel(syncRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
