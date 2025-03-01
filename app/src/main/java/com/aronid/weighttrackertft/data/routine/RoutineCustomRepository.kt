package com.aronid.weighttrackertft.data.routine

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoutineCustomRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val routinesCollection = firestore.collection(FirestoreCollections.ROUTINES_CUSTOM)
    private val exercisesCollection = firestore.collection(FirestoreCollections.EXERCISES)

    // CRUD Básico
    suspend fun createRoutine(routine: RoutineModel): String {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

        val newRoutine = routine.copy(
            userId = userId,
            createdAt = System.currentTimeMillis(),
            exercises = routine.exercises.ifEmpty { emptyList() }
        )
        println("Routine to save: $newRoutine") // Depuración
        return try {
            val docRef = routinesCollection.add(newRoutine).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Error al crear rutina: ${e.message}")
        }
    }

    suspend fun getUserRoutines(): List<RoutineModel> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = routinesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RoutineModel::class.java)?.copy(
                    id = doc.id,
                    exercises = doc.get("exercises") as? List<DocumentReference> ?: emptyList()
                )
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error getting routines: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    suspend fun getRoutineById(routineId: String): RoutineModel? {
        return try {
            val snapshot = routinesCollection.document(routineId).get().await()
            val routine = snapshot.toObject(RoutineModel::class.java)
            routine
        } catch (e: Exception) {
            null
        }
    }

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

    suspend fun updateRoutine(routine: RoutineModel, exerciseIds: List<String>) {
        val exerciseRefs = exerciseIds.map { id ->
            exercisesCollection.document(id)
        }
        val updatedRoutineWithRefs = routine.copy(exercises = exerciseRefs)
        routinesCollection.document(routine.id).set(updatedRoutineWithRefs).await()
    }

    suspend fun getExercisesByRoutineId(routineId: String): List<ExerciseModel> {
        val routineSnapshot = routinesCollection.document(routineId).get().await()
        val routine = routineSnapshot.toObject(RoutineModel::class.java)

        return routine?.exercises?.mapNotNull { exerciseRef ->
            exerciseRef.get().await().toObject(ExerciseModel::class.java)
        } ?: emptyList()
    }

    // Nuevo método para obtener todos los ejercicios disponibles
    suspend fun getAllExercises(): List<ExerciseModel> {
        return try {
            val snapshot = exercisesCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ExerciseModel::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching exercises: ${e.message}")
            emptyList()
        }
    }

    // Método para obtener una referencia a un ejercicio por su ID
    fun getExerciseReference(exerciseId: String): DocumentReference {
        return exercisesCollection.document(exerciseId)
    }
}