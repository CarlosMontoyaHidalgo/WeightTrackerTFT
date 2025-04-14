package com.aronid.weighttrackertft.data.routine

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.favorite.FavoriteRoutine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoutineCustomRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val routinesCollection = firestore.collection(FirestoreCollections.ROUTINES_CUSTOM)
    private val predefinedRoutinesCollection = firestore.collection(FirestoreCollections.ROUTINES_PREDEFINED)
    private val exercisesCollection = firestore.collection(FirestoreCollections.EXERCISES)

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

    suspend fun deleteRoutine(id: String) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val routineDoc = routinesCollection.document(id).get().await()
            val routine = routineDoc.toObject(RoutineModel::class.java)
            if (routine?.userId == userId) {
                routinesCollection.document(id).delete().await()
            } else {
                throw SecurityException("User $userId does not own routine $id")
            }
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun deleteRoutines(ids: List<String>) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val favoritesCollection = firestore.collection("users").document(userId).collection("favorites")

            firestore.runBatch { batch ->
                ids.forEach { id ->
                    // Eliminar la rutina
                    val routineRef = routinesCollection.document(id)
                    batch.delete(routineRef)
                    // Eliminar el favorito correspondiente
                    val favoriteRef = favoritesCollection.document(id)
                    batch.delete(favoriteRef) // Esto elimina el favorito si existe
                }
            }.await()
            println("Batch delete completed for ${ids.size} routines and their favorites")
        } catch (e: Exception) {
            Log.e("Repository", "Error deleting routines: ${e.message}")
            throw e
        }
    }
//    suspend fun deleteRoutines(ids: List<String>) {
//        try {
//            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
//            firestore.runBatch { batch ->
//                ids.forEach { id ->
//                    val ref = routinesCollection.document(id)
//                    batch.delete(ref)
//                }
//
////                ids.forEach { id ->
////                    val favoriteRef = favoritesCollection.document(id)
////                    batch.delete(favoriteRef) // Esto elimina el favorito si existe
////                }
//            }.await()
//            println("Batch delete completed for ${ids.size} routines")
//        } catch (e: Exception) {
//            throw e
//        }
//    }

//    suspend fun getExercisesByRoutineId(routineId: String): List<ExerciseModel> {
//        val routineSnapshot = routinesCollection.document(routineId).get().await()
//        val routine = routineSnapshot.toObject(RoutineModel::class.java)
//
//        return routine?.exercises?.mapNotNull { exerciseRef ->
//            exerciseRef.get().await().toObject(ExerciseModel::class.java)
//        } ?: emptyList()
//    }

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

    fun getExerciseReference(exerciseId: String): DocumentReference {
        return exercisesCollection.document(exerciseId)
    }

    suspend fun isFavorite(routineId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val favoritesRef = firestore.collection("users")
            .document(userId)
            .collection("favorites")
            .document(routineId)
        val snapshot = favoritesRef.get().await()
        return snapshot.exists()
    }

//    suspend fun toggleFavorite(routineId: String, isPredefined: Boolean) {
//        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
//        Log.d("Favorites", "Usuario autenticado: $userId")
//        val favoritesRef = firestore.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .document(routineId)
//
//        val snapshot = favoritesRef.get().await()
//        if (snapshot.exists()) {
//            Log.d("Favorites", "Eliminando favorito: $routineId")
//            favoritesRef.delete().await()
//        } else {
//            Log.d("Favorites", "Agregando favorito: $routineId")
//            val favoriteData = mapOf(
//                "routineId" to routineId,
//                "isPredefined" to isPredefined,
//                "timestamp" to System.currentTimeMillis()
//            )
//            favoritesRef.set(favoriteData).await()
//        }
//    }

    suspend fun toggleFavorite(routineId: String, isPredefined: Boolean) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        Log.d("Favorites", "Usuario autenticado: $userId")
        val favoritesRef = firestore.collection("users")
            .document(userId)
            .collection("favorites")
            .document(routineId)

        try {
            val snapshot = favoritesRef.get().await()
            if (snapshot.exists()) {
                Log.d("Favorites", "Eliminando favorito: $routineId")
                favoritesRef.delete().await()
                Log.d("Favorites", "Favorito eliminado exitosamente: $routineId")
            } else {
                Log.d("Favorites", "Agregando favorito: $routineId")
                val favoriteData = mapOf(
                    "routineId" to routineId,
                    "isPredefined" to isPredefined,
                    "timestamp" to System.currentTimeMillis()
                )
                favoritesRef.set(favoriteData).await()
                Log.d("Favorites", "Favorito agregado exitosamente: $routineId")
            }
            // Verificar desde el servidor
            val serverSnapshot = favoritesRef.get(Source.SERVER).await()
            Log.d("Favorites", "Estado en el servidor: ${serverSnapshot.data ?: "No existe en el servidor"}")
        } catch (e: Exception) {
            Log.e("Favorites", "Error en toggleFavorite: ${e.message}", e)
            throw e
        }
    }

    suspend fun getUserFavorites(): List<FavoriteRoutine> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val routineId = data["routineId"] as? String ?: return@mapNotNull null
                val isPredefined = data["isPredefined"] as? Boolean ?: false
                val timestamp = data["timestamp"] as? Long ?: 0L

                // Obtener el nombre de la rutina y verificar si existe
                val routineSnapshot = if (isPredefined) {
                    predefinedRoutinesCollection.document(routineId).get().await()
                } else {
                    routinesCollection.document(routineId).get().await()
                }

                // Solo incluir si la rutina existe
                if (routineSnapshot.exists()) {
                    val name = routineSnapshot.getString("name") ?: "Sin nombre"
                    FavoriteRoutine(routineId, name, isPredefined, timestamp)
                } else {
                    Log.d("Favorites", "Rutina eliminada encontrada en favoritos: $routineId")
                    null // Excluir esta entrada
                }
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching favorites: ${e.message}")
            emptyList()
        }
    }
}