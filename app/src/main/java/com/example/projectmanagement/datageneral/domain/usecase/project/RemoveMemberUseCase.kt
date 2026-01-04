package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.repository.project.ProjectMemberRepository
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class RemoveMemberUseCase(
    private val projectMemberRepository: ProjectMemberRepository
) {
    suspend operator fun invoke(
        projectId: String,
        userIdToRemove: String
    ): Result<Unit> =
        runCatching {
            projectMemberRepository.removeMember(
                projectId = projectId,
                userId = userIdToRemove
            )
        }.mapFailure { it.toProjectFailure() }
}
