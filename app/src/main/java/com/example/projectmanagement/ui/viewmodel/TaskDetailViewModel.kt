package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _taskId = MutableLiveData<String>()
    
    val task: LiveData<Task?> = _taskId.switchMap { id ->
        projectRepository.getTaskById(id)
    }
    
    private val _assigneeName = MutableLiveData<String>()
    val assigneeName: LiveData<String> = _assigneeName

    val taskState: LiveData<UiState<Task>> = task.switchMap { task ->
        val result = MutableLiveData<UiState<Task>>()
        if (task != null) {
            result.value = UiState.Success(task)
            
            // If we have an assignee ID, fetch the name
            if (!task.assigneeName.isNullOrEmpty()) {
                fetchAssigneeName(task.assigneeName)
            } else {
                _assigneeName.value = "Unassigned"
            }
        } else {
            result.value = UiState.Error("Task not found")
        }
        result
    }
    
    fun loadTask(taskId: String) {
        _taskId.value = taskId
    }

    private fun fetchAssigneeName(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                _assigneeName.value = user?.name ?: "Unknown User"
            } catch (e: Exception) {
                _assigneeName.value = "Unknown User"
            }
        }
    }
}

class TaskDetailViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailViewModel(projectRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
