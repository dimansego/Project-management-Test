package com.example.projectmanagement.datageneral.domain.usecase.project

import com.example.projectmanagement.datageneral.data.repository.project.ProjectMemberRepository
import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.domain.entity.project.ProjectRole
import com.example.projectmanagement.datageneral.domain.entity.project.asEntity
import com.example.projectmanagement.datageneral.domain.entity.user.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.project.exception.toProjectFailure
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure
import java.time.Instant

class JoinProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(inviteCode: String): Result<Unit> =
        runCatching {
            val currentUser = userRepo.getCurrentUser()?.asEntity() ?: throw UserAuthFailure.Unauthorized()

            val project = projectRepository.getProjectByInviteCode(inviteCode)?.asEntity()
                ?: error("Code $inviteCode doesn't exist")

//            require(project.isInviteActive) { "Invite is no longer active" }
            require(project.inviteExpireDate.isAfter(Instant.now())) {
                "Invite code has expired"
            }

            projectMemberRepository.addMember(
                projectId = project.id,
                userId = currentUser.id,
                role = ProjectRole.MEMBER.value
            )

            Unit
        }.mapFailure { it.toProjectFailure() }
}
