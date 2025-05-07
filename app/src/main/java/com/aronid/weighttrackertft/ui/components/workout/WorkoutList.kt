package com.aronid.weighttrackertft.ui.components.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutList(workouts: List<WorkoutModel>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Lista de entrenamientos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(workouts) { workout ->
                WorkoutItem(workout = workout)
                Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: WorkoutModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Tipo: ${workout.workoutType}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Calorías: ${workout.calories} kcal",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Volumen: ${workout.volume} kg",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Intensidad: ${workout.intensity}%",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Fecha: ${workout.date?.toDate()?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate()?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "No disponible"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}