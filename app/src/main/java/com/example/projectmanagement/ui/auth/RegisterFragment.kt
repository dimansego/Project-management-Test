package com.example.projectmanagement.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentRegisterBinding
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.RegisterViewModel
import com.example.projectmanagement.ui.viewmodel.RegisterViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory(
            (activity?.application as ProjectApplication).signUpUserUseCase
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up two-way binding for form fields
        binding.nameEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setName(s?.toString() ?: "")
                viewModel.clearErrors()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        binding.emailEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setEmail(s?.toString() ?: "")
                viewModel.clearErrors()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        binding.passwordEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPassword(s?.toString() ?: "")
                viewModel.clearErrors()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        binding.confirmPasswordEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setConfirmPassword(s?.toString() ?: "")
                viewModel.clearErrors()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        // Observe error states
        viewModel.nameError.observe(viewLifecycleOwner) { error ->
            binding.nameInputLayout.error = error
            binding.nameInputLayout.isErrorEnabled = error != null
        }
        
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.emailInputLayout.error = error
            binding.emailInputLayout.isErrorEnabled = error != null
        }
        
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.passwordInputLayout.error = error
            binding.passwordInputLayout.isErrorEnabled = error != null
        }
        
        viewModel.confirmPasswordError.observe(viewLifecycleOwner) { error ->
            binding.confirmPasswordInputLayout.error = error
            binding.confirmPasswordInputLayout.isErrorEnabled = error != null
        }
        
        binding.registerButton.setOnClickListener {
            viewModel.register()
        }
        
        viewModel.registerState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context, 
                        "Registration successful! Please check your email to confirm your account.", 
                        Toast.LENGTH_LONG
                    ).show()
                    // Navigate back to login screen
                    findNavController().popBackStack()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    // Show error message
                    val errorMsg = state.message
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    android.util.Log.e("RegisterFragment", "Registration error: $errorMsg")
                }
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }
}

