package com.example.projectmanagement.datageneral.domain.usecase.user

import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.domain.entity.user.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.toUserAuthFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class GetUserByIdUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String) = runCatching {
        userRepository.getUserById(userId)?.asEntity() ?: throw UserAuthFailure.NotFound()
    }.mapFailure { it.toUserAuthFailure() }
}

class GetCurrentUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = runCatching {
        userRepository.getCurrentUser()?.asEntity() ?: throw UserAuthFailure.Unauthorized()
    }.mapFailure { it.toUserAuthFailure() }
}