package com.aronid.weighttrackertft.data.user

import android.util.Log
import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val userCollection = firestore.collection(FirestoreCollections.USERS)

    suspend fun getCurrentUserHeight(): Double? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.getDouble("height")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserHeight(height: Double): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            firestore.collection("users").document(userId)
                .update("height", height)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")

            firestore.collection("users").document(user.uid).set(
                mapOf(
                    "hasCompletedQuestionnaire" to false,
                    "email" to email,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "profileImageUrl" to null,
                    "name" to "Usuario",

                    )
            ).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser {
        return auth.currentUser ?: throw IllegalStateException("User Not Available")
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun saveUser(user: UserModel) {
        val userDoc = userCollection.document(user.id)
        try {
            userDoc.set(user.toMap()).await()  // Guarda el documento completo
        } catch (e: Exception) {
            throw Exception("Error saving user: ${e.localizedMessage}", e)
        }
    }

    suspend fun updateUserFields(userId: String, updates: Map<String, Any>) {
        userCollection.document(userId).update(updates).await()
    }

    suspend fun getUserName(): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))
            val document = userCollection.document(userId).get().await()
            val name = document.getString("name") ?: "Usuario"
            Result.success(name)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserName(userId: String, newName: String): Result<Unit> {
        return try {
            userCollection.document(userId).update("name", newName).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfileImage(userId: String): Result<String?> {
        return try {
            val document = userCollection.document(userId).get().await()
            val profileImageUrl = document.getString("profileImageUrl")
            Result.success(profileImageUrl ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun getCurrentUserWeight(): Double? {
        val userId = getCurrentUser().uid ?: return null
        return try {
            val userDoc =
                userCollection.document(userId).get().await().toObject(UserModel::class.java)
            userDoc?.weight
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getBodyFatPercentage(): Double? {
        return userCollection.document(getCurrentUser().uid)
            .get().await().getDouble("bodyFat")
    }

    suspend fun getAccountCreationDate(): Result<Timestamp> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val createdAt = document.getTimestamp("createdAt")
                ?: return Result.failure(Exception("Fecha de creación no encontrada"))

            Result.success(createdAt)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener fecha de creación: ${e.message}", e))
        }
    }

    suspend fun getUserData(userId: String): UserModel? {
        return try {
            val snapshot = userCollection.document(userId).get().await()
            if (!snapshot.exists()) {
                Log.w("UserRepository", "Usuario no encontrado: $userId")
                return null
            }

            snapshot.toObject(UserModel::class.java)?.let { user ->
                user.copy(id = snapshot.id).also {
                    Log.d("UserRepository", "Datos obtenidos para usuario: $userId")
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error obteniendo usuario: ${e.message}")
            null
        }
    }

    suspend fun updateUserWeight(newWeight: Double): Result<Unit> {
        return try {
            val userId = getCurrentUser().uid
            userCollection.document(userId).update("weight", newWeight).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar el peso: ${e.localizedMessage}", e))
        }
    }

    suspend fun updateUserProfileImage(imageUrl: String): Result<Unit> {
        return try {
            val userId = getCurrentUser().uid
            userCollection.document(userId).update("profileImageUrl", imageUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "Error al actualizar la imagen de perfil: ${e.localizedMessage}",
                    e
                )
            )
        }
    }

    suspend fun getUserProfile(): Result<UserModel> {
        return try {
            val userId = getCurrentUser().uid
            val snapshot = userCollection.document(userId).get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Perfil de usuario no encontrado"))
            }

            val user = snapshot.toObject(UserModel::class.java)
                ?: return Result.failure(Exception("Error al convertir el documento a UserModel"))

            Result.success(user.copy(id = userId))
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "Error al obtener el perfil del usuario: ${e.localizedMessage}",
                    e
                )
            )
        }
    }


}
