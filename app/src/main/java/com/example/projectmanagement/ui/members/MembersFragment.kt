package com.example.projectmanagement.ui.members

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.FragmentMembersBinding
import com.example.projectmanagement.ui.viewmodel.MembersViewModel
import com.example.projectmanagement.ui.viewmodel.MembersViewModelFactory
import com.example.projectmanagement.ProjectApplication

class MembersFragment : Fragment() {
    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MembersViewModel by viewModels {
        MembersViewModelFactory(
            (activity?.application as ProjectApplication).syncRepository
        )
    }
    private lateinit var membersAdapter: MembersAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val projectId = MembersFragmentArgs.fromBundle(requireArguments()).projectId
        
        membersAdapter = MembersAdapter()
        binding.membersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.membersRecyclerView.adapter = membersAdapter
        
        viewModel.loadMembers(projectId)
        viewModel.loadProject(projectId)
        
        viewModel.members.observe(viewLifecycleOwner) { members ->
            membersAdapter.submitList(members)
        }
        
        viewModel.project.observe(viewLifecycleOwner) { project ->
            project?.let {
                binding.projectTitleText.text = it.title
            }
        }
        
        viewModel.inviteCode.observe(viewLifecycleOwner) { inviteCode ->
            binding.inviteCodeText.text = inviteCode ?: "N/A"
        }
        
        binding.copyInviteCodeButton.setOnClickListener {
            val inviteCode = viewModel.inviteCode.value
            if (inviteCode != null && inviteCode.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Project Invite Code", inviteCode)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Invite code copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No invite code available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

