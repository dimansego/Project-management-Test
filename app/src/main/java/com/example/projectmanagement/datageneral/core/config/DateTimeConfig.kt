package com.example.projectmanagement.datageneral.core.config

import java.time.OffsetDateTime
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object DateTimeConfig {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX")

    fun toInstant(date: String): Instant {
        return OffsetDateTime.parse(date, formatter).toInstant()
    }

    fun toDuration(duration: String): Duration {
        val parts = duration.lowercase()

        return when {
            "day" in parts -> {
                val days = Regex("(\\d+)\\s*days?")
                    .find(parts)?.groupValues?.get(1)?.toLongOrNull() ?: 0
                days.days
            }

            Regex("\\d{2}:\\d{2}:\\d{2}").matches(parts) -> {
                val (h, m, s) = parts.split(":").map { it.toLong() }
                h.hours + m.minutes + s.seconds
            }

            else -> Duration.ZERO
        }
    }

    fun fromDuration(dur: Duration): String {
        val totalSeconds = dur.inWholeSeconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    fun fromInstant(instant: Instant): String {
        return formatter.format(instant.atOffset(ZoneOffset.UTC))
    }

}
