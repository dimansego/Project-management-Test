package com.example.projectmanagement.datageneral.domain.entity.meeting

@Deprecated("No longer have semantic meaning")
enum class AttachmentType(val value: String) {
    AGENDA("agenda"),
    MINUTES("minutes"),
    SLIDES("slides"),
    REFERENCE("reference"),
    TASK_RELATED("task_related"),
    SUMMARY("summary");

    companion object {
        fun fromValue(value: String): AttachmentType? =
            entries.firstOrNull { it.value == value }
    }
}