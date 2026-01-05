package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import kotlinx.coroutines.launch

class MeetingsViewModel(
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository // Use UserRepository instead of AuthRepository
) : ViewModel() {

    private val _meetings = MutableLiveData<List<Meeting>>()
    val meetings: LiveData<List<Meeting>> = _meetings

    private var currentProjectId: String? = null

    // In MeetingsViewModel.kt

    fun loadMeetingsByProject(projectId: String) {
        currentProjectId = projectId // Save the state
        viewModelScope.launch {
            try {
                val fetchedMeetings = if (projectId.isEmpty()) {
                    // Use the new method we just created
                    syncRepository.getAllMeetingsForCurrentUser()
                } else {
                    syncRepository.getMeetingsByProjectId(projectId)
                }
                _meetings.value = fetchedMeetings
            } catch (e: Exception) {
                _meetings.value = emptyList()
            }
        }
    }

    fun refreshMeetings() {
        // Fix: Pass an empty string if currentProjectId is null
        loadMeetingsByProject(currentProjectId ?: "")
    }

    fun deleteMeeting(meetingId: String, projectId: String) {
        viewModelScope.launch {
            try {
                syncRepository.deleteMeeting(meetingId, projectId)
                // Refresh the list after deletion
                refreshMeetings()
            } catch (e: Exception) {
                // Error handling can be added here if needed
                throw e
            }
        }
    }
}

// Don't forget to update the Factory as well
class MeetingsViewModelFactory(
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeetingsViewModel(syncRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}