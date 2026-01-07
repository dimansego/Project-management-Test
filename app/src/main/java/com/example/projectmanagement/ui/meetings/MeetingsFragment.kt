package com.example.projectmanagement.ui.meetings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.databinding.FragmentMeetingsBinding
import com.example.projectmanagement.ui.viewmodel.MeetingsViewModel
import com.example.projectmanagement.ui.viewmodel.MeetingsViewModelFactory
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository

class MeetingsFragment : Fragment() {
    private var _binding: FragmentMeetingsBinding? = null
    private val binding get() = _binding!!
    // Inside MeetingsFragment.kt

    private val args: MeetingsFragmentArgs by navArgs()
    private val viewModel: MeetingsViewModel by viewModels {
        val app = requireActivity().application as ProjectApplication
        MeetingsViewModelFactory(
            app.syncRepository,
            app.userRepository // CHANGE THIS from app.authRepository
        )
    }
    private lateinit var adapter: MeetingsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val projectId = args.projectId

        // Load meetings specifically for this project
        viewModel.loadMeetingsByProject(projectId)
        adapter = MeetingsAdapter(
            onEditClick = { meeting ->
                // Navigate to edit meeting screen
                val action = MeetingsFragmentDirections.actionMeetingsFragmentToCreateMeetingFragment(
                    projectId = meeting.projectId,
                    meetingId = meeting.id ?: "",
                    title = "Edit Meeting"
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { meeting ->
                // Show delete confirmation dialog
                showDeleteConfirmationDialog(meeting.id ?: "", meeting.projectId)
            },
            onAddMembersClick = { meeting ->
                // TODO: Show add members dialog
                android.widget.Toast.makeText(context, "Add Members to Meeting: ${meeting.title}", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
        binding.meetingsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.meetingsRecyclerView.adapter = adapter
        
        viewModel.meetings.observe(viewLifecycleOwner, Observer { meetings ->
            adapter.submitList(meetings)
        })
    }

    private fun showDeleteConfirmationDialog(meetingId: String, projectId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Meeting")
            .setMessage("Are you sure you want to delete this meeting?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteMeeting(meetingId, projectId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh meetings when fragment resumes (e.g., after creating a new meeting)
        viewModel.refreshMeetings()
    }
}

