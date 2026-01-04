package com.example.projectmanagement.datageneral.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    /**
     * Format a millisecond timestamp (as String) to "MMM dd, yyyy" format
     */
    fun formatDeadline(deadlineString: String): String {
        if (deadlineString.isEmpty()) return ""
        return try {
            val timestamp = deadlineString.toLong()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = timestamp
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            deadlineString // Return original if parsing fails
        }
    }
    
    /**
     * Check if a deadline timestamp (as String) matches the current system date
     */
    fun isDeadlineToday(deadlineString: String): Boolean {
        if (deadlineString.isEmpty()) return false
        return try {
            val timestamp = deadlineString.toLong()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = timestamp
            
            val today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            
            val deadlineDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            deadlineDate.timeInMillis = timestamp
            deadlineDate.set(Calendar.HOUR_OF_DAY, 0)
            deadlineDate.set(Calendar.MINUTE, 0)
            deadlineDate.set(Calendar.SECOND, 0)
            deadlineDate.set(Calendar.MILLISECOND, 0)
            
            deadlineDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            deadlineDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            false
        }
    }
}

