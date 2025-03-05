package com.aronid.weighttrackertft.data.workout

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val workoutCollection = firestore.collection(FirestoreCollections.WORKOUT)

    suspend fun saveWorkout(workout: WorkoutModel): WorkoutModel {
        return try {
            val documentRef = workoutCollection.add(workout).await()
            val workoutId = documentRef.id // Obtener el ID generado por Firebase
            val workoutWithId = workout.copy(id = workoutId) // Actualizar el WorkoutModel con el ID
            Log.d(
                "WorkoutRepository",
                "Workout saved successfully with ID: ${workoutWithId.id}, Calories: ${workoutWithId.calories}, Volume: ${workoutWithId.volume}"
            )
            workoutWithId // Devolver el WorkoutModel con el ID asignado
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error while saving workout: ${e.message}", e)
            throw Exception("Error while saving workout: ${e.message}", e)
        }
    }

    suspend fun getWorkoutById(workoutId: String): WorkoutModel? {
        return try {
            val snapshot = workoutCollection
                .document(workoutId)
                .get()
                .await()
            val workout = snapshot.toObject(WorkoutModel::class.java)
            if (workout != null) {
                Log.d("WorkoutRepository", "Workout fetched: $workoutId")
            } else {
                Log.w("WorkoutRepository", "No workout found for ID: $workoutId")
            }
            workout
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error fetching workout by ID: $workoutId", e)
            null
        }
    }

    suspend fun getWorkoutsInDateRange(startDate: Timestamp, endDate: Timestamp): List<WorkoutModel> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            Log.d("WorkoutRepository", "Fetching workouts in date range for user ID: $userId")
            val snapshot = workoutCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()
            val workouts = snapshot.toObjects(WorkoutModel::class.java)
            Log.d("WorkoutRepository", "Workouts fetched: $workouts")
            workouts
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error fetching workouts in date range", e)
            emptyList()
        }
    }

//    suspend fun getWorkoutsByRoutine(routineId: String): List<WorkoutModel> {
//        return workoutCollection
//            .whereEqualTo("routineId", routineId)
//            .get()
//            .await()
//            .toObjects(WorkoutModel::class.java)
//    }

    }