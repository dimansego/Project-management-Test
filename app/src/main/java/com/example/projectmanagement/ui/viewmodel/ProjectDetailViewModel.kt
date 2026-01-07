package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.model.Project
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.datageneral.model.TaskStatus
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.model.user.AppUser
import com.example.projectmanagement.datageneral.data.repository.project.ProjectMemberRepository
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository,
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _projectId = MutableLiveData<String?>()

    val project: LiveData<Project?> = _projectId.switchMap { id ->
        if (id == null) MutableLiveData(null)
        else projectRepository.getProjectById(id)
    }

    private val _selectedStatus = MutableLiveData<TaskStatus?>()
    val selectedStatus: LiveData<TaskStatus?> = _selectedStatus

    private val _projectMembers = MutableLiveData<List<AppUser>>()
    val projectMembers: LiveData<List<AppUser>> = _projectMembers
    private val tasksLiveData: LiveData<List<Task>> = _projectId.switchMap { projectId ->
        if (projectId == null) MutableLiveData(emptyList())
        else projectRepository.getTasksByProjectId(projectId)
    }

    fun loadProjectMembers(projectId: String) {
        viewModelScope.launch {
            try {
                val members = projectMemberRepository.getMembersByProjectId(projectId)
                val userDetails = members.mapNotNull { member ->
                    userRepository.getUserById(member.userId)
                }
                _projectMembers.value = userDetails
            } catch (e: Exception) {
                _projectMembers.value = emptyList()
            }
        }
    }
    
    private val _meetings = MutableLiveData<List<Meeting>>()
    val meetings: LiveData<List<Meeting>> = _meetings

    val tasksState: LiveData<UiState<List<Task>>> = MediatorLiveData<UiState<List<Task>>>().apply {
        value = UiState.Loading

        fun updateTasks() {
            val allTasks = tasksLiveData.value ?: emptyList()
            val status = _selectedStatus.value

            val filteredTasks = if (status != null) {
                allTasks.filter { it.status == status }
            } else {
                allTasks
            }

            value = UiState.Success(filteredTasks)
        }

        addSource(tasksLiveData) { updateTasks() }
        addSource(_selectedStatus) { updateTasks() }
    }

    fun loadProject(projectId: String) {
        _projectId.value = projectId
        // Load meetings for this project
        loadProjectMembers(projectId)
        viewModelScope.launch {
            try {
                val fetchedMeetings = syncRepository.getMeetingsByProjectId(projectId)
                _meetings.value = fetchedMeetings
            } catch (e: Exception) {
                _meetings.value = emptyList()
            }
        }
        // optional: reset filter when opening a new project
        // _selectedStatus.value = null
    }

    fun filterByStatus(status: TaskStatus?) {
        _selectedStatus.value = status
    }

    fun assignTaskToMember(taskId: String, projectId: String, memberId: String) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskAssignee(taskId, projectId, memberId)
                // The task list will update automatically because the underlying data in the repository changes,
                // and the fragment is observing the LiveData from `projectRepository.getTasksByProjectId`.
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

class ProjectDetailViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectDetailViewModel(projectRepository, syncRepository, userRepository, projectMemberRepository, taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
