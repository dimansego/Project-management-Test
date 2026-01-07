package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CreateMeetingViewModel(
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _saveState = MutableLiveData<UiState<Meeting>>()
    val saveState: LiveData<UiState<Meeting>> = _saveState
    
    private val _titleError = MutableLiveData<String?>()
    val titleError: LiveData<String?> = _titleError
    
    private val _locationError = MutableLiveData<String?>()
    val locationError: LiveData<String?> = _locationError
    
    private val _startDateTimeError = MutableLiveData<String?>()
    val startDateTimeError: LiveData<String?> = _startDateTimeError
    
    private val _endDateTimeError = MutableLiveData<String?>()
    val endDateTimeError: LiveData<String?> = _endDateTimeError

    private val _loadedMeeting = MutableLiveData<Meeting?>()
    val loadedMeeting: LiveData<Meeting?> = _loadedMeeting

    private var currentMeetingId: String? = null
    private var currentProjectId: String? = null

    fun loadMeetingForEdit(meetingId: String, projectId: String) {
        currentMeetingId = meetingId
        currentProjectId = projectId
        viewModelScope.launch {
            try {
                val meeting = syncRepository.getMeetingById(meetingId, projectId)
                _loadedMeeting.postValue(meeting)
            } catch (e: Exception) {
                _saveState.postValue(UiState.Error("Error loading meeting: ${e.message}"))
            }
        }
    }
    
    fun saveMeeting(
        projectId: String,
        title: String,
        description: String?,
        location: String,
        startDate: LocalDate?,
        startTime: LocalTime?,
        endDate: LocalDate?,
        endTime: LocalTime?
    ) {
        if (!validateInput(title, location, startDate, startTime, endDate, endTime)) {
            return
        }
        
        _saveState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser == null) {
                    _saveState.postValue(UiState.Error("User not authenticated"))
                    return@launch
                }
                
                // Combine date and time into OffsetDateTime, then convert to ISO-8601 string
                val startDateTime = OffsetDateTime.of(
                    startDate!!,
                    startTime!!,
                    ZoneOffset.UTC
                )
                val endDateTime = OffsetDateTime.of(
                    endDate!!,
                    endTime!!,
                    ZoneOffset.UTC
                )
                
                // Format as ISO-8601 using standard formatter (e.g., 2026-01-05T13:30:00Z)
                val startTimeIso = startDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .replace("+00:00", "Z") // Ensure Z format for UTC
                val endTimeIso = endDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .replace("+00:00", "Z") // Ensure Z format for UTC
                
                val savedMeeting = if (currentMeetingId != null && currentMeetingId!!.isNotEmpty()) {
                    // Update existing meeting
                    syncRepository.updateMeetingAndSync(
                        meetingId = currentMeetingId!!,
                        projectId = projectId,
                        title = title.trim(),
                        description = description?.trim()?.takeIf { it.isNotEmpty() },
                        location = location.trim(),
                        startTimeIso = startTimeIso,
                        endTimeIso = endTimeIso
                    )
                } else {
                    // Create new meeting
                    val meeting = Meeting(
                        id = null, // Let Supabase autogenerate UUID
                        projectId = projectId,
                        title = title.trim(),
                        description = description?.trim()?.takeIf { it.isNotEmpty() },
                        startTime = startTimeIso,
                        endTime = endTimeIso,
                        location = location.trim(),
                        createdBy = currentUser.id!!,
                        createdAt = null, // Will be set by Supabase
                        updatedAt = null,
                        notes = null,
                        notesUpdatedAt = null,
                        notesUpdatedBy = null
                    )
                    syncRepository.insertMeetingAndSync(meeting)
                }
                
                _saveState.postValue(UiState.Success(savedMeeting))
            } catch (e: Exception) {
                _saveState.postValue(UiState.Error("Error creating meeting: ${e.message}"))
            }
        }
    }
    
    private fun validateInput(
        title: String,
        location: String,
        startDate: LocalDate?,
        startTime: LocalTime?,
        endDate: LocalDate?,
        endTime: LocalTime?
    ): Boolean {
        var isValid = true
        
        if (title.trim().isEmpty()) {
            _titleError.value = "Meeting title is required"
            isValid = false
        } else {
            _titleError.value = null
        }
        
        if (location.trim().isEmpty()) {
            _locationError.value = "Location is required"
            isValid = false
        } else {
            _locationError.value = null
        }
        
        if (startDate == null || startTime == null) {
            _startDateTimeError.value = "Start date and time are required"
            isValid = false
        } else {
            _startDateTimeError.value = null
        }
        
        if (endDate == null || endTime == null) {
            _endDateTimeError.value = "End date and time are required"
            isValid = false
        } else {
            _endDateTimeError.value = null
        }
        
        // Validate that end is after start
        if (startDate != null && startTime != null && endDate != null && endTime != null) {
            val startDateTime = OffsetDateTime.of(startDate, startTime, ZoneOffset.UTC)
            val endDateTime = OffsetDateTime.of(endDate, endTime, ZoneOffset.UTC)
            
            if (!endDateTime.isAfter(startDateTime)) {
                _endDateTimeError.value = "End time must be after start time"
                isValid = false
            }
        }
        
        return isValid
    }
    
    fun clearErrors() {
        _titleError.value = null
        _locationError.value = null
        _startDateTimeError.value = null
        _endDateTimeError.value = null
    }
}

class CreateMeetingViewModelFactory(
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateMeetingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateMeetingViewModel(syncRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

