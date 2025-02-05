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
                    "createdAt" to FieldValue.serverTimestamp()
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
}
