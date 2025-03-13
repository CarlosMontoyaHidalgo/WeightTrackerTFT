package com.aronid.weighttrackertft.data.user

import com.aronid.weighttrackertft.data.weight.WeightHistoryModel
import com.google.firebase.firestore.DocumentReference

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val birthdate: String = "",
    val profileImageUrl: String? = null,
    val height: Int? = null,
    val weight: Double? = null,
    val bodyFatPercentage: Double? = null,
    val gender: String? = null,
    val activityLevel: String? = null,
    val goal: String? = null,
    val hasCompletedQuestionnaire: Boolean = false,
    val savedRoutines: List<DocumentReference> = emptyList(),
    val savedExercises: List<DocumentReference> = emptyList(),
    val savedWorkouts: List<DocumentReference> = emptyList(),
    val initialWeight: Double? = null,
    val notificationFrequency: String? = "WEEKLY",
    val weightsHistory: List<WeightHistoryModel> = emptyList(),

    ) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "birthdate" to birthdate,
            "profileImageUrl" to profileImageUrl,
            "height" to height,
            "weight" to weight,
            "gender" to gender,
            "activityLevel" to activityLevel,
            "goal" to goal,
            "hasCompletedQuestionnaire" to hasCompletedQuestionnaire
        )
    }
}