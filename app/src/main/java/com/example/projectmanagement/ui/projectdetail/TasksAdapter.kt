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

class TasksAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onAddMembersClick: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding, onItemClick, onEditClick, onDeleteClick, onAddMembersClick)
    }
    
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onItemClick: (Task) -> Unit,
        private val onEditClick: (Task) -> Unit,
        private val onDeleteClick: (Task) -> Unit,
        private val onAddMembersClick: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(task: Task) {
            binding.titleTextView.text = task.title
            binding.descriptionTextView.text = task.description
            binding.statusChip.text = task.status.toString()
            binding.priorityTextView.text = task.priority.toString()
            binding.assigneeTextView.text = task.assigneeName
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

