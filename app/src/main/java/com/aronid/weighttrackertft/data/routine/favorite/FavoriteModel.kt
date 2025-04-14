package com.aronid.weighttrackertft.data.routine.favorite


data class FavoriteRoutine(
    val routineId: String,
    val name: String,
    val isPredefined: Boolean,
    val timestamp: Long
)
