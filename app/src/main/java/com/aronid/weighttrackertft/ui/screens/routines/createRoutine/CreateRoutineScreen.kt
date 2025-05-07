package com.aronid.weighttrackertft.ui.screens.routines.createRoutine

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    innerPadding: PaddingValues,
    viewModel: CreateRoutineViewModel,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.details), stringResource(R.string.exercises))

    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedExerciseIds by remember { mutableStateOf(listOf<String>()) }
    var targetMuscles by remember { mutableStateOf(listOf<String>()) }

    val state by viewModel.state.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()
    val availableMuscles by viewModel.availableMuscles.collectAsState()
    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs

    // Convert selectedExerciseIds to a list of ExerciseModel
    val selectedExercises = availableExercises.filter { selectedExerciseIds.contains(it.id) }

    val routineCreatedMessage = stringResource(id = R.string.routine_created)

    LaunchedEffect(state) {
        when (val currentState = state) {
            is CreateRoutineViewModel.State.Success -> {
                Toast.makeText(context, routineCreatedMessage, Toast.LENGTH_SHORT).show()
                navHostController.popBackStack()
            }

            is CreateRoutineViewModel.State.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(name, goal, description, selectedExerciseIds, targetMuscles) {
        viewModel.checkFormValidity(name, goal, description, selectedExerciseIds, targetMuscles)
    }

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

                    when (selectedTab) {
                        0 -> RoutineDetailsTab(
                            name = name,
                            goal = goal,
                            description = description,
                            targetMuscles = targetMuscles,
                            availableMuscles = availableMuscles,
                            onNameChange = { name = it },
                            onGoalChange = { goal = it },
                            onDescriptionChange = { description = it },
                            onMuscleSelected = { targetMuscles = it },
                            selectedExercises = selectedExercises,
                            onExerciseRemove = { exerciseId ->
                                selectedExerciseIds = selectedExerciseIds - exerciseId
                            }
                        )

                        1 -> ExerciseSelectionTab(
                            availableExercises = availableExercises,
                            selectedExerciseIds = selectedExerciseIds,
                            onExerciseSelected = { exercise ->
                                selectedExerciseIds =
                                    if (!selectedExerciseIds.contains(exercise.id)) {
                                        selectedExerciseIds + exercise.id
                                    } else {
                                        selectedExerciseIds - exercise.id
                                    }
                            },
                            isAddMode = false
                        )
                    }
                }

                if (selectedTab == 0) {
                    NewCustomButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        onClick = {
                            viewModel.createRoutine(
                                name,
                                goal,
                                description,
                                selectedExerciseIds,
                                targetMuscles
                            )
                        },
                        text = stringResource(R.string.create_routine),
                        buttonType = ButtonType.ELEVATED,
                        containerColor = Color.Blue,
                        textConfig = buttonConfigs.textConfig.copy(textColor = Color.White),
                        layoutConfig = buttonConfigs.layoutConfig,
                        stateConfig = buttonConfigs.stateConfig,
                        borderConfig = buttonConfigs.borderConfig
                    )
                }
            }
        }
    )
}