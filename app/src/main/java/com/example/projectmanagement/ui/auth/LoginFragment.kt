package com.example.projectmanagement.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentLoginBinding
import com.example.projectmanagement.datageneral.repository.ProjectRepository
import com.example.projectmanagement.datageneral.repository.SessionManager
import com.example.projectmanagement.ui.common.UiState
import com.example.projectmanagement.ui.viewmodel.HomeViewModel
import com.example.projectmanagement.ui.viewmodel.HomeViewModelFactory
import com.example.projectmanagement.ui.viewmodel.LoginViewModel
import com.example.projectmanagement.ui.viewmodel.LoginViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels {
        val app = requireActivity().application as ProjectApplication
        HomeViewModelFactory(
            ProjectRepository(
                app.database.projectDao(),
                app.database.taskDao()
            ),
            app.syncRepository,
            app.userRepository
        )
    }
    private val viewModel: LoginViewModel by viewModels { // Changed from activityViewModels
        LoginViewModelFactory(
            (activity?.application as ProjectApplication).signInUserUseCase
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }
        
        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        
        binding.forgotPasswordLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        
        // Set up two-way binding for email and password
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
        
        // Observe email error
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.emailInputLayout.error = error
            binding.emailInputLayout.isErrorEnabled = error != null
        }
        
        // Observe password error
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.passwordInputLayout.error = error
            binding.passwordInputLayout.isErrorEnabled = error != null
        }
        
        // Observe login state
        viewModel.loginState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Success -> {
                    SessionManager.setCurrentUser(state.data)
                    homeViewModel.refreshData()
                    // Navigate to main navigation
                    findNavController().navigate(R.id.action_loginFragment_to_main_nav)
                }
                is UiState.Error -> {
                    binding.errorTextView.text = state.message
                    binding.errorTextView.visibility = View.VISIBLE
                }
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }
}

