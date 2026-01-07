package com.example.projectmanagement.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanagement.R
import com.example.projectmanagement.databinding.ItemProjectCardBinding
import com.example.projectmanagement.ui.viewmodel.ProjectUi

class ProjectsAdapter(
    private val onItemClick: (ProjectUi) -> Unit,
    private val onEditClick: (ProjectUi) -> Unit,
    private val onDeleteClick: (ProjectUi) -> Unit,
    private val onSeeMembersClick: (ProjectUi) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {
    
    private var projects: List<ProjectUi> = emptyList()
    
    fun submitList(list: List<ProjectUi>) {
        projects = list
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectViewHolder(binding, onItemClick, onEditClick, onDeleteClick, onSeeMembersClick)
    }
    
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }
    
    override fun getItemCount(): Int = projects.size
    
    class ProjectViewHolder(
        private val binding: ItemProjectCardBinding,
        private val onItemClick: (ProjectUi) -> Unit,
        private val onEditClick: (ProjectUi) -> Unit,
        private val onDeleteClick: (ProjectUi) -> Unit,
        private val onSeeMembersClick: (ProjectUi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(projectUi: ProjectUi) {
            binding.projectTitle.text = projectUi.project.title
            binding.unfinishedTasks.text = "There are ${projectUi.taskCount} unfinished tasks"
            binding.root.setOnClickListener {
                onItemClick(projectUi)
            }
            
            binding.moreOptionsButton.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.project_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit_project -> {
                            onEditClick(projectUi)
                            true
                        }
                        R.id.menu_delete_project -> {
                            onDeleteClick(projectUi)
                            true
                        }
                        R.id.menu_see_members -> {
                            onSeeMembersClick(projectUi)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}

