package com.aronid.weighttrackertft.utils

import androidx.annotation.DrawableRes
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.utils.Translations.exerciseTranslations
import java.util.Locale

// Mapa que asocia el identificador del ejercicio con el ID del recurso drawable
private val exerciseImageMap = mapOf(
    "Bench Press" to R.drawable.bench_press,
    "Press De Banca" to R.drawable.bench_press,
    "bench_press" to R.drawable.bench_press,
    /*"squat" to R.drawable.squat,
    "deadlift" to R.drawable.deadlift,
    "pull_ups" to R.drawable.pull_ups,
    "plank" to R.drawable.plank,
    "bicep_curls" to R.drawable.bicep_curls,
    "tricep_dips" to R.drawable.tricep_dips,
    "lunges" to R.drawable.lunges,
    "shoulder_press" to R.drawable.shoulder_press,
    "leg_press" to R.drawable.leg_press,
    "calf_raises" to R.drawable.calf_raises,
    "running" to R.drawable.running,
    "jump_rope" to R.drawable.jump_rope,
    "burpees" to R.drawable.burpees,
    "mountain_climbers" to R.drawable.mountain_climbers,
    "high_knees" to R.drawable.high_knees,
    "push_ups" to R.drawable.push_ups,
    "sit_ups" to R.drawable.sit_ups,
    "leg_curls" to R.drawable.leg_curls,
    "lat_pulldown" to R.drawable.lat_pulldown,
    "dumbbell_flyes" to R.drawable.dumbbell_flyes,
    "tricep_extensions" to R.drawable.tricep_extensions,
    "bent_over_rows" to R.drawable.bent_over_rows,
    "leg_extensions" to R.drawable.leg_extensions,
    "chest_press" to R.drawable.chest_press*/
)

// Función para obtener el ID del recurso drawable según el identificador del ejercicio
@DrawableRes
fun getExerciseImageResource(exerciseId: String?): Int {
    return exerciseImageMap[exerciseId] ?: R.drawable.background // Fallback a imagen por defecto
}

// Función para obtener el identificador del ejercicio (por ejemplo, "bench_press") desde exerciseName
fun getExerciseId(exerciseName: String?, language: String = Locale.getDefault().language): String? {
    if (exerciseName == null) return null

    // Primero busca coincidencia exacta
    exerciseTranslations.entries.forEach { entry ->
        entry.value.values.forEach { translatedName ->
            if (translatedName.equals(exerciseName, ignoreCase = true)) {
                return entry.key
            }
        }
    }

    // Si no encuentra, prueba con eliminación de espacios y mayúsculas
    val normalizedInput = exerciseName.lowercase().replace(" ", "_")
    return exerciseTranslations.keys.find {
        it.equals(normalizedInput, ignoreCase = true)
    }
}