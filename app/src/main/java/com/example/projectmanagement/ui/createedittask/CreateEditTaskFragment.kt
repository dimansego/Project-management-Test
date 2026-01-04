package com.example.projectmanagement.ui.createedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.data.model.TaskPriority
import com.example.projectmanagement.data.model.TaskStatus
import com.example.projectmanagement.databinding.FragmentCreateEditTaskBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.CreateEditTaskViewModel
import com.example.projectmanagement.ui.viewmodel.CreateEditTaskViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CreateEditTaskFragment : Fragment() {
    private var _binding: FragmentCreateEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateEditTaskViewModel by viewModels {
        CreateEditTaskViewModelFactory(
            (activity?.application as ProjectApplication).syncRepository
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val args = CreateEditTaskFragmentArgs.fromBundle(requireArguments())
        val taskId = args.taskId
        val projectId = args.projectId
        
        if (taskId != 0) {
            viewModel.initForEdit(taskId)
        } else {
            viewModel.initForCreate(projectId)
        }
        
        setupStatusDropdown()
        setupPriorityDropdown()
        setupFormFields()
        setupDatePicker()
        
        binding.saveButton.setOnClickListener {
            // Explicitly update ViewModel with current UI values before saving
            updateViewModelFromUI()
            viewModel.saveTask()
        }
        
        // Observe title error
        viewModel.titleError.observe(viewLifecycleOwner) { error ->
            binding.titleInputLayout.error = error
            binding.titleInputLayout.isErrorEnabled = error != null
        }
        
        viewModel.saveState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    findNavController().popBackStack()
                }
                is UiState.Error -> {
                    // Handle error
                }
                else -> {}
            }
        })
    }
    
    private fun setupFormFields() {
        // Set up two-way binding for form fields
        binding.titleEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setTitle(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        binding.descriptionEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setDescription(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        // Deadline is handled by date picker, no text watcher needed
        
        // Observe ViewModel fields to update EditTexts when ViewModel changes
        viewModel.title.observe(viewLifecycleOwner) { title ->
            if (binding.titleEditText.text?.toString() != title) {
                binding.titleEditText.setText(title)
            }
        }
        
        viewModel.description.observe(viewLifecycleOwner) { description ->
            if (binding.descriptionEditText.text?.toString() != description) {
                binding.descriptionEditText.setText(description)
            }
        }
        
        // Deadline is handled by date picker setup
    }
    
    private fun setupStatusDropdown() {
        val statuses = TaskStatus.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statuses)
        binding.statusAutoComplete.setAdapter(adapter)
        
        viewModel.status.observe(viewLifecycleOwner) { status ->
            if (status != null) {
                val currentText = binding.statusAutoComplete.text?.toString() ?: ""
                if (currentText != status.name) {
                    binding.statusAutoComplete.setText(status.name, false)
                }
            }
        }
        
        binding.statusAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedStatus = TaskStatus.values()[position]
            viewModel.status.value = selectedStatus
        }
    }
    
    private fun setupPriorityDropdown() {
        val priorities = TaskPriority.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, priorities)
        binding.priorityAutoComplete.setAdapter(adapter)
        
        viewModel.priority.observe(viewLifecycleOwner) { priority ->
            if (priority != null) {
                val currentText = binding.priorityAutoComplete.text?.toString() ?: ""
                if (currentText != priority.name) {
                    binding.priorityAutoComplete.setText(priority.name, false)
                }
            }
        }
        
        binding.priorityAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedPriority = TaskPriority.values()[position]
            viewModel.priority.value = selectedPriority
        }
    }
    
    private fun setupDatePicker() {
        binding.deadlineEditText.setOnClickListener {
            showDatePicker()
        }
        
        // Observe deadline from ViewModel and update display
        viewModel.deadlineTimestamp.observe(viewLifecycleOwner) { timestamp ->
            if (timestamp != null && timestamp > 0) {
                val formattedDate = formatDate(timestamp)
                binding.deadlineEditText.setText(formattedDate)
            } else {
                binding.deadlineEditText.text?.clear()
            }
        }
    }
    
    private fun showDatePicker() {
        val currentTimestamp = viewModel.deadlineTimestamp.value ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = currentTimestamp
        
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Deadline")
            .setSelection(currentTimestamp)
            .build()
        
        datePicker.addOnPositiveButtonClickListener { selection ->
            val timestamp = selection ?: System.currentTimeMillis()
            viewModel.setDeadlineTimestamp(timestamp)
        }
        
        datePicker.show(parentFragmentManager, "datePicker")
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = timestamp
        return sdf.format(calendar.time)
    }
    
    private fun updateViewModelFromUI() {
        // Explicitly read current UI values and update ViewModel
        val selectedStatus = binding.statusAutoComplete.text?.toString() ?: ""
        try {
            val status = TaskStatus.valueOf(selectedStatus.uppercase())
            viewModel.status.value = status
        } catch (e: IllegalArgumentException) {
            // If invalid, keep current ViewModel value or use default
            if (viewModel.status.value == null) {
                viewModel.status.value = TaskStatus.TODO
            }
        }
        
        val selectedPriority = binding.priorityAutoComplete.text?.toString() ?: ""
        try {
            val priority = TaskPriority.valueOf(selectedPriority.uppercase())
            viewModel.priority.value = priority
        } catch (e: IllegalArgumentException) {
            // If invalid, keep current ViewModel value or use default
            if (viewModel.priority.value == null) {
                viewModel.priority.value = TaskPriority.MEDIUM
            }
        }
    }
}

