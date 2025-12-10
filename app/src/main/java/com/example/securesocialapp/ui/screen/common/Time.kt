package com.example.securesocialapp.ui.screen.common

import android.text.format.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatConciseTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val zone = ZoneId.systemDefault()
    val dateTime = LocalDateTime.ofInstant(instant, zone)
    val now = LocalDateTime.now(zone)

    val duration = Duration.between(dateTime, now)
    val seconds = duration.seconds
    val minutes = duration.toMinutes()
    val hours = duration.toHours()

    return when {
        // Just now (< 1 min)
        seconds < 60 -> "${seconds} sec"

        // Minutes (< 1 hour)
        minutes < 60 -> "${minutes} min"

        // Hours (< 24 hours)
        hours < 24 -> "${hours} hr"

        // Yesterday (Check strictly against calendar date)
        dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1)) -> "Yesterday"

        // This Year (25 Nov)
        dateTime.year == now.year -> DateTimeFormatter.ofPattern("dd MMM").format(dateTime)

        // Previous Years (25 Nov 2024)
        else -> DateTimeFormatter.ofPattern("dd MMM yyyy").format(dateTime)
    }
}