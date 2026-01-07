package com.example.projectmanagement.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentForgotPasswordBinding
import com.example.projectmanagement.ui.viewmodel.ForgotPasswordViewModel
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up email field
        binding.emailEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setEmail(s?.toString() ?: "")
                viewModel.clearErrors()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        // Observe email error
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.emailInputLayout.error = error
            binding.emailInputLayout.isErrorEnabled = error != null
        }
        
        binding.sendResetLinkButton.setOnClickListener {
            viewModel.sendResetLink()
        }
        
        viewModel.resetLinkSent.observe(viewLifecycleOwner, Observer { sent ->
            if (sent) {
                Snackbar.make(binding.root, "Reset link sent to your email", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}

