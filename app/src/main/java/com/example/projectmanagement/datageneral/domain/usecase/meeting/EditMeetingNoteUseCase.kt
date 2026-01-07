package com.example.projectmanagement.datageneral.domain.usecase.meeting

import com.example.projectmanagement.datageneral.data.repository.meeting.MeetingRepository
import com.example.projectmanagement.datageneral.domain.entity.meeting.MeetingEntity
import com.example.projectmanagement.datageneral.domain.entity.meeting.asEntity
import com.example.projectmanagement.datageneral.domain.usecase.meeting.exception.toMeetingFailure
import com.example.projectmanagement.datageneral.domain.utilities.mapFailure

class EditMeetingNoteUseCase(
    private val repository: MeetingRepository
) {

    suspend operator fun invoke(meeting: MeetingEntity, notes: String): Result<MeetingEntity> {
        meeting.notes = notes
        return runCatching {
            repository.updateMeeting(meeting.id, meeting.projectId, meeting.asUpdateMap()).asEntity()
        }.mapFailure { it.toMeetingFailure() }
    }
}
