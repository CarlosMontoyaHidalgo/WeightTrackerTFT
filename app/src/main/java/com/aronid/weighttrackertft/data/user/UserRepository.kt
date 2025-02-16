package com.aronid.weighttrackertft.data.user

import com.aronid.weighttrackertft.constants.FirestoreCollections
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
                    "name" to "Usuario"
                )
            ).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
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

    suspend fun getUserName(userId: String): Result<String> {
        return try {
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

    /* de pago
    suspend fun updateUserProfileImage(userId: String, imageUrl: Uri): Result<String> {
        return try {
            // Genera nombre único para evitar caché
            val timestamp = System.currentTimeMillis()
            val fileRef = storageRef.child("profile_images/${userId}_$timestamp.jpg")

            // Sube el archivo con monitorización de progreso
            val uploadTask = fileRef.putFile(imageUri).await()

            if (uploadTask.task.isSuccessful) {
                val downloadUrl = fileRef.downloadUrl.await().toString()

                // Actualiza Firestore con transacción
                firestore.runTransaction { transaction ->
                    val docRef = userCollection.document(userId)
                    transaction.update(docRef, "profileImageUrl", downloadUrl)
                }.await()

                Result.success(downloadUrl)
            } else {
                Result.failure(Exception("Error en subida de archivo"))
            }
        } catch (e: Exception) {
            // Rollback: Elimina imagen subida si falla actualización
            fileRef?.delete().await()
            Result.failure(e)
        }
    }
*/

    suspend fun logout() {
        FirebaseAuth.getInstance().signOut()
    }


}
