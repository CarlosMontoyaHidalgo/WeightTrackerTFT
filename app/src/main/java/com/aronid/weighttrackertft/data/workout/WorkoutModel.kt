package com.aronid.weighttrackertft.data.workout

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class WorkoutModel(
    @DocumentId val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("date") val date: Timestamp? = null,
    @PropertyName("exercises") val exercises: List<ExerciseWithSeries> = emptyList(),
    @PropertyName("calories") val calories: Int = 0,
    @PropertyName("volume") val volume: Int = 0,
    @PropertyName("intensity") val intensity: Double = 0.0,
    @PropertyName("workoutType") val workoutType: String = ""
) {
//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "userId" to userId,
//            "date" to date,
//            "calories" to calories,
//            "exercises" to exercises.map { it.toMap() }
//        )
//    }
}

data class ExerciseWithSeries(
    @PropertyName("exerciseName") val exerciseName: String? = "",
    @PropertyName("goal") val goal: String? = "",
    @PropertyName("description") val description: String? = "",
    @PropertyName("imageUrl") val imageUrl: String? = "",
    @PropertyName("series") val series: List<SeriesItem> = emptyList(),
    @PropertyName("requiresWeight") val requiresWeight: Boolean = false,

    ){
//    fun toMap(): Map<String, Any?>{
//        return mapOf(
//            "exerciseName" to exerciseName,
//            "series" to series
//        )
//    }
}

data class SeriesItem(
    @PropertyName("setNumber") val setNumber: Int = 1,
    @PropertyName("weight") val weight: String? = "",
    @PropertyName("reps") val reps: String? = "",
    @PropertyName("isDone") val isDone: Boolean = false,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "setNumber" to setNumber,
            "weight" to weight,
            "reps" to reps?.toIntOrNull(),
        )
    }
}