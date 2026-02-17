package com.us4ever.viderapro

import kotlin.math.pow
import kotlin.math.log

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        "${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log(size.toDouble(), 1024.0)).toInt()
    val value = size / 1024.0.pow(digitGroups.toDouble())
    val roundedValue = (value * 10).toInt() / 10.0
    return "$roundedValue ${units[digitGroups]}"
}

// Simple date formatter without external dependency for now
fun formatDate(seconds: Long): String {
    // This is a placeholder as proper date formatting is platform specific or needs a library
    // For now, let's just return a string representation or use an expect/actual
    return getPlatformDateString(seconds)
}

expect fun getPlatformDateString(seconds: Long): String

expect fun copyToClipboard(text: String?)
