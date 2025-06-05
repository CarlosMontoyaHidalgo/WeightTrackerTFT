package com.aronid.weighttrackertft.utils

import com.google.firebase.Timestamp
import java.util.Calendar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


fun String?.toTitleCase(): String {
    return this?.split(" ")?.joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    } ?: ""
}

fun getDateRange(period: String, referenceDate: Timestamp? = null): Pair<Timestamp, Timestamp> {
    val calendar = Calendar.getInstance()
    referenceDate?.let { calendar.time = it.toDate() }

    when (period.lowercase()) {
        "daily" -> {}
        "weekly" -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        "monthly" -> calendar.set(Calendar.DAY_OF_MONTH, 1)
        "yearly" -> {
            calendar.set(Calendar.MONTH, Calendar.JANUARY)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        else -> throw IllegalArgumentException("Invalid period: $period")
    }

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startDate = Timestamp(calendar.time)

    when (period.lowercase()) {
        "daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
        "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
        "monthly" -> calendar.add(Calendar.MONTH, 1)
        "yearly" -> calendar.add(Calendar.YEAR, 1)
    }

    val endDate = Timestamp(calendar.time)
    return startDate to endDate
}


fun Timestamp.formatSpanish(): String {
    val calendar = Calendar.getInstance()
    calendar.time = this.toDate()
    val formatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale("es", "ES"))
    return formatter.format(calendar.time).replaceFirstChar { it.uppercase() }
}

fun LocalDate.formatSpanish(): String {
    return "${this.dayOfMonth} de ${this.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("es", "ES"))} de ${this.year}"
}

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Timestamp.formatShort(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MMM/yy", Locale("es", "ES"))
    return this.toLocalDate().format(formatter)
}

fun Double.formatToSinglePrecision(): String {
    return String.format("%.1f", this)
}


fun Int.formatToSinglePrecision(): String {
    return String.format("%.1f", this.toDouble())
}


fun formatDuration(seconds: Long?): String {
    if (seconds == null || seconds <= 0) return "0s"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m ")
        append("${remainingSeconds}s")
    }.trim()
}