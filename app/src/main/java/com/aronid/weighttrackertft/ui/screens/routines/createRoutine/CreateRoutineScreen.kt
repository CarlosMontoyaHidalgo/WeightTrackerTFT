package com.aronid.weighttrackertft.ui.screens.routines.createRoutine

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.searchBar.muscle.MuscleSearchBar
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun CreateRoutineScreen(
    innerPadding: PaddingValues,
    viewModel: CreateRoutineViewModel,
    navHostController: NavHostController,
) {

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedExerciseIds by remember { mutableStateOf(listOf<String>()) }
    val state by viewModel.state.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()

    val navResult = navHostController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<List<String>>("selectedExerciseIds")

    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs

    LaunchedEffect(navResult) {
        navResult?.value?.let {
            selectedExerciseIds = it
            navHostController.currentBackStackEntry?.savedStateHandle?.remove<List<String>>("selectedExerciseIds")
        }
    }

    LaunchedEffect(name, goal, description, selectedExerciseIds) {
        viewModel.checkFormValidity(name, goal, description, selectedExerciseIds)
    }

    LaunchedEffect(state) {
        when (val currentState = state) {
            is CreateRoutineViewModel.State.Success -> {
                Toast.makeText(context, "Rutina creada!", Toast.LENGTH_SHORT).show()
                navHostController.popBackStack()
            }

            is CreateRoutineViewModel.State.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("Objetivo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "Seleccionar Ejercicios",
                style = MaterialTheme.typography.titleMedium
            )

            MuscleSearchBar(
                exercises = availableExercises,
                selectedExerciseIds = selectedExerciseIds,
                onExerciseSelected = { exercise ->
                    selectedExerciseIds = if (!selectedExerciseIds.contains(exercise.id)) {
                        selectedExerciseIds + exercise.id
                    } else {
                        selectedExerciseIds - exercise.id
                    }
                },
                modifier = Modifier
                    .fillMaxSize()

            )
        },
        formButton = {
            NewCustomButton(
                modifier = Modifier,
                onClick = {
                    viewModel.createRoutine(name, goal, description, selectedExerciseIds)
                },
                text = "Crear Rutina",
                buttonType = ButtonType.ELEVATED,
                containerColor = Color.Blue,
                textConfig = buttonConfigs.textConfig.copy(textColor = Color.White),
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig,
            )

        }
    )
}

