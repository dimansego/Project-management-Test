package com.example.projectmanagement.ui.createmeeting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentCreateMeetingBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateMeetingFragment : Fragment() {
    private var _binding: FragmentCreateMeetingBinding? = null
    private val binding get() = _binding!!
    
    private var meetingName: String = ""
    private var description: String = ""
    private var selectedTime: String = ""
    
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
        
        setupFormFields()
        setupTimePicker()
        
        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                saveMeeting()
            }
        }
    }
    
    private fun setupFormFields() {
        binding.meetingNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                meetingName = s?.toString() ?: ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        binding.descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                description = s?.toString() ?: ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupTimePicker() {
        binding.timeEditText.setOnClickListener {
            showTimePicker()
        }
    }
    
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Select Meeting Time")
            .build()
        
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val time = String.format(Locale.getDefault(), "%02d:%02d %s", 
                if (hour > 12) hour - 12 else if (hour == 0) 12 else hour,
                minute,
                if (hour >= 12) "PM" else "AM"
            )
            selectedTime = time
            binding.timeEditText.setText(time)
        }
        
        picker.show(parentFragmentManager, "timePicker")
    }
    
    private fun validateInput(): Boolean {
        var isValid = true
        
        if (meetingName.trim().isEmpty()) {
            binding.meetingNameInputLayout.error = "Meeting name is required"
            binding.meetingNameInputLayout.isErrorEnabled = true
            isValid = false
        } else {
            binding.meetingNameInputLayout.isErrorEnabled = false
        }
        
        if (selectedTime.isEmpty()) {
            binding.timeInputLayout.error = "Time is required"
            binding.timeInputLayout.isErrorEnabled = true
            isValid = false
        } else {
            binding.timeInputLayout.isErrorEnabled = false
        }
        
        return isValid
    }
    
    private fun saveMeeting() {
        // For now, just navigate back
        // In a full implementation, this would save to database
        // TODO: Save meeting to database
        findNavController().popBackStack()
    }
}

