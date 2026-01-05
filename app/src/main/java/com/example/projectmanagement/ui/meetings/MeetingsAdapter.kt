package com.example.projectmanagement.ui.meetings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.ItemMeetingBinding
import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


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
            
            // Format start and end times for display
            val startTimeFormatted = MeetingsAdapter.formatDateTime(meeting.startTime)
            val endTimeFormatted = MeetingsAdapter.formatDateTime(meeting.endTime)
            binding.dateTimeTextView.text = "$startTimeFormatted - $endTimeFormatted"
            
            // Display location
            binding.locationTextView.text = "Location: ${meeting.location}"
            
            binding.participantsTextView.text = binding.root.context.getString(
                com.example.projectmanagement.R.string.meeting_participants,
                "TODO" // meeting.participants is not in Meeting model yet
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
    
    companion object {
        fun formatDateTime(isoString: String): String {
            return try {
                // Try parsing ISO-8601 format (e.g., "2026-01-05T13:30:00Z")
                val dateTime = if (isoString.contains('T')) {
                    // ISO-8601 format - handle both "Z" and "+00:00" formats
                    val normalized = if (isoString.endsWith("Z")) {
                        isoString.replace("Z", "+00:00")
                    } else {
                        isoString
                    }
                    OffsetDateTime.parse(normalized)
                } else {
                    // Try the DateTimeConfig format (e.g., "yyyy-MM-dd HH:mm:ss.SSSSSSXXX")
                    OffsetDateTime.parse(isoString)
                }
                
                // Format as "MMM dd, yyyy 'at' hh:mm a"
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                dateTime.format(formatter)
            } catch (e: Exception) {
                // If parsing fails, return the original string
                isoString
            }
        }
    }
}
