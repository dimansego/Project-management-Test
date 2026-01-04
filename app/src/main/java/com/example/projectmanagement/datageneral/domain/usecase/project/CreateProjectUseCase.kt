package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.model.project.Project
import com.example.projectmanagement.datageneral.data.repository.project.ProjectMemberRepository
import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectEntity
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectRole
import com.example.projectmanagement.datageneral.domain.entity.project.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val projectMemberRepository: ProjectMemberRepository
) {
    suspend operator fun invoke(project: Project): Result<ProjectEntity> =
        runCatching {
            val created = projectRepository.insertProject(project).asEntity()

            //If project member creation fails, throw exception
            try {
                projectMemberRepository.addMember(
                    created.id,
                    created.ownerId,
                    ProjectRole.LEADER.value
                )
            } catch (e: Exception) {
                projectRepository.deleteProject(created.id)
                throw e
            }
            created
        }.mapFailure { it.toProjectFailure() }
}