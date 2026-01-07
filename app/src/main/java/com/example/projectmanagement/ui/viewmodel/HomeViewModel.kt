package com.example.projectmanagement.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.model.Project
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.datageneral.model.TaskStatus
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository
import kotlinx.coroutines.launch


data class ProjectUi(
    val project: Project,
    val progress: Int, // 0-100
    val memberCount: Int,
    val taskCount: Int
)

data class TaskUi(
    val task: Task,
    val projectTitle: String
)

class HomeViewModel(
    private val projectRepository: ProjectRepository,
    private val syncRepository: SupabaseSyncRepository,
    private val userRepository: UserRepository
) : ViewModel() {



    
    private val _currentUserName = MutableLiveData<String>()
    val currentUserName: LiveData<String> = _currentUserName
    
    private val _tasksToCompleteCount = MediatorLiveData<Int>()
    val tasksToCompleteCount: LiveData<Int> = _tasksToCompleteCount
    
    private val _projects = MediatorLiveData<List<ProjectUi>>()
    val projects: LiveData<List<ProjectUi>> = _projects
    
    private val _tasks = MediatorLiveData<List<TaskUi>>()
    val tasks: LiveData<List<TaskUi>> = _tasks
    
    private val _todayTaskCount = MediatorLiveData<Int>()
    val todayTaskCount: LiveData<Int> = _todayTaskCount
    
    private val _inProgressTaskCount = MediatorLiveData<Int>()
    val inProgressTaskCount: LiveData<Int> = _inProgressTaskCount
    
    private val allProjectsLiveData = projectRepository.getAllProjects()
    private val allTasksLiveData = projectRepository.getAllTasks()
    
    init {

        setupProjects()
        setupTasks()
        setupCounts()
        refreshData()
    }
    fun refreshData() {
        loadCurrentUser()

        viewModelScope.launch {
            try {
                // REMOVE: if (projectRepository.getProjectCount() == 0)
                // We want to pull projects from Supabase every time the user enters Home
                // to catch projects they joined or updates from other users.
                Log.d("HomeVM", "Triggering sync to catch new joined projects...")
                syncRepository.initialSync()

            } catch (e: Exception) {
                Log.e("HomeVM", "Refresh failed: ${e.message}")
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                // Fetch the actual profile from Supabase
                val user = userRepository.getCurrentUser()
                _currentUserName.value = user?.name ?: "Guest"
            } catch (e: Exception) {
                _currentUserName.value = "Guest"
            }
        }
    }
    
    private fun setupProjects() {
        fun updateProjects() {
            val allProjects = allProjectsLiveData.value ?: emptyList()
            val allTasks = allTasksLiveData.value ?: emptyList()
            
            val projectsUi = allProjects.map { project ->
                val projectTasks = allTasks.filter { it.projectId == project.id }
                val completedTasks = projectTasks.count { it.status == TaskStatus.DONE }
                val progress = if (projectTasks.isEmpty()) 0 else (completedTasks * 100 / projectTasks.size)
                
                ProjectUi(
                    project = project,
                    progress = progress,
                    memberCount = projectTasks.map { it.assigneeName }.distinct().size,
                    taskCount = projectTasks.size
                )
            }
            _projects.value = projectsUi
        }
        
        _projects.addSource(allProjectsLiveData) { updateProjects() }
        _projects.addSource(allTasksLiveData) { updateProjects() }
    }
    
    private fun setupTasks() {
        _tasks.addSource(allTasksLiveData) { allTasks ->
            val inProgressTasks = allTasks.filter { it.status == TaskStatus.DOING }
            val allProjects = allProjectsLiveData.value ?: emptyList()
            
            val tasksUi = inProgressTasks.map { task ->
                val project = allProjects.find { it.id == task.projectId }
                TaskUi(
                    task = task,
                    projectTitle = project?.title ?: ""
                )
            }
            _tasks.value = tasksUi
        }
        
        // Also update when projects change to refresh project titles
        _tasks.addSource(allProjectsLiveData) {
            val allTasks = allTasksLiveData.value ?: emptyList()
            val inProgressTasks = allTasks.filter { it.status == TaskStatus.DOING }
            
            val tasksUi = inProgressTasks.map { task ->
                val project = it.find { p -> p.id == task.projectId }
                TaskUi(
                    task = task,
                    projectTitle = project?.title ?: ""
                )
            }
            _tasks.value = tasksUi
        }
    }
    
    private fun setupCounts() {
        _tasksToCompleteCount.addSource(allTasksLiveData) { allTasks ->
            val incompleteTasks = allTasks.filter { it.status != TaskStatus.DONE }
            _tasksToCompleteCount.value = incompleteTasks.size
        }
        
        _todayTaskCount.addSource(allTasksLiveData) { allTasks ->
            // Only count tasks where deadline matches current system date
            val todayTasks = allTasks.filter { task ->
                com.example.projectmanagement.datageneral.core.DateFormatter.isDeadlineToday(task.deadline)
            }
            _todayTaskCount.value = todayTasks.size
        }
        
        _inProgressTaskCount.addSource(allTasksLiveData) { allTasks ->
            val inProgressTasks = allTasks.filter { it.status == TaskStatus.DOING }
            _inProgressTaskCount.value = inProgressTasks.size
        }
    }
}

class HomeViewModelFactory(private val projectRepository: ProjectRepository, private val syncRepository: SupabaseSyncRepository,private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(projectRepository, syncRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
