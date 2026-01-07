package com.example.projectmanagement.datageneral.domain.usecase.meeting

import com.example.projectmanagement.datageneral.data.model.meeting.Meeting
import com.example.projectmanagement.datageneral.data.repository.meeting.MeetingRepository
import com.example.projectmanagement.datageneral.domain.entity.meeting.MeetingEntity
import com.example.projectmanagement.datageneral.domain.entity.meeting.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.meeting.exception.toMeetingFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class ScheduleMeetingUseCase(private val repository: MeetingRepository) {

    suspend operator fun invoke(meeting: Meeting): Result<MeetingEntity> =
        runCatching {
            repository.createMeeting(meeting).asEntity()
        }.mapFailure { it.toMeetingFailure() }

}