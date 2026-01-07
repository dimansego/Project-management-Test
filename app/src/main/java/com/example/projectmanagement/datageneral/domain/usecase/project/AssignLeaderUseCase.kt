package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.repository.project.ProjectMemberRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectRole
import com.example.projectmanagement.datageneral.domain.entity.user.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class AssignLeaderUseCase(
    private val memberRepo: ProjectMemberRepository,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(projectId: String, newLeaderId: String): Result<Unit> {
        return runCatching {
            val currentUser = userRepo.getCurrentUser()?.asEntity() ?: throw UserAuthFailure.Unauthorized()

            //Demote. Already ensure that the current user is the project leader.
            memberRepo.updateRole(projectId, currentUser.id, ProjectRole.MEMBER.value)

            //Promote.
            memberRepo.updateRole(projectId, newLeaderId, ProjectRole.LEADER.value)
        }.mapFailure { it.toProjectFailure() }
    }
}