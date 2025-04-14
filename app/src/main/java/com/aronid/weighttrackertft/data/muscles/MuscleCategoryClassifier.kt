package com.aronid.weighttrackertft.data.muscles

import com.google.firebase.firestore.DocumentReference

object MuscleCategoryResolver {
    private val muscleCategories = mapOf(
        "chest" to "Empuje",
        "triceps" to "Empuje",
        "shoulders" to "Empuje",
        "back" to "Tirón",
        "biceps" to "Tirón",
        "quadriceps" to "Piernas",
        "hamstrings" to "Piernas",
        "glutes" to "Piernas",
        "abs" to "Core",
        "obliques" to "Core",
        "lower_back" to "Core"
    )

//    fun getCategoryFromReference(ref: DocumentReference?): String {
//        val muscleId = ref?.path?.split("/")?.lastOrNull() ?: return "Otro"
//        return muscleCategories[muscleId] ?: "Otro"
//    }
    fun getCategoryFromReference(muscleRef: DocumentReference?): String {
        return when (muscleRef) {
            is DocumentReference -> {
                val muscleId = muscleRef.path.split("/").lastOrNull() ?: return "Otro"
                muscleCategories[muscleId] ?: "Otro"
            }
            is String -> {
                muscleCategories[muscleRef.lowercase()] ?: "Otro"
            }
            else -> "Otro"
        }
    }
}