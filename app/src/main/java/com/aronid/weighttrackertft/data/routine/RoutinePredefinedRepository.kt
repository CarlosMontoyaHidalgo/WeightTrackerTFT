package com.aronid.weighttrackertft.data.routine

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class RoutinePredefinedRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val routinesCollection = firestore.collection(FirestoreCollections.ROUTINES_PREDEFINED)

    suspend fun getPredefinedRoutines(): List<RoutineModel> {
        return try {
            val snapshot = routinesCollection.get().await()
            Log.d("Repository", "Fetched ${snapshot.documents.size} documents")

            snapshot.documents.forEach { doc ->
                Log.d("Repository", "Document ID: ${doc.id}, Data: ${doc.data}")
            }

            val routines = snapshot.documents.mapNotNull { doc ->
                val routine = doc.toObject(RoutineModel::class.java)?.copy(id = doc.id)
                Log.d("Repository", "Mapped Routine: $routine")
                routine
            }

            Log.d("Repository", "Total Routines Mapped: ${routines.size}")
            routines
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching routines: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRoutineById(id: String): RoutineModel? {
        return try {
            val snapshot = routinesCollection.document(id).get().await()
            snapshot
                .toObject(RoutineModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getExercisesByRoutineId(routineId: String): List<ExerciseModel> {
        val routineSnapshot = routinesCollection.document(routineId).get().await()
        val routine = routineSnapshot.toObject(RoutineModel::class.java)

        return routine?.exercises?.mapNotNull { exerciseRef ->
            exerciseRef.get().await().toObject(ExerciseModel::class.java)
        } ?: emptyList()
    }
}