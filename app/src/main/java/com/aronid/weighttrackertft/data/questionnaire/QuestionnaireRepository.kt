package com.aronid.weighttrackertft.data.questionnaire

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class QuestionnaireRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getQuestionnaireStatus(userId: String): Boolean {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (!document.exists()) false
            else document.getBoolean("hasCompletedQuestionnaire") ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateQuestionnaireStatus(userId: String, hasCompleted: Boolean) {
        try {
            firestore.collection("users").document(userId)
                .update("hasCompletedQuestionnaire", hasCompleted).await()
        } catch (e: Exception) {
            if (e is NullPointerException) {
                firestore.collection("users").document(userId)
                    .set(mapOf("hasCompletedQuestionnaire" to hasCompleted))
            } else {
                throw e
            }
        }
    }
}