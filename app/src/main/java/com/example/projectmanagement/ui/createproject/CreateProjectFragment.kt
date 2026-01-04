package com.example.projectmanagement.ui.createproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentCreateProjectBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.CreateProjectViewModel
import com.example.projectmanagement.ui.viewmodel.CreateProjectViewModelFactory

class CreateProjectFragment : Fragment() {
    private var _binding: FragmentCreateProjectBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateProjectViewModel by viewModels {
        CreateProjectViewModelFactory(
            (activity?.application as ProjectApplication).syncRepository
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupFormFields()
        
        binding.saveButton.setOnClickListener {
            viewModel.saveProject()
        }
        
        // Observe project name error
        viewModel.projectNameError.observe(viewLifecycleOwner) { error ->
            binding.projectNameInputLayout.error = error
            binding.projectNameInputLayout.isErrorEnabled = error != null
        }
        
        viewModel.saveState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    findNavController().popBackStack()
                }
                is UiState.Error -> {
                    android.widget.Toast.makeText(
                        context,
                        "Error saving project: ${state.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                is UiState.Loading -> {
                    // Show loading indicator if needed
                }
                else -> {}
            }
        })
    }
    
    private fun setupFormFields() {
        binding.projectNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setProjectName(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        binding.descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setDescription(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        // Observe ViewModel fields to update EditTexts when ViewModel changes
        viewModel.projectName.observe(viewLifecycleOwner) { name ->
            if (binding.projectNameEditText.text?.toString() != name) {
                binding.projectNameEditText.setText(name)
            }
        }
        
        viewModel.description.observe(viewLifecycleOwner) { desc ->
            if (binding.descriptionEditText.text?.toString() != desc) {
                binding.descriptionEditText.setText(desc)
            }
        }
    }
}

