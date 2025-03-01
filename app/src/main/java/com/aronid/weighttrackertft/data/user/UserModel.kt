package com.aronid.weighttrackertft.data.user

import com.google.firebase.firestore.DocumentReference

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val birthdate: String? = null,
    val profileImageUrl: String? = null,
    val height: Int? = null,
    val weight: Double? = null,
    val bodyFatPercentage: Double? = null,
    val gender: String? = null,
    val activityLevel: String? = null,
    val goal: String? = null,
    val hasCompletedQuestionnaire: Boolean = false,
    val createdRoutines: List<DocumentReference> = emptyList(),

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