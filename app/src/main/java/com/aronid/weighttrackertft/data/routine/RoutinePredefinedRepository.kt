package com.aronid.weighttrackertft.data.routine

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class RoutinePredefinedRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val predefinedRoutinesCollection = firestore.collection(FirestoreCollections.ROUTINES_PREDEFINED)

    suspend fun getPredefinedRoutines(): List<RoutineModel> {
        return try {
            val snapshot = predefinedRoutinesCollection.get().await()
            val routines = snapshot.documents.mapNotNull { doc ->
                val routine = doc.toObject(RoutineModel::class.java)?.copy(id = doc.id)
                routine
            }
            routines
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRoutineById(id: String): RoutineModel? {
        return try {
            val snapshot = predefinedRoutinesCollection.document(id).get().await()
            snapshot
                .toObject(RoutineModel::class.java)?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

//    suspend fun getExercisesByRoutineId(routineId: String): List<ExerciseModel> {
//        val routineSnapshot = predefinedRoutinesCollection.document(routineId).get().await()
//        val routine = routineSnapshot.toObject(RoutineModel::class.java)
//
//        return routine?.exercises?.mapNotNull { exerciseRef ->
//            exerciseRef.get().await().toObject(ExerciseModel::class.java)
//        } ?: emptyList()
//    }

    suspend fun getExercisesForRoutine(routineId: String): List<ExerciseModel> {
        val routine = getRoutineById(routineId)
        val exerciseRefs = routine?.exercises ?: return emptyList()
        return exerciseRefs.mapNotNull { ref ->
            try {
                ref.get().await().toObject(ExerciseModel::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}