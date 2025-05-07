package com.aronid.weighttrackertft.data.goals

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userCollection = firestore.collection(FirestoreCollections.USERS)

    suspend fun getGoalCalories(userId: String): Result<Double?> {
        return try {
            val document = userCollection.document(userId).get().await()
            val goalCalories = document.getDouble("goalCalories")
            Result.success(goalCalories)
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "Error al obtener objetivo de calorías: ${e.localizedMessage}",
                    e
                )
            )
        }
    }

    suspend fun updateGoalCalories(userId: String, goalCalories: Double): Result<Unit> {
        return try {
            userCollection.document(userId).update("goalCalories", goalCalories).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "Error al actualizar objetivo de calorías: ${e.localizedMessage}",
                    e
                )
            )
        }
    }
}

// Puedes añadir más métodos para otros objetivos como volumen si lo necesitas
