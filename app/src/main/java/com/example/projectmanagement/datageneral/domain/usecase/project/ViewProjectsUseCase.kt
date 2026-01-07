package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectEntity
import com.example.projectmanagement.datageneral.domain.entity.project.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.ProjectFailure
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ViewProjectsUseCase(private val projectRepository: ProjectRepository) {
    suspend operator fun invoke(): Result<List<ProjectEntity>> =
        runCatching {
            projectRepository.getAllProjects()
                .map { it.asEntity() }
        }.mapFailure { it.toProjectFailure() }

    suspend operator fun invoke(projectId: String): Result<ProjectEntity> =
        runCatching {
            projectRepository.getProjectById(projectId)?.asEntity() ?: throw ProjectFailure.NotFound()
        }.mapFailure { it.toProjectFailure() }
}
