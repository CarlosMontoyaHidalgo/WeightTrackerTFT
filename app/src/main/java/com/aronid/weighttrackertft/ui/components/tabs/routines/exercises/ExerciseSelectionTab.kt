package com.aronid.weighttrackertft.ui.components.tabs.routines.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.ui.components.searchBar.muscle.MuscleSearchBar

@Composable
fun ExerciseSelectionTab(
    availableExercises: List<ExerciseModel>,
    selectedExerciseIds: List<String>,
    onExerciseSelected: (ExerciseModel) -> Unit,
    isAddMode: Boolean = true // Controla si es modo "agregar" o "seleccionados"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = if (isAddMode) Arrangement.spacedBy(8.dp) else Arrangement.Top
    ) {
        Text(
            text = stringResource(if (isAddMode) R.string.add_exercises else R.string.selected_exercises),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = if (isAddMode) 0.dp else 8.dp)
        )

        MuscleSearchBar(
            exercises = availableExercises,
            selectedExerciseIds = selectedExerciseIds,
            onExerciseSelected = onExerciseSelected,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isAddMode) Modifier.fillMaxHeight() else Modifier.height(400.dp))
        )
    }
}