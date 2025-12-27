package com.example.projectmanagement.ui.projectdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.data.model.TaskStatus
import com.example.projectmanagement.data.repository.ProjectRepository
import com.example.projectmanagement.databinding.FragmentProjectDetailBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.ProjectDetailViewModel
import com.example.projectmanagement.ui.viewmodel.ProjectDetailViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProjectDetailFragment : Fragment() {
    private var _binding: FragmentProjectDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectDetailViewModel by viewModels {
        ProjectDetailViewModelFactory(
            ProjectRepository(
                (activity?.application as ProjectApplication).database.projectDao(),
                (activity?.application as ProjectApplication).database.taskDao()
            )
        )
    }
    private lateinit var adapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val projectId = ProjectDetailFragmentArgs.fromBundle(requireArguments()).projectId
        viewModel.loadProject(projectId)
        
        adapter = TasksAdapter { task ->
            val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToTaskDetailFragment(task.id)
            findNavController().navigate(action)
        }
        
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tasksRecyclerView.adapter = adapter
        
        binding.addTaskFab.setOnClickListener {
            showActionBottomSheet(projectId)
        }
        
        setupFilterChips()
        
        // Observe project data and update UI
        viewModel.project.observe(viewLifecycleOwner) { project ->
            project?.let {
                binding.projectTitleTextView.text = it.title
                binding.projectDescriptionTextView.text = it.description
            }
        }
        
        // Observe tasks state
        viewModel.tasksState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.submitList(state.data)
                    binding.progressBar.visibility = View.GONE
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    // Handle error
                }
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }
    
    private fun setupFilterChips() {
        val chips = listOf(
            binding.allChip,
            binding.todoChip,
            binding.doingChip,
            binding.doneChip
        )
        
        chips.forEach { chip ->
            chip.setOnClickListener {
                updateChipSelection(chip, chips)
                val status = when (chip.id) {
                    R.id.allChip -> null
                    R.id.todoChip -> TaskStatus.TODO
                    R.id.doingChip -> TaskStatus.DOING
                    R.id.doneChip -> TaskStatus.DONE
                    else -> null
                }
                viewModel.filterByStatus(status)
            }
        }
        
        // Set default selection
        binding.allChip.isChecked = true
    }
    
    private fun updateChipSelection(selectedChip: Chip, allChips: List<Chip>) {
        allChips.forEach { it.isChecked = it == selectedChip }
    }
    
    private fun showActionBottomSheet(projectId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Action")
            .setItems(arrayOf("Create Task", "Create Meeting")) { _, which ->
                when (which) {
                    0 -> {
                        // Create Task
                        val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToCreateEditTaskFragment(projectId)
                        findNavController().navigate(action)
                    }
                    1 -> {
                        // Create Meeting
                        val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToCreateMeetingFragment(projectId)
                        findNavController().navigate(action)
                    }
                }
            }
            .show()
    }
}

