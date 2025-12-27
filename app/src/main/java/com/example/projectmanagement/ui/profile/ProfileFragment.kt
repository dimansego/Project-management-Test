package com.example.projectmanagement.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentProfileBinding
import com.example.projectmanagement.ui.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            // Navigate to auth graph using global action immediately
            findNavController().navigate(R.id.action_global_to_auth)
        }
        
        // Observe current user and update UI
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.nameTextView.text = user.name
                binding.emailTextView.text = user.email
            }
        }
    }
}

