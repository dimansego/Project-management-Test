package com.example.projectmanagement.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.ItemTaskCardBinding
import com.example.projectmanagement.datageneral.core.DateFormatter
import com.example.projectmanagement.ui.viewmodel.TaskUi

class TasksAdapter(
    private val onItemClick: (TaskUi) -> Unit,
    private val onEditClick: (TaskUi) -> Unit,
    private val onDeleteClick: (TaskUi) -> Unit,
    private val onAddMembersClick: (TaskUi) -> Unit
) : ListAdapter<TaskUi, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskCardBinding.inflate(
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
        private val binding: ItemTaskCardBinding,
        private val onItemClick: (TaskUi) -> Unit,
        private val onEditClick: (TaskUi) -> Unit,
        private val onDeleteClick: (TaskUi) -> Unit,
        private val onAddMembersClick: (TaskUi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(taskUi: TaskUi) {
            binding.taskTitle.text = taskUi.task.title
            binding.projectNameText.text = taskUi.projectTitle
            val formattedDeadline = DateFormatter.formatDeadline(taskUi.task.deadline)
            binding.dueDateText.text = binding.root.context.getString(
                com.example.projectmanagement.R.string.deadline_label,
                formattedDeadline
            )
            binding.root.setOnClickListener {
                onItemClick(taskUi)
            }
            
            binding.moreOptionsButton.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.task_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClick(taskUi)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(taskUi)
                            true
                        }
                        R.id.menu_add_members -> {
                            onAddMembersClick(taskUi)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
    
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskUi>() {
        override fun areItemsTheSame(oldItem: TaskUi, newItem: TaskUi): Boolean {
            return oldItem.task.id == newItem.task.id
        }
        
        override fun areContentsTheSame(oldItem: TaskUi, newItem: TaskUi): Boolean {
            return oldItem == newItem
        }
    }
}

