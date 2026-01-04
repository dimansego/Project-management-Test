package com.example.projectmanagement.ui.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.databinding.ItemMemberBinding
import com.example.projectmanagement.ui.viewmodel.MemberUi

class MembersAdapter : ListAdapter<MemberUi, MembersAdapter.MemberViewHolder>(MemberDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class MemberViewHolder(
        private val binding: ItemMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(member: MemberUi) {
            binding.memberNameText.text = member.userName
            binding.memberEmailText.text = member.userEmail
            binding.memberRoleText.text = member.role ?: "Member"
        }
    }
    
    class MemberDiffCallback : DiffUtil.ItemCallback<MemberUi>() {
        override fun areItemsTheSame(oldItem: MemberUi, newItem: MemberUi): Boolean {
            return oldItem.userId == newItem.userId
        }
        
        override fun areContentsTheSame(oldItem: MemberUi, newItem: MemberUi): Boolean {
            return oldItem == newItem
        }
    }
}


