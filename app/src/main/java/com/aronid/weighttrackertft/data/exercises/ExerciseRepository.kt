package com.aronid.weighttrackertft.data.exercises

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ExerciseRepository.kt
 *
 * Este repositorio gestiona la interacción con Firestore para la colección de ejercicios.
 * Proporciona métodos para realizar operaciones CRUD, paginación, búsqueda avanzada
 * y filtrado de ejercicios por categoría.
 *
 * Métodos principales:
 * - createExercise: Agrega un nuevo ejercicio a Firestore.
 * - getAllExercises: Obtiene todos los ejercicios ordenados por nombre.
 * - getExerciseById: Recupera un ejercicio específico por su ID.
 * - updateExercise: Actualiza los datos de un ejercicio existente.
 * - deleteExercise: Elimina un ejercicio por su ID.
 * - getExercisesPaginated: Obtiene una lista paginada de ejercicios con opción de filtrar por categoría.
 * - searchExercisesAdvanced: Realiza una búsqueda avanzada considerando nombre, categoría y músculos primarios.
 * - getExercisesByCategory: Filtra ejercicios por su categoría.
 *
 */

class ExerciseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val exerciseCollection = firestore.collection(FirestoreCollections.EXERCISES)


    // CREATE
    suspend fun createExercise(exercise: ExerciseModel) {
        exerciseCollection.add(exercise).await()
    }

    // READ ALL (sin parámetros)
    suspend fun getAllExercises(): List<ExerciseModel> {
        return try {
            val snapshot = exerciseCollection
                .get()
                .await()

            Log.d("ExerciseViewModel", "Fetched ${snapshot.documents.size} documents")

            snapshot
                .documents
                .mapNotNull { doc ->
                    doc.toObject<ExerciseModel>()?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            Log.d("ExerciseViewModel", "Error fetching exercises: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchMuscleName(ref: DocumentReference?): String {
        return try {
            ref?.get()?.await()?.getString("name") ?: "Unknown"
        } catch (e: Exception) {
            Log.e("ExerciseCard", "Error fetching muscle name", e)
            "Error"
        }
    }

    // READ BY ID
    suspend fun getExerciseById(id: String): ExerciseModel? {
        return try {
            val snapshot = exerciseCollection.document(id)
                .get()
                .await()

            snapshot
                .toObject(ExerciseModel::class.java)
                ?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMuscleByExerciseId(id: String): String {
        return try {
            val snapshot = exerciseCollection.document(id)
                .get()
                .await()

            val muscleRef = snapshot.get("primary_muscles") as DocumentReference
            fetchMuscleName(muscleRef)
        } catch (e: Exception) {
            Log.e("ExerciseDetailsViewModel", "Error fetching muscle name", e)
            "Error"
        }
    }

    // UPDATE
    suspend fun updateExercise(exercise: ExerciseModel) {
        exerciseCollection.document(exercise.id).set(exercise).await()
    }

    // DELETE
    suspend fun deleteExercise(id: String) {
        exerciseCollection.document(id).delete().await()
    }

    // BÚSQUEDA AVANZADA CON CATEGORÍA
    suspend fun searchExercisesAdvanced(query: String): List<ExerciseModel> {
        return try {
            val normalizedQuery = query.lowercase()

            // Búsqueda por nombre
            val nameQuery = exerciseCollection
                .orderBy("name")
                .startAt(normalizedQuery)
                .endAt("$normalizedQuery\uf8ff")
                .get()
                .await()

            // Búsqueda por categoría
            val categoryQuery = exerciseCollection
                .whereEqualTo("category", normalizedQuery)
                .get()
                .await()

            // Búsqueda por músculos
            val muscleQuery = exerciseCollection
                .whereArrayContainsAny("primary_muscles", listOf(normalizedQuery))
                .get()
                .await()

            // Combinar resultados y eliminar duplicados
            (nameQuery.documents + categoryQuery.documents + muscleQuery.documents)
                .distinctBy { it.id }
                .mapNotNull { doc ->
                    doc.toObject(ExerciseModel::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // OBTENER EJERCICIOS POR CATEGORÍA
    suspend fun getExercisesByCategory(category: String): List<ExerciseModel> {
        return try {
            exerciseCollection
                .whereEqualTo("category", category)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(ExerciseModel::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMETExercise(exerciseName: String): Float {
        return try {
            exerciseCollection.whereEqualTo("name", exerciseName.lowercase()).get()
                .await().documents.firstOrNull()?.getDouble("met")?.toFloat() ?: 4.0f
        } catch (e: Exception) {
            4.0f
        }
    }
}