package com.example.projectmanagement.datageneral.domain.entity.meeting

import com.example.projectmanagement.datageneral.core.config.DateTimeConfig
import com.example.projectmanagement.datageneral.data.model.meeting.MeetingInvitation
import com.example.projectmanagement.datageneral.domain.utilities.Updatable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

class MeetingInvitationEntity(
    val meetingId: String,
    val userId: String,
    val projectId: String,
    initialStatus: InvitationStatus,
    initialInvitedAt: Instant?,
    initialRespondedAt: Instant?
) : Updatable {

    override val modifiedFields = mutableSetOf<String>()

    var status: InvitationStatus = initialStatus
        private set

    var invitedAt: Instant = initialInvitedAt ?: Instant.now()
        private set

    var respondedAt: Instant? = initialRespondedAt
        private set

    // ------------------------------------------------------------
    // Business-driven update methods (recommended over setters)
    // ------------------------------------------------------------

    /** Accept an invitation */
    fun accept() {
        require(status != InvitationStatus.ACCEPTED) { "Invitation already accepted." }
        require(status != InvitationStatus.DECLINED) { "Cannot accept a declined invitation." }

        status = InvitationStatus.ACCEPTED
        respondedAt = Instant.now()

        modifiedFields += MeetingInvitation.STATUS
        modifiedFields += MeetingInvitation.RESPONDED_AT
    }

    /** Decline an invitation */
    fun decline() {
        require(status != InvitationStatus.DECLINED) { "Invitation already declined." }
        require(status != InvitationStatus.ACCEPTED) { "Cannot decline an accepted invitation." }

        status = InvitationStatus.DECLINED
        respondedAt = Instant.now()

        modifiedFields += MeetingInvitation.STATUS
        modifiedFields += MeetingInvitation.RESPONDED_AT
    }

    /** Allow meeting creators to resend / reinvite */
    fun reinvite() {
        // Reset to "invited"
        status = InvitationStatus.INVITED
        invitedAt = Instant.now()
        respondedAt = null

        modifiedFields += MeetingInvitation.STATUS
        modifiedFields += MeetingInvitation.INVITED_AT
        modifiedFields += MeetingInvitation.RESPONDED_AT
    }

    // ------------------------------------------------------------
    // JSON Update Map (PATCH)
    // ------------------------------------------------------------
    override fun asUpdateMap(): JsonObject =
        buildJsonObject {
            for (col in modifiedFields) {
                when (col) {
                    MeetingInvitation.STATUS -> put(MeetingInvitation.STATUS, JsonPrimitive(status.value))
                    MeetingInvitation.INVITED_AT -> put(MeetingInvitation.INVITED_AT, JsonPrimitive(DateTimeConfig.fromInstant(invitedAt)))
                    MeetingInvitation.RESPONDED_AT -> {
                        if (respondedAt != null) put(MeetingInvitation.RESPONDED_AT, JsonPrimitive(DateTimeConfig.fromInstant(respondedAt!!)))
                    }
                }
            }
        }
}

fun MeetingInvitation.asEntity(): MeetingInvitationEntity {
    return MeetingInvitationEntity(
        meetingId = meetingId,
        userId = userId,
        initialStatus = InvitationStatus.fromValue(requireNotNull(status) {"Invitation Status is null"})
            ?: error("Unknown Meeting Invitation status: $status"),
        initialInvitedAt = invitedAt?.let { DateTimeConfig.toInstant(it) },
        initialRespondedAt = respondedAt?.let { DateTimeConfig.toInstant(it) },
        projectId = projectId
    )
}

fun MeetingInvitationEntity.asDataModel(): MeetingInvitation {
    return MeetingInvitation(
        meetingId = meetingId,
        userId = userId,
        status = status.value,
        invitedAt = DateTimeConfig.fromInstant(invitedAt),
        respondedAt = respondedAt?.let { DateTimeConfig.fromInstant(it) },
        projectId = projectId
    )
}
