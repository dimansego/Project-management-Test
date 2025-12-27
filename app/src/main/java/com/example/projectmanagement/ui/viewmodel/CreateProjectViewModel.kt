package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.data.model.Project
import com.example.projectmanagement.data.repository.ProjectRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class CreateProjectViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    val projectName = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val dueDate = MutableLiveData<String>()
    
    private val _saveState = MutableLiveData<UiState<Project>>()
    val saveState: LiveData<UiState<Project>> = _saveState
    
    private val _projectNameError = MutableLiveData<String?>()
    val projectNameError: LiveData<String?> = _projectNameError
    
    fun saveProject() {
        val nameValue = projectName.value?.trim() ?: ""
        
        if (!isEntryValid(nameValue)) {
            return
        }
        
        val newProject = getNewProjectEntry(nameValue)
        _saveState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                projectRepository.insertProject(newProject)
                _saveState.postValue(UiState.Success(newProject))
            } catch (e: Exception) {
                _saveState.postValue(UiState.Error("Error: ${e.message}"))
            }
        }
    }
    
    fun clearErrors() {
        _projectNameError.value = null
    }
    
    fun setProjectName(name: String) {
        projectName.value = name
    }
    
    fun setDescription(desc: String) {
        description.value = desc
    }
    
    fun setDueDate(date: String) {
        dueDate.value = date
    }
    
    private fun isEntryValid(nameValue: String): Boolean {
        return if (nameValue.isEmpty()) {
            _projectNameError.value = "Project name is required"
            false
        } else {
            _projectNameError.value = null
            true
        }
    }
    
    private fun getNewProjectEntry(nameValue: String): Project {
        return Project(
            id = 0,
            title = nameValue,
            description = description.value ?: "",
            dueDate = dueDate.value ?: ""
        )
    }
}

class CreateProjectViewModelFactory(private val projectRepository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateProjectViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

