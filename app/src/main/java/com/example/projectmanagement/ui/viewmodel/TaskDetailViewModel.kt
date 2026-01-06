package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.ui.common.UiState

class TaskDetailViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    private val _taskId = MutableLiveData<String>()
    
    val task: LiveData<Task?> = _taskId.switchMap { id ->
        projectRepository.getTaskById(id)
    }
    
    val taskState: LiveData<UiState<Task>> = task.switchMap { task ->
        MutableLiveData(
            if (task != null) {
                UiState.Success(task)
            } else {
                UiState.Error("Task not found")
            }
        )
    }
    
    fun loadTask(taskId: String) {
        _taskId.value = taskId
    }
}

class TaskDetailViewModelFactory(private val projectRepository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
