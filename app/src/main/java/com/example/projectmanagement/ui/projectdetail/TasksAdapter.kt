package com.example.projectmanagement.ui.projectdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.R
import com.example.projectmanagement.datageneral.model.Task
import com.example.projectmanagement.databinding.ItemTaskBinding
import com.example.projectmanagement.datageneral.core.DateFormatter
import com.example.projectmanagement.datageneral.data.model.user.AppUser

class TasksAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onAddMembersClick: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {
    private var projectMembers: List<AppUser> = emptyList()

    fun setMembers(members: List<AppUser>) {
        this.projectMembers = members
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding, onItemClick, onEditClick, onDeleteClick, onAddMembersClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), projectMembers)
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onItemClick: (Task) -> Unit,
        private val onEditClick: (Task) -> Unit,
        private val onDeleteClick: (Task) -> Unit,
        private val onAddMembersClick: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task, members: List<AppUser>) {
            binding.titleTextView.text = task.title
            binding.descriptionTextView.text = task.description
            binding.statusChip.text = task.status.toString()
            binding.priorityTextView.text = task.priority.toString()
            
            // In the domain model, assigneeName holds the ID due to repository mapping
            val assigneeId = task.assigneeName
            
            val userName = if (assigneeId.isEmpty()) {
                "Unassigned"
            } else {
                // Find the member in the list whose ID matches the task's assigneeId
                members.find { it.id == assigneeId }?.name ?: "Unknown User"
            }
            binding.assigneeTextView.text = userName
            
            val formattedDeadline = DateFormatter.formatDeadline(task.deadline)
            binding.deadlineTextView.text = binding.root.context.getString(
                com.example.projectmanagement.R.string.deadline_label,
                formattedDeadline
            )
            binding.root.setOnClickListener {
                onItemClick(task)
            }

            binding.moreOptionsButton.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.task_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClick(task)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(task)
                            true
                        }
                        R.id.menu_add_members -> {
                            onAddMembersClick(task)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
