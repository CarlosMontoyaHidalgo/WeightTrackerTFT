package com.aronid.weighttrackertft.data.exercises

object ExerciseUtils {
    fun getMuscleIdsFromExercises(exercises: List<ExerciseModel>): Set<String> {
        val muscleIds = mutableSetOf<String>()
        exercises.forEach { exercise ->
            exercise.primaryMuscle?.let { muscleIds.add(it.id) }
            exercise.secondaryMuscle.forEach { muscleIds.add(it.id) }
        }
        return muscleIds
    }
}
