package com.aronid.weighttrackertft.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        parsedDate?.let { outputFormat.format(it) } ?: date
    } catch (e: Exception) {
        date
    }
}

fun isValidDateFormat(date: String): Boolean {
    val regex = Regex("^\\d{1,2}-\\d{1,2}-\\d{4}$")
    return date.matches(regex)
}