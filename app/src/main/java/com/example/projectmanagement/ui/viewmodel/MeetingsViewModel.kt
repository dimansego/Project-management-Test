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
        // Fake data
        val fakeMeetings = listOf(
            Meeting(1, "Sprint Planning", "2024-02-01", "10:00 AM", "Team A, Team B"),
            Meeting(2, "Code Review", "2024-02-03", "2:00 PM", "Dev Team"),
            Meeting(3, "Project Retrospective", "2024-02-05", "3:30 PM", "All Members")
        )
        _meetings.value = fakeMeetings
    }
}



