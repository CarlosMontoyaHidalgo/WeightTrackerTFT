package com.aronid.weighttrackertft.ui.screens.routines.createRoutine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.routine.exerciseItem.ExerciseItem
import com.aronid.weighttrackertft.ui.components.searchBar.muscle.MuscleSearchBar

@Composable
fun ChooseExercisesScreen(
    innerPadding: PaddingValues,
    availableExercises: List<ExerciseModel>,
    initialSelectedExerciseIds: List<String>,
    navHostController: NavHostController
) {
    var selectedExerciseIds by remember { mutableStateOf(initialSelectedExerciseIds) }
    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = true,
        formContent = {
            Text(
                text = "Elegir Ejercicios",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (availableExercises.isEmpty()) {
                Text(
                    text = "No hay ejercicios disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                MuscleSearchBar(
                    exercises = availableExercises,
                    selectedExerciseIds = selectedExerciseIds,
                    onExerciseSelected = { exercise ->
                        if (!selectedExerciseIds.contains(exercise.id)) {
                            selectedExerciseIds = selectedExerciseIds + exercise.id
                        } else {
                            selectedExerciseIds = selectedExerciseIds - exercise.id
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if (selectedExerciseIds.isNotEmpty()) {
                    Text(
                        text = "Ejercicios seleccionados:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 100.dp)) {
                        items(selectedExerciseIds) { exerciseId ->
                            val exercise = availableExercises.find { it.id == exerciseId }
                            exercise?.let {
                                ExerciseItem(
                                    exercise = it,
                                    isSelected = true,
                                    onExerciseSelected = {
                                        selectedExerciseIds = selectedExerciseIds - exerciseId
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        formButton = {
            Button(
                onClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedExerciseIds", selectedExerciseIds)
                    navHostController.popBackStack()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Confirmar Selección")
            }
        }
    )
}