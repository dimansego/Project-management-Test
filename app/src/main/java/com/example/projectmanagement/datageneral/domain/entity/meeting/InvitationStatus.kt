package com.example.projectmanagement.datageneral.domain.entity.meeting

enum class InvitationStatus(val value: String) {
    INVITED("invited"),
    ACCEPTED("accepted"),
    DECLINED("declined"),
    DID_NOT_RESPOND("did not respond");

    companion object {
        fun fromValue(value: String): InvitationStatus? =
            entries.firstOrNull { it.value == value }
    }
}