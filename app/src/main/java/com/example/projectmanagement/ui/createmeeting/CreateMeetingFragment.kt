package com.example.projectmanagement.ui.createmeeting

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.databinding.FragmentCreateMeetingBinding
import com.example.projectmanagement.ui.viewmodel.CreateMeetingViewModel
import com.example.projectmanagement.ui.viewmodel.CreateMeetingViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class CreateMeetingFragment : Fragment() {
    private var _binding: FragmentCreateMeetingBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CreateMeetingViewModel by viewModels {
        val app = requireActivity().application as ProjectApplication
        CreateMeetingViewModelFactory(
            app.syncRepository,
            app.userRepository
        )
    }
    
    private var projectId: String = ""
    private var startDate: LocalDate? = null
    private var startTime: LocalTime? = null
    private var endDate: LocalDate? = null
    private var endTime: LocalTime? = null
    
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMeetingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get projectId and meetingId from navigation arguments
        val args = CreateMeetingFragmentArgs.fromBundle(requireArguments())
        projectId = args.projectId
        val meetingId = args.meetingId
        
        // Check if we're in edit mode
        if (meetingId.isNotEmpty()) {
            // Update title to "Edit Meeting"
            activity?.title = "Edit Meeting"
            // Load the meeting for editing
            viewModel.loadMeetingForEdit(meetingId, projectId)
        } else {
            activity?.title = "Create Meeting"
        }
        
        setupFormFields()
        setupDateTimePickers()
        setupObservers()
        
        binding.saveButton.setOnClickListener {
            saveMeeting()
        }
    }
    
    private fun setupFormFields() {
        // Clear errors when user starts typing
        binding.meetingNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.meetingNameInputLayout.isErrorEnabled = false
            }
        }
        
        binding.locationEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.locationInputLayout.isErrorEnabled = false
            }
        }
    }
    
    private fun setupDateTimePickers() {
        // Start Date Picker
        binding.startDateEditText.setOnClickListener {
            showDatePicker(true)
        }
        
        // Start Time Picker
        binding.startTimeEditText.setOnClickListener {
            showTimePicker(true)
        }
        
        // End Date Picker
        binding.endDateEditText.setOnClickListener {
            showDatePicker(false)
        }
        
        // End Time Picker
        binding.endTimeEditText.setOnClickListener {
            showTimePicker(false)
        }
    }
    
    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                if (isStartDate) {
                    startDate = selectedDate
                    binding.startDateEditText.setText(selectedDate.format(dateFormatter))
                } else {
                    endDate = selectedDate
                    binding.endDateEditText.setText(selectedDate.format(dateFormatter))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
    
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(if (isStartTime) "Select Start Time" else "Select End Time")
            .build()
        
        picker.addOnPositiveButtonClickListener {
            val selectedTime = LocalTime.of(picker.hour, picker.minute)
            if (isStartTime) {
                startTime = selectedTime
                binding.startTimeEditText.setText(selectedTime.format(timeFormatter))
            } else {
                endTime = selectedTime
                binding.endTimeEditText.setText(selectedTime.format(timeFormatter))
            }
        }
        
        picker.show(parentFragmentManager, if (isStartTime) "startTimePicker" else "endTimePicker")
    }
    
    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is com.example.projectmanagement.ui.common.UiState.Loading -> {
                    binding.saveButton.isEnabled = false
                    binding.saveButton.text = "Saving..."
                }
                is com.example.projectmanagement.ui.common.UiState.Success -> {
                    // Navigate back on success
                    findNavController().popBackStack()
                }
                is com.example.projectmanagement.ui.common.UiState.Error -> {
                    binding.saveButton.isEnabled = true
                    binding.saveButton.text = "Save"
                    android.widget.Toast.makeText(
                        requireContext(),
                        state.message,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        
        viewModel.titleError.observe(viewLifecycleOwner, Observer { error ->
            binding.meetingNameInputLayout.error = error
            binding.meetingNameInputLayout.isErrorEnabled = error != null
        })
        
        viewModel.locationError.observe(viewLifecycleOwner, Observer { error ->
            binding.locationInputLayout.error = error
            binding.locationInputLayout.isErrorEnabled = error != null
        })
        
        viewModel.startDateTimeError.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                binding.startDateInputLayout.error = error
                binding.startDateInputLayout.isErrorEnabled = true
                binding.startTimeInputLayout.error = error
                binding.startTimeInputLayout.isErrorEnabled = true
            } else {
                binding.startDateInputLayout.isErrorEnabled = false
                binding.startTimeInputLayout.isErrorEnabled = false
            }
        })
        
        viewModel.endDateTimeError.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                binding.endDateInputLayout.error = error
                binding.endDateInputLayout.isErrorEnabled = true
                binding.endTimeInputLayout.error = error
                binding.endTimeInputLayout.isErrorEnabled = true
            } else {
                binding.endDateInputLayout.isErrorEnabled = false
                binding.endTimeInputLayout.isErrorEnabled = false
            }
        })

        viewModel.loadedMeeting.observe(viewLifecycleOwner, Observer { meeting ->
            meeting?.let {
                // Pre-fill the form with existing meeting data
                binding.meetingNameEditText.setText(it.title)
                binding.locationEditText.setText(it.location)
                it.description?.let { desc ->
                    binding.descriptionEditText.setText(desc)
                }

                // Parse and set start date/time
                parseAndSetDateTime(it.startTime, true)
                
                // Parse and set end date/time
                parseAndSetDateTime(it.endTime, false)
            }
        })
    }

    private fun parseAndSetDateTime(isoString: String, isStart: Boolean) {
        try {
            val normalized = if (isoString.endsWith("Z")) {
                isoString.replace("Z", "+00:00")
            } else {
                isoString
            }
            val dateTime = OffsetDateTime.parse(normalized)
            val localDate = dateTime.toLocalDate()
            val localTime = dateTime.toLocalTime()
            
            if (isStart) {
                startDate = localDate
                startTime = localTime
                binding.startDateEditText.setText(localDate.format(dateFormatter))
                binding.startTimeEditText.setText(localTime.format(timeFormatter))
            } else {
                endDate = localDate
                endTime = localTime
                binding.endDateEditText.setText(localDate.format(dateFormatter))
                binding.endTimeEditText.setText(localTime.format(timeFormatter))
            }
        } catch (e: Exception) {
            // If parsing fails, leave fields empty
            e.printStackTrace()
        }
    }
    
    private fun saveMeeting() {
        val title = binding.meetingNameEditText.text?.toString() ?: ""
        val description = binding.descriptionEditText.text?.toString()?.takeIf { it.isNotEmpty() }
        val location = binding.locationEditText.text?.toString() ?: ""
        
        viewModel.saveMeeting(
            projectId = projectId,
            title = title,
            description = description,
            location = location,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime
        )
    }
}

