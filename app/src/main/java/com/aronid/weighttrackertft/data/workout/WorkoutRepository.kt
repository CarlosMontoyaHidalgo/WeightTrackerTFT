package com.aronid.weighttrackertft.data.workout

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.utils.getDateRange
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun getAllWorkouts(): List<WorkoutModel> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            Log.d("WorkoutRepository", "Fetching all workouts for user ID: $userId")
            val snapshot = workoutCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val workouts = snapshot.toObjects(WorkoutModel::class.java)
            Log.d("WorkoutRepository", "All workouts fetched: $workouts")

            workouts
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error fetching all workouts", e)
            emptyList()
        }
    }

    suspend fun deleteWorkout(workoutId: String) {
        try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            Log.d("WorkoutRepository", "Deleting workout with ID: $workoutId for user ID: $userId")
            workoutCollection
                .document(workoutId)
                .delete()
                .await()
            Log.d("WorkoutRepository", "Workout deleted successfully")
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error deleting workout", e)
        }
    }

    suspend fun deleteWorkouts(workoutIds: List<String>) {
        try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")

            Log.d("WorkoutRepository", "Deleting workouts with IDs: $workoutIds for user ID: $userId")

            val batch = workoutCollection.firestore.batch()

            workoutIds.forEach { workoutId ->
                val docRef = workoutCollection.document(workoutId)
                batch.delete(docRef)
            }

            batch.commit().await()

            Log.d("WorkoutRepository", "Workouts deleted successfully")
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error deleting workouts", e)
        }
    }

    fun getWorkoutsPagingSource(userId: String): WorkoutPagingSource {
        Log.d("WorkoutRepository", "Creating PagingSource for userId: $userId")
        val baseQuery = workoutCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)

        return WorkoutPagingSource(baseQuery)
    }

    fun getWorkoutsPagingSource(
        userId: String,
        startDate: Timestamp? = null,
        endDate: Timestamp? = null
    ): WorkoutPagingSource {
        Log.d("WorkoutRepository", "Creating PagingSource for userId: $userId, startDate: $startDate, endDate: $endDate")
        var baseQuery = workoutCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
        return WorkoutPagingSource(baseQuery, startDate, endDate)
    }

    suspend fun getCaloriesForPeriod(period: String, referenceDate: Timestamp? = null): Int {
        val (startDate, endDate) = getDateRange(period, referenceDate)
        val workouts = getWorkoutsInDateRange(startDate, endDate)
        return workouts.sumOf { it.calories }
    }


//    suspend fun getWorkoutsByRoutine(routineId: String): List<WorkoutModel> {
//        return workoutCollection
//            .whereEqualTo("routineId", routineId)
//            .get()
//            .await()
//            .toObjects(WorkoutModel::class.java)
//    }

    }