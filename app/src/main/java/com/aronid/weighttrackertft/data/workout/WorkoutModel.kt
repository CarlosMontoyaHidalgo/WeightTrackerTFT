package com.aronid.weighttrackertft.data.workout

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class WorkoutModel(
    @DocumentId val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("date") val date: Timestamp? = null,
    @PropertyName("exercises") val exercises: List<ExerciseWithSeries> = emptyList(),
    @PropertyName("calories") val calories: Int = 0,
    @PropertyName("volume") val volume: Int = 0,
    @PropertyName("intensity") val intensity: Double = 0.0,
    @PropertyName("workoutType") val workoutType: String = "",
    @PropertyName("primaryMuscleIds") val primaryMuscleIds: List<String> = emptyList(),
    @PropertyName("secondaryMuscleIds") val secondaryMuscleIds: List<String> = emptyList(),
    @PropertyName("duration") val duration: Long = 0L,
    @PropertyName("isFavorite") val isFavorite: Boolean = false,
)

data class ExerciseWithSeries(
    @PropertyName("exerciseName") val exerciseName: String? = "",
    @PropertyName("goal") val goal: String? = "",
    @PropertyName("description") val description: String? = "",
    @PropertyName("imageUrl") val imageUrl: String? = "",
    @PropertyName("series") val series: List<SeriesItem> = emptyList(),
    @PropertyName("requiresWeight") val requiresWeight: Boolean = false,
    @PropertyName("primaryMuscleRef") val primaryMuscleRef: DocumentReference? = null,
    @PropertyName("secondaryMusclesRef") val secondaryMusclesRef: List<DocumentReference?> = emptyList(),

    )

data class SeriesItem(
    @PropertyName("setNumber") val setNumber: Int = 1,
    @PropertyName("weight") val weight: String? = "",
    @PropertyName("reps") val reps: String? = "",
    @PropertyName("isDone") val isDone: Boolean = false,
)