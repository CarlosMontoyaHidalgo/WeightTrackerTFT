package com.aronid.weighttrackertft.data.user

import com.aronid.weighttrackertft.constants.FirestoreCollections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection(FirestoreCollections.USERS)

    suspend fun updateUserQuestionnaireStatus(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("questionnaireCompleted", true).await()
    }

    // Función para obtener los datos del usuario (ejemplo de lectura)
    suspend fun getUser(userId: String): UserModel? {
        return try {
            val userDoc = userCollection.document(userId).get().await()
            if (userDoc.exists()) {
                userDoc.toObject(UserModel::class.java) // Mapeamos el documento a nuestro modelo
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Función para guardar o actualizar el usuario en Firestore
    suspend fun saveUser(userModel: UserModel) {
        val user = auth.currentUser ?: throw Exception("User not found")
        val userRef = userCollection.document(user.uid) // Usamos el UID de Firebase como ID en Firestore
        userRef.set(userModel.toMap()).await() // Guardamos los datos del usuario en Firestore
    }

}