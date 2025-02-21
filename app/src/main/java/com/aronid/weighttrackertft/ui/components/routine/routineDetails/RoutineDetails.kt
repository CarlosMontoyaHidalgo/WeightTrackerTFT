package com.aronid.weighttrackertft.ui.components.routine.routineDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.ui.components.routine.exerciseItem.ExerciseItem

@Composable
fun RoutineDetailsContent(routine: RoutineModel?, exercises: List<ExerciseModel>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            routine?.let { Text(text = it.name, style = MaterialTheme.typography.headlineMedium) }
            routine?.let { Text(text = it.goal, style = MaterialTheme.typography.bodyLarge) }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Ejercicios", style = MaterialTheme.typography.headlineSmall)
        }
        items(exercises) { exercise ->
            ExerciseItem(exercise, false, {})
        }

    }
}