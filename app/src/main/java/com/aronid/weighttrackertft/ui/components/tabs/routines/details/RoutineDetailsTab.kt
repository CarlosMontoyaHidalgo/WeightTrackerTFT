package com.aronid.weighttrackertft.ui.components.tabs.routines.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.utils.Translations

@Composable
fun RoutineDetailsTab(
    name: String,
    goal: String,
    description: String,
    targetMuscles: List<String>,
    availableMuscles: List<String>,
    onNameChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMuscleSelected: (List<String>) -> Unit,
    selectedExercises: List<ExerciseModel>? = null,
    onExerciseRemove: ((String) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = goal,
                onValueChange = onGoalChange,
                label = { Text(stringResource(R.string.goal)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = stringResource(R.string.target_muscles),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            MuscleGrid(
                muscles = availableMuscles,
                selectedMuscles = targetMuscles,
                onSelectionChanged = onMuscleSelected,
            )
        }

        item {
            Text(
                text = stringResource(R.string.selected_exercises),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            when {
                selectedExercises == null -> {
                    Text(
                        text = stringResource(R.string.no_exercises_selected),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                selectedExercises.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_exercises_selected),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        items(selectedExercises) { exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onExerciseRemove?.invoke(exercise.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Translations.translateAndFormat(exercise.name, Translations.exerciseTranslations),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.remove_exercise),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onExerciseRemove?.invoke(exercise.id) },
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleGrid(
    muscles: List<String>,
    selectedMuscles: List<String>,
    onSelectionChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        muscles.chunked(3).forEach { rowMuscles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowMuscles.forEach { muscle ->
                    FilterChip(
                        selected = selectedMuscles.contains(muscle),
                        onClick = {
                            val newSelection = if (selectedMuscles.contains(muscle)) {
                                selectedMuscles - muscle
                            } else {
                                selectedMuscles + muscle
                            }
                            onSelectionChanged(newSelection)
                        },
                        label = { Text(Translations.translateAndFormat(muscle, Translations.muscleTranslations)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
                // Rellenar espacios vacíos en la fila si hay menos de 3 elementos
                repeat(3 - rowMuscles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}