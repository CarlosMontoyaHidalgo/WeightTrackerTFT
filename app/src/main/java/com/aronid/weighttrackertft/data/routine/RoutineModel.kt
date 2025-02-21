package com.aronid.weighttrackertft.data.routine

import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class RoutineModel(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("goal") val goal: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("exercises") val exercises: List<DocumentReference> = emptyList(),
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("userId") val userId: String = "",
) {
    init {
        /*
        require(name.isNotBlank()) { "Name can't be blank" }
        require(goal.isNotBlank()) { "Goal can't be blank" }
        require(description.isNotBlank()) { "Description can't be blank" }
        require(userId.isNotBlank()) { "User ID can't be blank" }
    */
    }
}

data class RoutineDay(
    @PropertyName("day_name")
    val dayName: String,

    )

data class ExerciseItem(
    val name: String = "",  // Nombre del ejercicio
    val sets: Int = 3,
    val reps: Int = 12,
    val notes: String = ""
)

data class RoutineItem(
    @PropertyName("exercise_id")
    val exerciseId: String,
    @PropertyName("sets")
    val sets: Int = 3,
    @PropertyName("reps")
    val reps: Int = 12,
    @PropertyName("notes")
    val notes: String = ""
)