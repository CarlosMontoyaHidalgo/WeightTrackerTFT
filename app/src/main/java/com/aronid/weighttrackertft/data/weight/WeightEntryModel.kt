package com.aronid.weighttrackertft.data.weight

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot


data class WeightEntryModel(
    val documentId: String = "",
    val userId: String,
    val weight: Double,
    val timestamp: Timestamp,
    val note: String? = null
) {
    companion object {
        fun fromFirestore(doc: QueryDocumentSnapshot): WeightEntryModel {
            return WeightEntryModel(
                documentId = doc.id,
                userId = doc.getString("userId") ?: "",
                weight = doc.getDouble("weight") ?: 0.0,
                timestamp = doc.getTimestamp("timestamp") ?: com.google.firebase.Timestamp.now(),
                note = doc.getString("note")
            )
        }
    }

}