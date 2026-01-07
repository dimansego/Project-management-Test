package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectEntity
import com.example.projectmanagement.datageneral.domain.entity.project.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class UpdateProjectUseCase(private val repo: ProjectRepository) {
    suspend operator fun invoke(project: ProjectEntity): Result<ProjectEntity> =
        runCatching {
            require(project.modifiedFields.isNotEmpty()) { "No changes to update" }

            repo.updateProject(project.id, project.asUpdateMap()).asEntity()
        }.mapFailure { it.toProjectFailure() }
}