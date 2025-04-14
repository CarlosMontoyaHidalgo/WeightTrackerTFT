package com.aronid.weighttrackertft.ui.components.cards.CustomWorkoutCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.ui.components.tags.MyTag
import com.google.firebase.Timestamp
@Composable
fun WorkoutCard(
    modifier: Modifier = Modifier,
    workout: WorkoutModel,
    onDeleteClick: () -> Unit = {},
    showDeleteButton: Boolean = false,
    isChecked: Boolean = false,
    showCheckbox: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    navHostController: NavHostController? = null,
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        onClick = { onCardClick() }
    ) {
        Column( // Cambiamos Row por Column para más espacio vertical
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = workout.workoutType.ifEmpty { "Entrenamiento sin nombre" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fecha: ${workout.date?.toDate()?.toString() ?: "No disponible"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (showCheckbox) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onCheckedChange(it) },
                        modifier = Modifier
                    )
                }
            }

            // Métricas adicionales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "Calorías", value = "${workout.calories} kcal")
                MetricItem(label = "Volumen", value = "${workout.volume} kg")
                MetricItem(label = "Intensidad", value = workout.intensity.toString())
            }

            // Músculos primarios
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Primarios: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(workout.primaryMuscleIds) { muscle ->
                        MyTag(text = muscle.ifEmpty { "Ninguno" })
                    }
                }
            }

            // Músculos secundarios
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Secundarios: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(workout.secondaryMuscleIds) { muscle ->
                        MyTag(text = muscle.ifEmpty { "Ninguno" })
                    }
                }
            }

            if (workout.exercises.isNotEmpty()) {
                Text(
                    text = "Ejercicios (${workout.exercises.size}):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(workout.exercises.take(2)) { exercise ->
                        MyTag(text = exercise.exerciseName.toString())
                    }
                    if (workout.exercises.size > 2) {
                        item {
                            MyTag(text = "+${workout.exercises.size - 2} más")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

