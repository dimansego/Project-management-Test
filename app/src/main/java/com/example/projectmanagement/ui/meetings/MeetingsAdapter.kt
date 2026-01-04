package com.example.projectmanagement.ui.meetings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.ItemMeetingBinding
import com.example.projectmanagement.ui.viewmodel.Meeting

class MeetingsAdapter(
    private val onEditClick: (Meeting) -> Unit,
    private val onDeleteClick: (Meeting) -> Unit,
    private val onAddMembersClick: (Meeting) -> Unit
) : ListAdapter<Meeting, MeetingsAdapter.MeetingViewHolder>(MeetingDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val binding = ItemMeetingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MeetingViewHolder(binding, onEditClick, onDeleteClick, onAddMembersClick)
    }
    
    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class MeetingViewHolder(
        private val binding: ItemMeetingBinding,
        private val onEditClick: (Meeting) -> Unit,
        private val onDeleteClick: (Meeting) -> Unit,
        private val onAddMembersClick: (Meeting) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(meeting: Meeting) {
            binding.titleTextView.text = meeting.title
            binding.dateTimeTextView.text = binding.root.context.getString(
                com.example.projectmanagement.R.string.meeting_datetime,
                meeting.date,
                meeting.time
            )
            binding.participantsTextView.text = binding.root.context.getString(
                com.example.projectmanagement.R.string.meeting_participants,
                meeting.participants
            )
            
            binding.moreOptionsButton.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.task_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClick(meeting)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(meeting)
                            true
                        }
                        R.id.menu_add_members -> {
                            onAddMembersClick(meeting)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
    
    class MeetingDiffCallback : DiffUtil.ItemCallback<Meeting>() {
        override fun areItemsTheSame(oldItem: Meeting, newItem: Meeting): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Meeting, newItem: Meeting): Boolean {
            return oldItem == newItem
        }
    }
}

