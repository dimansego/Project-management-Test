package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import kotlinx.coroutines.launch

class MeetingsViewModel(
    private val syncRepository: SupabaseSyncRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _meetings = MutableLiveData<List<Meeting>>()
    val meetings: LiveData<List<Meeting>> = _meetings
    
    init {
        loadMeetings()
    }
    
    private fun loadMeetings() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.currentAuthUser
                if (currentUser != null) {
                    val fetchedMeetings = syncRepository.getMeetingsByAuthId(currentUser.id)
                    _meetings.value = fetchedMeetings
                } else {
                    _meetings.value = emptyList()
                }
            } catch (e: Exception) {
                _meetings.value = emptyList()
            }
        }
    }
    
    fun refreshMeetings() {
        loadMeetings()
    }
}

class MeetingsViewModelFactory(
    private val syncRepository: SupabaseSyncRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeetingsViewModel(syncRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
