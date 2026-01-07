package com.example.projectmanagement.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.databinding.FragmentHomeBinding
import com.example.projectmanagement.ui.viewmodel.HomeViewModel
import com.example.projectmanagement.ui.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels {
        // Get the application instance once to make the code cleaner
        val app = activity?.application as ProjectApplication

        HomeViewModelFactory(
            ProjectRepository(
                app.database.projectDao(),
                app.database.taskDao()
            ),
            app.syncRepository,
            app.userRepository // <--- ADD THIS LINE HERE
        )
    }

    private lateinit var projectsAdapter: ProjectsAdapter
    private lateinit var tasksAdapter: TasksAdapter


    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProjectsRecyclerView()
        setupTasksRecyclerView()
        
        // Action bar menu items are handled in MainActivity
        // Remove the header icons click handlers since they're now in Action Bar
        
        observeData()
        setupUI()
    }
    
    private fun setupUI() {
        // Set up greeting and task count text
        viewModel.currentUserName.observe(viewLifecycleOwner) { name ->
            binding.greetingText.text = getString(R.string.hello_user, name ?: "Guest")
        }
        
        viewModel.tasksToCompleteCount.observe(viewLifecycleOwner) { count ->
            binding.tasksCountText.text = getString(R.string.tasks_to_complete, count ?: 0)
        }
        
        viewModel.todayTaskCount.observe(viewLifecycleOwner) { count ->
            binding.todayTaskCountText.text = (count ?: 0).toString()
        }
        
        viewModel.inProgressTaskCount.observe(viewLifecycleOwner) { count ->
            binding.inProgressTaskCountText.text = (count ?: 0).toString()
        }
    }
    
    private fun setupProjectsRecyclerView() {
        projectsAdapter = ProjectsAdapter(
            onItemClick = { projectUi ->
                val action = HomeFragmentDirections.actionHomeFragmentToProjectDetailFragment(projectUi.project.id)
                findNavController().navigate(action)
            },
            onEditClick = { projectUi ->
                // Navigate to edit project screen
                val action = HomeFragmentDirections.actionHomeFragmentToCreateProjectFragment(
                    projectId = projectUi.project.id,    // Pass the ID to fetch data
                    title = "Edit Project"               // This will show in the Action Bar
                )

                // 2. Navigate
                findNavController().navigate(action)
            },
            onDeleteClick = { projectUi ->
                // Show delete confirmation and delete project
                com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Project")
                    .setMessage("Are you sure you want to delete '${projectUi.project.title}'? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                (activity?.application as ProjectApplication).syncRepository.deleteProject(projectUi.project.id)
                                android.widget.Toast.makeText(context, "Project deleted", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Error deleting project: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onSeeMembersClick = { projectUi ->
                val action = HomeFragmentDirections.actionHomeFragmentToMembersFragment(projectUi.project.id)
                findNavController().navigate(action)
            }
        )
        
        binding.projectsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.projectsRecyclerView.adapter = projectsAdapter
    }
    
    private fun setupTasksRecyclerView() {
        tasksAdapter = TasksAdapter(
            onItemClick = { taskUi ->
                val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailFragment(taskUi.task.id)
                findNavController().navigate(action)
            },
            onEditClick = { taskUi ->
                val action = HomeFragmentDirections.actionHomeFragmentToCreateEditTaskFragment(
                    taskId = taskUi.task.id,
                    projectId = taskUi.task.projectId,
                    title = "Edit Task"
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { taskUi ->
                // Show delete confirmation and delete task
                com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete '${taskUi.task.title}'? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                (activity?.application as ProjectApplication).syncRepository.deleteTask(taskUi.task.id, taskUi.task.projectId)
                                android.widget.Toast.makeText(context, "Task deleted", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Error deleting task: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onAddMembersClick = { taskUi ->
                // TODO: Show add members dialog
                android.widget.Toast.makeText(context, "Add Members to Task: ${taskUi.task.title}", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tasksRecyclerView.adapter = tasksAdapter
    }
    
    private fun observeData() {
        viewModel.projects.observe(viewLifecycleOwner, Observer { projects ->
            projectsAdapter.submitList(projects)
        })
        
        viewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasksAdapter.submitList(tasks)
        })
    }
}

