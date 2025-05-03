package com.aronid.weighttrackertft.ui.components.chatbot

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.data.exercises.ExerciseModel

// Función para detectar si un mensaje es una rutina
fun isRoutineMessage(message: String): Boolean {
    val routineKeywords = listOf(
        "rutina", "rutina de", "rutina de empuje", "rutina de tracción"
    )
    return routineKeywords.any { message.contains(it, ignoreCase = true) } ||
            message.lines().any { line -> line.matches(Regex("\\d+\\.\\s.*")) }
}

// Normalizar texto para corregir errores tipográficos
fun normalizeText(text: String): String {
    return text.lowercase()
        .replace("[áàâãä]", "a")
        .replace("[éèêë]", "e")
        .replace("[íìîï]", "i")
        .replace("[óòôõö]", "o")
        .replace("[úùûü]", "u")
}

// Mapeo completo entre nombres del bot (español) y IDs de documentos en Firebase
val exerciseIdMapping = mapOf(
    "press de banca" to "bench_press",
    "press de banmca" to "bench_press",
    "remo inclinado" to "bent_over_rows",
    "curl de bíceps" to "bicep_curls",
    "burpees" to "burpees",
    "elevaciones de pantorrillas" to "calf_raises",
    "press de pecho" to "chest_press",
    "peso muerto" to "deadlift",
    "aperturas con mancuernas" to "dumbbell_flyes",
    "rodillas altas" to "high_knees",
    "saltar la cuerda" to "jump_rope",
    "jalón en polea" to "lat_pulldown",
    "curl de piernas" to "leg_curls",
    "extensiones de piernas" to "leg_extensions",
    "prensa de piernas" to "leg_press",
    "zancadas" to "lunges",
    "escaladores" to "mountain_climbers",
    "plancha" to "plank",
    "dominadas" to "pull_ups",
    "flexiones" to "push_ups",
    "correr" to "running",
    "press de hombros" to "shoulder_press",
    "abdominales" to "sit_ups",
    "sentadilla" to "squat",
    "fondos en paralelas" to "tricep_dips",
    "extensiones de tríceps" to "tricep_extensions",
    "extensiones de triceps" to "tricep_extensions"
)

fun extractExerciseIds(routineText: String): List<String> {
    val exerciseIds = mutableListOf<String>()
    val normalizedRoutine = normalizeText(routineText)

    // First try to find numbered lines (1. Exercise name)
    val numberedLines = routineText.lines()
        .filter { it.matches(Regex("\\d+\\.\\s.*")) }
        .map { it.replaceFirst(Regex("\\d+\\.\\s+"), "").trim() }

    if (numberedLines.isNotEmpty()) {
        // Process each line as a potential exercise
        for (line in numberedLines) {
            val normalizedLine = normalizeText(line)
            for ((botName, id) in exerciseIdMapping) {
                val normalizedBotName = normalizeText(botName)
                if (normalizedLine.contains(normalizedBotName) ||
                    normalizedBotName.contains(normalizedLine)) {
                    exerciseIds.add(id)
                    break  // Found a match for this line
                }
            }
        }
    } else {
        // Fallback to comma-separated approach
        val potentialExercises = normalizedRoutine.split(",", "\n").map { it.trim() }

        for (potentialExercise in potentialExercises) {
            if (potentialExercise.isBlank()) continue

            for ((botName, id) in exerciseIdMapping) {
                val normalizedBotName = normalizeText(botName)
                if (normalizedBotName.contains(potentialExercise) ||
                    potentialExercise.contains(normalizedBotName)) {
                    exerciseIds.add(id)
                    break  // Found a match for this potential exercise
                }
            }
        }
    }

    // For debugging purposes
    println("Found exercises: ${exerciseIds.joinToString()}")

    return exerciseIds.distinct()
}

// Extraer músculos objetivo del texto de la rutina
fun extractTargetMuscles(routineText: String, availableMuscles: List<String>): List<String> {
    val targetMuscles = mutableListOf<String>()
    val normalizedRoutine = normalizeText(routineText)

    availableMuscles.forEach { muscle ->
        val normalizedMuscle = normalizeText(muscle)
        if (normalizedRoutine.contains(normalizedMuscle)) {
            targetMuscles.add(muscle)
        }
    }

    if (normalizedRoutine.contains("dia de empuje")) {
        targetMuscles.addAll(listOf("Pecho", "Tríceps", "Hombros"))
    }

    return if (targetMuscles.isEmpty()) listOf("Piernas") else targetMuscles.distinct()
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    availableExercises: List<ExerciseModel>,
    availableMuscles: List<String>,
    onSaveRoutine: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 0.dp,
                        bottomEnd = if (message.isUser) 0.dp else 16.dp
                    )
                )
                .background(
                    if (message.isUser) MaterialTheme.colorScheme.primary
                    else Color(0xFFEBEBEB)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (!message.isUser && message.isRoutine) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { onSaveRoutine(message.text) },
                modifier = Modifier
                    .align(if (message.isUser) Alignment.End else Alignment.Start),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar rutina",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Guardar Rutina",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val delays = listOf(200, 400, 600)

    Row(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEBEBEB))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEachIndexed { index, delay ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.3f at delay
                        0.6f at delay + 300
                        0.3f at delay + 600
                    },
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = alpha))
            )

            if (index != delays.lastIndex) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isRoutine: Boolean = false
)
