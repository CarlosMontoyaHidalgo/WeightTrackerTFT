package com.aronid.weighttrackertft.data.exercises

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class ExerciseModel(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("equipment") val equipment: List<String> = emptyList(),
    @PropertyName("primaryMuscle") val primaryMuscle: DocumentReference? = null,
    @PropertyName("secondaryMuscle") val secondaryMuscle: List<DocumentReference> = emptyList(),
    @PropertyName("video_url") val videoUrl: String = "",
    @PropertyName("imageUrl") val imageUrl: String = "",
    @PropertyName("type") val type: String = "",
    @PropertyName("requiresWeight") val requiresWeight: Boolean = false,
    val instructions: Map<String, String>? = null,
    val commonMistakes: Map<String, List<String>>? = null,
    val tips: Map<String, List<String>>? = null,
    val met: Double? = null,
    val primaryMuscleName: String? = null,
    val secondaryMuscleNames: List<String> = emptyList()
) {


    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "equipment" to equipment,
            "primaryMuscles" to primaryMuscle,
            "secondaryMuscles" to secondaryMuscle,
            "videoUrl" to videoUrl,
            "imageUrl" to imageUrl,
            "type" to type
        )
    }

    fun matchesSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
            primaryMuscle.toString(),
            secondaryMuscle.joinToString(" "),
            "$name ${primaryMuscle.toString()}",
            "$name${primaryMuscle.toString()}",
            "${name.first()} ${primaryMuscle.toString().first()}",
            "$primaryMuscle $name",
            "$primaryMuscle$name",
            "${primaryMuscle.toString().first()} ${name.first()}",
        )
        return matchingCombinations.any { it.contains(query, ignoreCase = true) }
    }
}