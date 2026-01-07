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
import com.example.projectmanagement.ui.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as com.example.projectmanagement.ProjectApplication
        ProfileViewModelFactory(
            app.authRepository, // Pass AuthRepository
            app.userRepository,  // Pass UserRepository
                    com.example.projectmanagement.datageneral.repository.ProjectRepository(
                    app.database.projectDao(),
            app.database.taskDao())
        )
    }
    
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

        // 1. Trigger logout logic in ViewModel
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        // 2. Observe logout success to navigate
        // In ProfileFragment.kt onViewCreated
        // Inside ProfileFragment.kt
        viewModel.logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // Use the activity's NavController (the one managing root_nav)
                val rootNavController = androidx.navigation.Navigation.findNavController(
                    requireActivity(),
                    R.id.navHostFragment // Ensure this ID matches your FragmentContainerView in activity_main.xml
                )

                // Navigate using the GLOBAL action ID defined in root_nav.xml
                // This clears the stack and moves the user to the auth graph
                rootNavController.navigate(R.id.action_global_to_auth)
            }
        }

        // 3. Observe current user and update UI
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Display the profile name from Supabase
                binding.nameTextView.text = user.name ?: "No Name Set"
                binding.emailTextView.text = user.email
            }
        }
    }
}

