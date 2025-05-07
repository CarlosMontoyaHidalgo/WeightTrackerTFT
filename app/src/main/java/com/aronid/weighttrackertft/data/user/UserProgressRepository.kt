package com.aronid.weighttrackertft.data.user

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val progressCollection = firestore.collection(FirestoreCollections.USER_PROGRESS)

    // Guardar un registro de progreso (peso) para un usuario
    suspend fun saveProgress(progress: UserProgressModel): Result<Unit> {
        return try {
            progressCollection.add(progress.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error saving progress: ${e.localizedMessage}", e))
        }
    }

    // Obtener registros de progreso en un rango de fechas
    suspend fun getProgressInDateRange(start: Timestamp, end: Timestamp, userId: String): Result<List<UserProgressModel>> {
        return try {
            val querySnapshot = progressCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("timestamp", start)
                .whereLessThanOrEqualTo("timestamp", end)
                .get()
                .await()

            // Verifica los documentos que vienen de la consulta
            querySnapshot.documents.forEach { document ->
                Log.d("Firestore111", "Document: ${document.data}")
            }

            val entries = querySnapshot.documents.mapNotNull { it.toObject(UserProgressModel::class.java) }
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    fun getCurrentUser(): FirebaseUser {
        return auth.currentUser ?: throw IllegalStateException("User Not Available")
    }
}