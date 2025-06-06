package com.aronid.weighttrackertft.utils

import java.util.Locale

object Translations {
    val exerciseTranslations = mapOf(
        "squat" to mapOf("en" to "Squat", "es" to "Sentadilla"),
        "bench_press" to mapOf("en" to "Bench Press", "es" to "Press de Banca"),
        "deadlift" to mapOf("en" to "Deadlift", "es" to "Peso Muerto"),
        "pull_ups" to mapOf("en" to "Pull-Ups", "es" to "Dominadas"),
        "plank" to mapOf("en" to "Plank", "es" to "Plancha"),
        "bicep_curls" to mapOf("en" to "Bicep Curls", "es" to "Curls de Bíceps"),
        "tricep_dips" to mapOf("en" to "Tricep Dips", "es" to "Fondos de Tríceps"),
        "lunges" to mapOf("en" to "Lunges", "es" to "Zancadas"),
        "shoulder_press" to mapOf("en" to "Shoulder Press", "es" to "Press de Hombros"),
        "leg_press" to mapOf("en" to "Leg Press", "es" to "Prensa de Piernas"),
        "calf_raises" to mapOf("en" to "Calf Raises", "es" to "Elevaciones de Pantorrillas"),
        "running" to mapOf("en" to "Running", "es" to "Correr"),
        "jump_rope" to mapOf("en" to "Jump Rope", "es" to "Saltar la Cuerda"),
        "burpees" to mapOf("en" to "Burpees", "es" to "Burpees"),
        "mountain_climbers" to mapOf("en" to "Mountain Climbers", "es" to "Escaladores"),
        "high_knees" to mapOf("en" to "High Knees", "es" to "Rodillas Altas"),
        "push_ups" to mapOf("en" to "Push-Ups", "es" to "Flexiones"),
        "sit_ups" to mapOf("en" to "Sit Ups", "es" to "Abdominales"),
        "leg_curls" to mapOf("en" to "Leg Curls", "es" to "Curls de Piernas"),
        "lat_pulldown" to mapOf("en" to "Lat Pulldown", "es" to "Jalón al Pecho"),
        "dumbbell_flyes" to mapOf("en" to "Dumbbell Flyes", "es" to "Aperturas con Mancuernas"),
        "tricep_extensions" to mapOf("en" to "Tricep Extensions", "es" to "Extensiones de Tríceps"),
        "bent_over_rows" to mapOf("en" to "Bent Over Rows", "es" to "Remo Inclinado"),
        "leg_extensions" to mapOf("en" to "Leg Extensions", "es" to "Extensiones de Piernas"),
        "chest_press" to mapOf("en" to "Chest Press", "es" to "Press de Pecho")
    )

    val muscleTranslations = mapOf(
        "legs" to mapOf("en" to "Legs", "es" to "Piernas"),
        "glutes" to mapOf("en" to "Glutes", "es" to "Glúteos"),
        "hamstrings" to mapOf("en" to "Hamstrings", "es" to "Isquiotibiales"),
        "chest" to mapOf("en" to "Chest", "es" to "Pecho"),
        "shoulders" to mapOf("en" to "Shoulders", "es" to "Hombros"),
        "triceps" to mapOf("en" to "Triceps", "es" to "Tríceps"),
        "back" to mapOf("en" to "Back", "es" to "Espalda"),
        "biceps" to mapOf("en" to "Biceps", "es" to "Bíceps"),
        "abs" to mapOf("en" to "Abs", "es" to "Abdominales"),
        "lower_back" to mapOf("en" to "Lower Back", "es" to "Espalda Baja"),
        "forearms" to mapOf("en" to "Forearms", "es" to "Antebrazos"),
        "full_body" to mapOf("en" to "Full Body", "es" to "Cuerpo Completo"),
        "quads" to mapOf("en" to "Quads", "es" to "Cuádriceps"),
        "hip_flexors" to mapOf("en" to "Hip Flexors", "es" to "Flexores de Cadera"),
        "calves" to mapOf("en" to "Calves", "es" to "Gemelos")
    )

    val uiStrings = mapOf(
        "description_label" to mapOf("en" to "Description", "es" to "Descripción"),
        "primary_muscle_label" to mapOf("en" to "Primary Muscle", "es" to "Músculo Principal"),
        "secondary_muscles_label" to mapOf("en" to "Secondary Muscles", "es" to "Músculos Secundarios")
    )

    val currentLocale: String
        get() = Locale.getDefault().language

    fun translateAndFormat(displayName: String, translations: Map<String, Map<String, String>>): String {
        val id = translations.entries.find { (_, langMap) ->
            langMap.values.any { it.equals(displayName, ignoreCase = true) }
        }?.key ?: displayName.lowercase(Locale.getDefault())
        val baseTranslation = translations[id]
            ?.get(currentLocale) ?: translations[id]?.get("en") ?: displayName
        return baseTranslation.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }

}