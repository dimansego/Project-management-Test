package com.example.projectmanagement.datageneral.domain.usecase.meeting

import com.example.projectmanagement.datageneral.data.model.meeting.Attachment
import com.example.projectmanagement.datageneral.data.repository.meeting.AttachmentRepository
import com.example.projectmanagement.datageneral.domain.entity.meeting.AttachmentEntity
import com.example.projectmanagement.datageneral.domain.entity.meeting.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.meeting.exception.toAttachmentFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class AddAttachmentUseCase(private val repo: AttachmentRepository) {

    suspend operator fun invoke(attachment: Attachment): Result<AttachmentEntity> =
        runCatching {
            repo.createAttachment(attachment).asEntity()
        }.mapFailure { it.toAttachmentFailure() }

}