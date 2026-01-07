package com.example.projectmanagement.datageneral.domain.usecase.meeting

import com.example.projectmanagement.datageneral.data.repository.meeting.MeetingRepository
import com.example.projectmanagement.datageneral.domain.entity.meeting.MeetingEntity
import com.example.projectmanagement.datageneral.domain.entity.meeting.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.meeting.exception.MeetingFailure
import com.example.projectmanagement.datageneral.domain.usecase.meeting.exception.toMeetingFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ViewMeetingsUseCase(
    private val meetingRepo: MeetingRepository,
) {

    suspend operator fun invoke(projectId: String): Result<List<MeetingEntity>> =
        runCatching {
            meetingRepo.getMeetingsByProjectId(projectId).map { it.asEntity() }
        }.mapFailure { it.toMeetingFailure() }

    suspend operator fun invoke(projectId: String, meetingId: String): Result<MeetingEntity> =
        runCatching {
            meetingRepo.getMeetingById(meetingId, projectId)?.asEntity() ?: throw MeetingFailure.NotFound()
        }.mapFailure { it.toMeetingFailure() }
}
