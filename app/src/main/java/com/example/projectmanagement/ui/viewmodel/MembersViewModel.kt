package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.model.Project
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

    fun loadMembers(projectId: String) {
        viewModelScope.launch {
            try {
                val projectMembers = syncRepository.getMembersRemote(projectId)

                // LOG THE SIZE: If this is 0, the query is working but returning nothing
                android.util.Log.d("MembersDebug", "Fetched ${projectMembers.size} members from Supabase")

                val uiMembers = projectMembers.map { member ->
                    MemberUi(
                        userId = member.userId,
                        userName = member.userDetails?.name ?: "Unknown User",
                        userEmail = member.userDetails?.email ?: "No email available",
                        role = member.role
                    )
                }
                _members.value = uiMembers
            } catch (e: Exception) {
                // THIS WILL TELL US THE REAL PROBLEM (e.g., "Table not found" or "JWT expired")
                android.util.Log.e("MembersDebug", "CRITICAL ERROR: ${e.message}", e)
                _members.value = emptyList()
            }
        }
    }
    
    fun loadProject(projectId: String) {
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
