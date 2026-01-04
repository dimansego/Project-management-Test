package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Meeting(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val participants: String
)

class MeetingsViewModel : ViewModel() {
    
    private val _meetings = MutableLiveData<List<Meeting>>()
    val meetings: LiveData<List<Meeting>> = _meetings
    
    init {
        loadMeetings()
    }
    
    private fun loadMeetings() {
        // Load meetings from repository (no hardcoded data)
        _meetings.value = emptyList()
    }
}
