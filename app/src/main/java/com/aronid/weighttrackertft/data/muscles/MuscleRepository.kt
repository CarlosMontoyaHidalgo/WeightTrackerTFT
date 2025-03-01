package com.aronid.weighttrackertft.data.muscles

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MuscleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val muscleCollection = firestore.collection(FirestoreCollections.MUSCLES)

    suspend fun fetchMuscleName(ref: DocumentReference?): String {
        return try {
            val muscleDoc = ref?.get()?.await()
            val name = muscleDoc?.getString("name") ?: "Unknown"
            name
        } catch (e: Exception) {
            "Error"
        }
    }

    suspend fun addMuscle(muscle: MuscleModel): Result<String> {
        return try {
            muscleCollection.document(muscle.id).set(muscle).await()
            Result.success("Muscle added successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMuscles(): Result<List<MuscleModel>> {
        return try {
            val muscles = muscleCollection.get().await()
            val muscleList = muscles.documents.mapNotNull { it.toObject<MuscleModel>() }
            Result.success(muscleList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMuscleById(id: String): Result<MuscleModel?> {
        return try {
            val muscleDoc = muscleCollection.document(id).get().await()
            val muscle = muscleDoc.toObject<MuscleModel>()
            Result.success(muscle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMuscle(muscle: MuscleModel): Result<String> {
        return try {
            muscleCollection.document(muscle.id).set(muscle).await()
            Result.success("Muscle updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMuscle(id: String): Result<String> {
        return try {
            muscleCollection.document(id).delete().await()
            Result.success("Muscle deleted successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}