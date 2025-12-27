package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.switchMap
import com.example.projectmanagement.data.model.Task
import com.example.projectmanagement.data.model.TaskPriority
import com.example.projectmanagement.data.model.TaskStatus
import com.example.projectmanagement.data.repository.ProjectRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class CreateEditTaskViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val status = MutableLiveData<TaskStatus>()
    val priority = MutableLiveData<TaskPriority>()
    val deadlineTimestamp = MutableLiveData<Long?>(null)
    val deadline = MutableLiveData<String>()
    val assigneeName = MutableLiveData<String>()
    
    private val _taskId = MutableLiveData<Int?>()
    private val _projectId = MutableLiveData<Int>()
    
    private val _saveState = MutableLiveData<UiState<Task>>()
    val saveState: LiveData<UiState<Task>> = _saveState
    
    private val _titleError = MutableLiveData<String?>()
    val titleError: LiveData<String?> = _titleError
    
    // Load task data when editing
    val taskData: LiveData<Task?> = _taskId.switchMap { id ->
        if (id != null && id != 0) {
            projectRepository.getTaskById(id)
        } else {
            MutableLiveData(null)
        }
    }
    
    init {
        // Observe taskData to populate fields when editing
        taskData.observeForever { task ->
            if (task != null && _taskId.value != null) {
                _projectId.value = task.projectId
                title.value = task.title
                description.value = task.description
                status.value = task.status
                priority.value = task.priority
                deadline.value = task.deadline
                // Parse deadline string to timestamp for date picker
                deadlineTimestamp.value = parseDeadlineString(task.deadline)
                assigneeName.value = task.assigneeName
            }
        }
    }
    
    fun initForCreate(projectId: Int) {
        _projectId.value = projectId
        _taskId.value = null
        title.value = ""
        description.value = ""
        status.value = TaskStatus.TODO
        priority.value = TaskPriority.MEDIUM
        deadlineTimestamp.value = null
        deadline.value = ""
        assigneeName.value = ""
    }
    
    fun initForEdit(taskId: Int) {
        _taskId.value = taskId
    }
    
    fun saveTask() {
        val titleValue = title.value?.trim() ?: ""
        
        if (!isEntryValid(titleValue)) {
            return
        }
        
        val newTask = getNewTaskEntry(titleValue)
        _saveState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                if (_taskId.value != null && _taskId.value != 0) {
                    projectRepository.updateTask(newTask)
                } else {
                    projectRepository.insertTask(newTask)
                }
                _saveState.postValue(UiState.Success(newTask))
            } catch (e: Exception) {
                _saveState.postValue(UiState.Error("Error: ${e.message}"))
            }
        }
    }
    
    fun clearErrors() {
        _titleError.value = null
    }
    
    fun setTitle(titleValue: String) {
        title.value = titleValue
    }
    
    fun setDescription(descriptionValue: String) {
        description.value = descriptionValue
    }
    
    fun setDeadline(deadlineValue: String) {
        deadline.value = deadlineValue
    }
    
    fun setDeadlineTimestamp(timestamp: Long) {
        deadlineTimestamp.value = timestamp
        // Store as timestamp string (milliseconds) for database
        deadline.value = timestamp.toString()
    }
    
    private fun parseDeadlineString(deadlineString: String): Long? {
        if (deadlineString.isEmpty()) return null
        return try {
            deadlineString.toLong()
        } catch (e: Exception) {
            null
        }
    }
    
    fun setAssigneeName(assigneeNameValue: String) {
        assigneeName.value = assigneeNameValue
    }
    
    private fun isEntryValid(titleValue: String): Boolean {
        return if (titleValue.isEmpty()) {
            _titleError.value = "Title is required"
            false
        } else {
            _titleError.value = null
            true
        }
    }
    
    private fun getNewTaskEntry(titleValue: String): Task {
        // Use deadline timestamp converted to string, or fallback to deadline string
        val deadlineValue = if (deadlineTimestamp.value != null && deadlineTimestamp.value!! > 0) {
            deadlineTimestamp.value.toString()
        } else {
            deadline.value ?: ""
        }
        
        return Task(
            id = _taskId.value ?: 0,
            projectId = _projectId.value ?: 1,
            title = titleValue,
            description = description.value ?: "",
            status = status.value ?: TaskStatus.TODO,
            priority = priority.value ?: TaskPriority.MEDIUM,
            deadline = deadlineValue,
            assigneeName = assigneeName.value ?: ""
        )
    }
}

class CreateEditTaskViewModelFactory(private val projectRepository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEditTaskViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
