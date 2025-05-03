package com.aronid.weighttrackertft.data.workout

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.utils.getDateRange
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
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
            val workoutId = documentRef.id
            val workoutWithId = workout.copy(id = workoutId)
            workoutWithId
        } catch (e: Exception) {
            throw Exception("Error while saving workout: ${e.message}", e)
        }
    }

    suspend fun getWorkoutById(workoutId: String): WorkoutModel? {
        return try {
            val snapshot = workoutCollection
                .document(workoutId)
                .get()
                .await()
            val workout = snapshot.toObject(WorkoutModel::class.java)?.copy(id = snapshot.id)
            workout
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWorkoutsInDateRange(
        startDate: Timestamp,
        endDate: Timestamp
    ): List<WorkoutModel> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            val snapshot = workoutCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()
            val workouts = snapshot.toObjects(WorkoutModel::class.java)
            workouts
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllWorkouts(): List<WorkoutModel> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            val snapshot = workoutCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val workouts = snapshot.toObjects(WorkoutModel::class.java)
            workouts
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteWorkout(workoutId: String) {
        try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            workoutCollection
                .document(workoutId)
                .delete()
                .await()
        } catch (e: Exception) {
        }
    }

    suspend fun deleteWorkouts(workoutIds: List<String>) {
        try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado")
            val batch = workoutCollection.firestore.batch()
            workoutIds.forEach { workoutId ->
                val docRef = workoutCollection.document(workoutId)
                batch.delete(docRef)
            }
            batch.commit().await()
        } catch (e: Exception) {
        }
    }

    fun getWorkoutsPagingSource(userId: String): WorkoutPagingSource {
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

    suspend fun getWorkoutsByIds(userId: String, ids: List<String>): List<WorkoutModel> {
        val workouts = mutableListOf<WorkoutModel>()
        if (ids.isEmpty()) {
            return workouts
        }

        try {
            val tasks = ids.map { workoutId ->
                workoutCollection.document(workoutId).get()
            }
            val snapshots = tasks.map { it.await() }
            snapshots.forEach { snapshot ->
                if (snapshot.exists()) {
                    val workout =
                        snapshot.toObject(WorkoutModel::class.java)?.copy(id = snapshot.id)
                    if (workout != null && workout.userId == userId) {
                        workouts.add(workout)
                    }
                }
            }
        } catch (e: Exception) {
        }
        return workouts
    }
}