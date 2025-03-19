package com.aronid.weighttrackertft.data.muscles

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class MuscleModel(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("primary_muscle") val primaryMuscle: List<String> = emptyList(), // Add default
    @PropertyName("secondary_muscle") val secondaryMuscle: List<String>? = null,
    @PropertyName("image_url") val imageUrl: Int = 0,
    @PropertyName("description") val description: String = ""
)