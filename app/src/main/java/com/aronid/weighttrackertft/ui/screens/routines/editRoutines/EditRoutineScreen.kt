package com.aronid.weighttrackertft.ui.screens.routines.editRoutines

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.tabs.routines.details.RoutineDetailsTab
import com.aronid.weighttrackertft.ui.components.tabs.routines.exercises.ExerciseSelectionTab
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoutineScreen(
    innerPadding: PaddingValues,
    routineId: String,
    navHostController: NavHostController,
    viewModel: EditRoutineViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.details), stringResource(R.string.exercises))

    val routine by viewModel.routine.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()
    val targetMuscles by viewModel.targetMuscles.collectAsState()
    val availableMuscles by viewModel.availableMuscles.collectAsState()
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val routineUpdatedMessage = stringResource(id = R.string.routine_updated_successfully)
    val nameCannotBeEmptyMessage = stringResource(id = R.string.name_cannot_be_empty)

    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

    LaunchedEffect(routine) {
        routine?.let {
            name = it.name
            goal = it.goal
            description = it.description
        }
    }

    LaunchedEffect(state) {
        when (val currentState = state) {
            is EditRoutineViewModel.State.Success -> {
                Toast.makeText(context, routineUpdatedMessage, Toast.LENGTH_SHORT).show()
                navHostController.popBackStack()
            }

            is EditRoutineViewModel.State.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    val hasChanges = routine?.let {
        name != it.name || goal != it.goal || description != it.description ||
                exercises.map { ex -> ex.id } != it.exercises.map { ex -> ex.id } ||
                targetMuscles != it.targetMuscles
    } ?: false

    val (textConfig, layoutConfig, stateConfig) = getDefaultButtonConfig(hasChanges)
    val borderConfig = getDefaultBorderConfig()

    Scaffold(
        modifier = Modifier,
        content = { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    Log.d("EditRoutineScreen", "selectedTab: $selectedTab")
                    Log.d("EditRoutineScreen", "exercises: ${exercises.map { it.name }}")
                    Log.d("EditRoutineScreen", "allExercises: ${allExercises.map { it.name }}")
                    Log.d("EditRoutineScreen", "targetMuscles: $targetMuscles")
                    Log.d("EditRoutineScreen", "targetMuscles: $targetMuscles")



                    when (selectedTab) {
                        0 -> RoutineDetailsTab(
                            name = name,
                            goal = goal,
                            description = description,
                            targetMuscles = targetMuscles,
                            availableMuscles = availableMuscles,
                            selectedExercises = exercises,
                            onNameChange = { name = it },
                            onGoalChange = { goal = it },
                            onDescriptionChange = { description = it },
                            onMuscleSelected = { viewModel.updateTargetMuscles(it) },
                            onExerciseRemove = { exerciseId -> viewModel.removeExercise(exerciseId) }
                        )

                        1 -> ExerciseSelectionTab(
                            availableExercises = allExercises,
                            selectedExerciseIds = exercises.map { it.id },
                            onExerciseSelected = { exercise ->
                                if (exercises.any { it.id == exercise.id }) {
                                    viewModel.removeExercise(exercise.id)
                                } else {
                                    viewModel.addExercise(exercise)
                                }
                            },
                            isAddMode = true
                        )
                    }
                }

                if (selectedTab == 0) {
                    NewCustomButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.updateRoutine(
                                    name = name,
                                    goal = goal,
                                    description = description,
                                    targetMuscles = targetMuscles
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    nameCannotBeEmptyMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        text = stringResource(R.string.save_changes),
                        buttonType = ButtonType.ELEVATED,
                        containerColor = Color.Blue,
                        textConfig = textConfig.copy(textColor = Color.White),
                        layoutConfig = layoutConfig,
                        stateConfig = stateConfig,
                        borderConfig = borderConfig
                    )
                }
            }
        }
    )
}
