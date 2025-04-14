package com.aronid.weighttrackertft.data.weight

import com.google.firebase.Timestamp

data class WeightHistoryModel(
    val date: Timestamp,
    val weight: Double
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "date" to date,
            "weight" to weight
        )
    }
}