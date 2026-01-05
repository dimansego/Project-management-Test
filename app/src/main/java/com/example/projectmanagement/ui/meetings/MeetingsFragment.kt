package com.example.projectmanagement.ui.meetings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.ProjectApplication
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentMeetingsBinding
import com.example.projectmanagement.ui.viewmodel.MeetingsViewModel
import com.example.projectmanagement.ui.viewmodel.MeetingsViewModelFactory

class MeetingsFragment : Fragment() {
    private var _binding: FragmentMeetingsBinding? = null
    private val binding get() = _binding!!
    // Inside MeetingsFragment.kt
    private val viewModel: MeetingsViewModel by viewModels {
        // Cast to 'ProjectApplication' instead of 'MyApplication'
        val app = requireActivity().application as ProjectApplication
        MeetingsViewModelFactory(
            app.syncRepository,
            app.authRepository
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
        
        adapter = MeetingsAdapter(
            onEditClick = { meeting ->
                // TODO: Navigate to edit meeting screen
                android.widget.Toast.makeText(context, "Edit Meeting: ${meeting.title}", android.widget.Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { meeting ->
                // TODO: Show delete confirmation and delete meeting
                android.widget.Toast.makeText(context, "Delete Meeting: ${meeting.title}", android.widget.Toast.LENGTH_SHORT).show()
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
}

