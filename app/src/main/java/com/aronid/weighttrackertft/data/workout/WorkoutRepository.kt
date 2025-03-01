package com.aronid.weighttrackertft.data.workout

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private val workoutCollection = firestore.collection(FirestoreCollections.WORKOUT)

    suspend fun saveWorkout(workout: WorkoutModel): DocumentReference{
        return try {
            workoutCollection.add(workout).await()
        } catch (e: Exception) {
            throw Exception("Error while saving workout: ${e.message}", e)
        }
    }

    suspend fun getWorkoutsByRoutine(routineId: String): List<WorkoutModel> {
        return workoutCollection
            .whereEqualTo("routineId", routineId)
            .get()
            .await()
            .toObjects(WorkoutModel::class.java)
    }

}