package com.aronid.weighttrackertft.data.routine

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class RoutineModel(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("goal") val goal: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("exercises") val exercises: List<DocumentReference> = emptyList(),
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("userId") val userId: String = "",
    @PropertyName("targetMuscles") val targetMuscles: List<String> = emptyList(),
    @PropertyName("isFavorite") val isFavorite: Boolean = false,

    ) {
    init {
        /*
        require(name.isNotBlank()) { "Name can't be blank" }
        require(goal.isNotBlank()) { "Goal can't be blank" }
        require(description.isNotBlank()) { "Description can't be blank" }
        require(userId.isNotBlank()) { "User ID can't be blank" }
    */
    }
    fun matchesSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf(
            name,
            goal,
            description,
            "$name $goal",
            "$name$goal",
            "${name.first()} ${goal.first()}",
            "$goal $name",
            "$goal$name",
            "${goal.first()} ${name.first()}",
        )
        return matchingCombinations.any{ it.contains(query, ignoreCase = true) }
    }
}
