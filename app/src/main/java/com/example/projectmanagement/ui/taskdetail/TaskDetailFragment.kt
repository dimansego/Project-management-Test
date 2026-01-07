package com.example.projectmanagement.ui.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.databinding.FragmentTaskDetailBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.TaskDetailViewModel
import com.example.projectmanagement.ui.viewmodel.TaskDetailViewModelFactory

class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskDetailViewModel by viewModels {
        TaskDetailViewModelFactory(
            ProjectRepository(
                (activity?.application as ProjectApplication).database.projectDao(),
                (activity?.application as ProjectApplication).database.taskDao()
            )
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val taskId = TaskDetailFragmentArgs.fromBundle(requireArguments()).taskId
        viewModel.loadTask(taskId)
        
        binding.editButton.setOnClickListener {
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToCreateEditTaskFragment(
                taskId = taskId,
                title = "Edit Task"
            )
            findNavController().navigate(action)
        }
        
        // Observe task data and update UI
        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let {
                binding.titleTextView.text = it.title
                binding.descriptionTextView.text = it.description
                binding.statusChip.text = it.status.toString()
                binding.priorityTextView.text = it.priority.toString()
                binding.assigneeTextView.text = it.assigneeName
                binding.deadlineTextView.text = getString(R.string.deadline_label, it.deadline)
            }
        }
        
        // Observe task state for loading and error
        viewModel.taskState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                }
                is UiState.Error -> {
                    binding.errorTextView.text = state.message
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorTextView.visibility = View.GONE
                }
            }
        })
    }
}

