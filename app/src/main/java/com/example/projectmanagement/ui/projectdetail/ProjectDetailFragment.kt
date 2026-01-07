package com.example.projectmanagement.ui.projectdetail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.datageneral.model.TaskStatus
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.databinding.FragmentProjectDetailBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.ProjectDetailViewModel
import com.example.projectmanagement.ui.viewmodel.ProjectDetailViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProjectDetailFragment : Fragment() {
    private var _binding: FragmentProjectDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectDetailViewModel by viewModels {
        val application = (activity?.application as ProjectApplication)
        ProjectDetailViewModelFactory(
            ProjectRepository(
                application.database.projectDao(),
                application.database.taskDao()
            ),
            application.syncRepository,
            application.userRepository,
            application.projectMemberRepository,
            application.taskRepository
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
        viewModel.loadProjectMembers(projectId)

        adapter = TasksAdapter(
            onItemClick = { task ->
                val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToTaskDetailFragment(task.id)
                findNavController().navigate(action)
            },
            onEditClick = { task ->
                val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToCreateEditTaskFragment(
                    taskId = task.id,
                    projectId = projectId
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { task ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete '${task.title}'? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                (activity?.application as ProjectApplication).syncRepository.deleteTask(task.id, projectId)
                                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error deleting task: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onAddMembersClick = { task ->
                showAssignMemberDialog(task.id, projectId)
            }
        )

        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tasksRecyclerView.adapter = adapter

        binding.addTaskFab.setOnClickListener {
            showActionBottomSheet(projectId)
        }

        setupFilterChips()

        viewModel.project.observe(viewLifecycleOwner) { project ->
            project?.let {
                binding.projectTitleTextView.text = it.title
                binding.projectDescriptionTextView.text = it.description
            }
        }

        viewModel.tasksState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.submitList(state.data)
                    binding.progressBar.visibility = View.GONE
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupFilterChips() {
        val chips = listOf(binding.allChip, binding.todoChip, binding.doingChip, binding.doneChip)
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
        binding.allChip.isChecked = true
    }

    private fun updateChipSelection(selectedChip: Chip, allChips: List<Chip>) {
        allChips.forEach { it.isChecked = it == selectedChip }
    }

    private fun showActionBottomSheet(projectId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Action")
            .setItems(arrayOf("Create Task", "Create Meeting")) { _, which ->
                when (which) {
                    0 -> {
                        val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToCreateEditTaskFragment(projectId = projectId)
                        findNavController().navigate(action)
                    }
                    1 -> {
                        val action = ProjectDetailFragmentDirections.actionProjectDetailFragmentToCreateMeetingFragment(projectId)
                        findNavController().navigate(action)
                    }
                }
            }
            .show()
    }

    private fun showAssignMemberDialog(taskId: String, projectId: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_assign_member, null)
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val dialog = builder.create()

        val autoComplete = dialogView.findViewById<AutoCompleteTextView>(R.id.memberAutoComplete)
        val btnAssign = dialogView.findViewById<Button>(R.id.btnAssign)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val members = viewModel.projectMembers.value ?: emptyList()
        val memberMap = members.associate { it.name to it.id }
        val memberNames = members.map { it.name }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, memberNames)
        autoComplete.setAdapter(adapter)

        var selectedMemberId: String? = null

        autoComplete.setOnItemClickListener { _, _, position, _ ->
            val name = adapter.getItem(position) ?: ""
            selectedMemberId = memberMap[name]
        }

        btnAssign.setOnClickListener {
            if (selectedMemberId != null) {
                viewModel.assignTaskToMember(taskId, projectId, selectedMemberId!!)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please select a member from the list", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}
