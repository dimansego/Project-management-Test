package com.example.projectmanagement.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.data.repository.ProjectRepository
import com.example.projectmanagement.databinding.FragmentHomeBinding
import com.example.projectmanagement.ui.home.HomeFragmentDirections
import com.example.projectmanagement.ui.viewmodel.HomeViewModel
import com.example.projectmanagement.ui.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            ProjectRepository(
                (activity?.application as ProjectApplication).database.projectDao(),
                (activity?.application as ProjectApplication).database.taskDao()
            )
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

