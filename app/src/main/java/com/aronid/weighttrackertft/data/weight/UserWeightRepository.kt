package com.aronid.weighttrackertft.data.weight

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWeightRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val weightCollection get() = firestore.collection("user_weight_entries")

    // Guardar nuevo registro de peso
    suspend fun saveWeightEntry(weight: Double, note: String = ""): Result<Unit> {
        return try {
            val entry = mapOf(
                "userId" to auth.currentUser?.uid ?: throw Exception("Usuario no autenticado"),
                "weight" to weight,
                "timestamp" to Timestamp.now(),
                "note" to note
            )

            weightCollection.add(entry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error guardando peso: ${e.localizedMessage}"))
        }
    }

    // Obtener historial de peso
    suspend fun getWeightHistory(limit: Int = 30): List<WeightEntryModel> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val snapshot = firestore.collection("user_weight_entries")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    // Conversión segura a QueryDocumentSnapshot
                    val qDoc = doc as? QueryDocumentSnapshot
                    qDoc?.let { WeightEntryModel.fromFirestore(it) }
                } catch (e: Exception) {
                    Log.w("WeightRepo", "Error procesando documento ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeightRepo", "Error obteniendo historial", e)
            emptyList()
        }

        // Escuchar cambios en tiempo real
        fun observeWeightUpdates(onUpdate: (List<WeightEntryModel>) -> Unit) {
            val userId = auth.currentUser?.uid ?: return

            weightCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    error?.let {
                        Log.e("WeightRepo", "Error en listener", it)
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        val entries = it.documents
                            .mapNotNull { doc ->
                                try {
                                    (doc as? QueryDocumentSnapshot)?.let {
                                        WeightEntryModel.fromFirestore(
                                            it
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.w("WeightRepo", "Error procesando documento ${doc.id}", e)
                                    null
                                }
                            }
                        onUpdate(entries)
                    }
                }
        }

        // Eliminar entrada
        suspend fun deleteWeightEntry(entryId: String): Result<Unit> {
            return try {
                weightCollection.document(entryId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(Exception("Error eliminando registro: ${e.localizedMessage}"))
            }
        }
    }
}