package com.aronid.weighttrackertft.ui.screens.routines.createRoutine

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.searchBar.muscle.MuscleSearchBar
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

    val navResult = navHostController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<List<String>>("selectedExerciseIds")

    val routineCreatedMessage = stringResource(id = R.string.routine_created)

    LaunchedEffect(navResult) {
        navResult?.value?.let {
            selectedExerciseIds = it
            navHostController.currentBackStackEntry?.savedStateHandle?.remove<List<String>>("selectedExerciseIds")
        }
    }

    LaunchedEffect(name, goal, description, selectedExerciseIds, targetMuscles) {
        viewModel.checkFormValidity(name, goal, description, selectedExerciseIds, targetMuscles)
    }

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
                            onMuscleSelected = { targetMuscles = it }
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
                            }
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

@Composable
private fun RoutineDetailsTab(
    name: String,
    goal: String,
    description: String,
    targetMuscles: List<String>,
    availableMuscles: List<String>,
    onNameChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMuscleSelected: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

        Text(
            text = stringResource(R.string.target_muscles),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        MuscleGrid(
            muscles = availableMuscles,
            selectedMuscles = targetMuscles,
            onSelectionChanged = onMuscleSelected
        )
    }
}

@Composable
private fun ExerciseSelectionTab(
    availableExercises: List<ExerciseModel>,
    selectedExerciseIds: List<String>,
    onExerciseSelected: (ExerciseModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.selected_exercises),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        MuscleSearchBar(
            exercises = availableExercises,
            selectedExerciseIds = selectedExerciseIds,
            onExerciseSelected = onExerciseSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }
}

@Composable
private fun MuscleGrid(
    muscles: List<String>,
    selectedMuscles: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(muscles) { muscle ->
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
                label = { Text(muscle) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}