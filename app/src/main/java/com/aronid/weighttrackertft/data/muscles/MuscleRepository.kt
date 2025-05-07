package com.aronid.weighttrackertft.data.muscles

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseUtils.getMuscleIdsFromExercises
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MuscleRepository @Inject constructor(
    firestore: FirebaseFirestore
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

    suspend fun fetchMuscleById(muscleId: String): MuscleModel? {
        return try {
            val snapshot = muscleCollection.document(muscleId).get().await()
            snapshot.toObject(MuscleModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchMusclesFromIds(muscleIds: Set<String>): List<MuscleModel> {
        val muscles = mutableListOf<MuscleModel>()
        muscleIds.forEach { id ->
            val doc = muscleCollection.document(id).get().await()
            doc.toObject(MuscleModel::class.java)?.let { muscles.add(it) }
        }
        return muscles
    }

    suspend fun getWorkedMuscles(exercises: List<ExerciseModel>): List<MuscleModel> {
        val muscleIds = getMuscleIdsFromExercises(exercises)
        return fetchMusclesFromIds(muscleIds)
    }


//    suspend fun addMuscle(muscle: MuscleModel): Result<String> {
//        return try {
//            muscleCollection.document(muscle.id).set(muscle).await()
//            Result.success("Muscle added successfully")
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun getAllMuscles(): Result<List<MuscleModel>> {
//        return try {
//            val muscles = muscleCollection.get().await()
//            val muscleList = muscles.documents.mapNotNull { it.toObject<MuscleModel>() }
//            Result.success(muscleList)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun getMuscleById(id: String): Result<MuscleModel?> {
//        return try {
//            val muscleDoc = muscleCollection.document(id).get().await()
//            val muscle = muscleDoc.toObject<MuscleModel>()
//            Result.success(muscle)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun updateMuscle(muscle: MuscleModel): Result<String> {
//        return try {
//            muscleCollection.document(muscle.id).set(muscle).await()
//            Result.success("Muscle updated successfully")
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun deleteMuscle(id: String): Result<String> {
//        return try {
//            muscleCollection.document(id).delete().await()
//            Result.success("Muscle deleted successfully")
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

}