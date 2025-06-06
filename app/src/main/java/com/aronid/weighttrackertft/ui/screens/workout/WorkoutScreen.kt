package com.aronid.weighttrackertft.ui.screens.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.exercise.exerciseProgressIndicator.ExerciseProgressIndicator
import com.aronid.weighttrackertft.ui.components.series.row.SeriesRow
import com.aronid.weighttrackertft.ui.components.timer.WorkoutTimer
import com.aronid.weighttrackertft.ui.screens.chatbot.ChatbotViewModel
import com.aronid.weighttrackertft.ui.screens.routines.createRoutine.CreateRoutineViewModel
import com.aronid.weighttrackertft.utils.Translations
import com.aronid.weighttrackertft.utils.Translations.exerciseTranslations
import com.aronid.weighttrackertft.utils.Translations.translateAndFormat
import com.aronid.weighttrackertft.utils.getExerciseId
import com.aronid.weighttrackertft.utils.getExerciseImageResource
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun WorkoutScreen(
    innerPadding: PaddingValues,
    routineId: String?,
    isPredefined: Boolean,
    navHostController: NavHostController,
    viewModel: WorkoutViewModel,
    chatbotViewModel: ChatbotViewModel = hiltViewModel(), // Added
    createRoutineViewModel: CreateRoutineViewModel = hiltViewModel() // Added
) {
    val exercises by viewModel.exercisesWithSeries.collectAsState()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val currentExercise = exercises.getOrNull(currentExerciseIndex)
    var showDialog by remember { mutableStateOf(false) }
    var finishConfirmation by remember { mutableStateOf(false) }
    val workoutDuration by viewModel.workoutDuration.collectAsState()

    LaunchedEffect(routineId) {
        routineId?.let { id -> viewModel.loadRoutineExercises(id, isPredefined) }
        currentExercise?.let { exercise ->
            Log.d("WorkoutScreen", "Exercise: ${exercise.instructions}")
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        val (headerRef, seriesRef, timerRef, buttonsRef, fabRef) = createRefs()

        WorkoutHeader(
            currentExercise = currentExercise,
            onFinishClick = { finishConfirmation = true },
            onInfoClick = { showDialog = true },
            modifier = Modifier.constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        if (finishConfirmation) {
            CustomAlertDialog(
                showDialog = finishConfirmation,
                onDismiss = { finishConfirmation = false },
                onConfirm = {
                    finishConfirmation = false
                    viewModel.viewModelScope.launch {
                        val workout = viewModel.saveWorkoutData()
                        navHostController.navigate(
                            NavigationRoutes.WorkoutSummary.createRoute(
                                workout.id
                            )
                        )
                    }
                },
                title = stringResource(R.string.finish_workout),
                text = stringResource(R.string.finish_workout_confirmation),
                confirmButtonText = stringResource(R.string.save),
                dismissButtonText = stringResource(R.string.cancel)
            )
        }

        if (showDialog) {
            CustomAlertDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = {},
                title = translateAndFormat(
                    currentExercise?.exerciseName ?: "",
                    exerciseTranslations
                ),
                text = "",
                imageUrl = (currentExercise?.imageUrl ?: R.drawable.background).toString(),
                customContent = {
                    ExerciseInfo(currentExercise = currentExercise)
                },
                imageResId = currentExercise?.exerciseName?.let {
                    getExerciseImageResource(getExerciseId(it))
                } ?: R.drawable.background,
                confirmButtonText = "",
                dismissButtonText = stringResource(R.string.close)
            )
        }
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .constrainAs(seriesRef) {
                        top.linkTo(headerRef.bottom, margin = 32.dp)
                        bottom.linkTo(buttonsRef.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                if (currentExercise == null) {
                    item { CircularProgressIndicator() }
                } else {
                    items(currentExercise.series.size) { index ->
                        SeriesRow(
                            seriesNumber = currentExercise.series[index].setNumber,
                            weight = currentExercise.series[index].weight ?: "",
                            reps = currentExercise.series[index].reps.toString(),
                            isCompleted = currentExercise.series[index].isDone,
                            requiresWeight = currentExercise.requiresWeight,
                            onWeightChange = { viewModel.updateSeriesWeight(index, it) },
                            onRepsChange = {
                                val repsValue = it.toIntOrNull() ?: 0
                                viewModel.updateSeriesReps(index, repsValue.toString())
                            },
                            onToggleCompleted = { viewModel.toggleSeriesCompletion(index) }
                        )
                        if (index < currentExercise.series.lastIndex && !currentExercise.series[index].isDone) {
                            VerticalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            WorkoutTimer(
                durationInSeconds = workoutDuration / 1000,
                modifier = Modifier.constrainAs(timerRef) {
                    bottom.linkTo(buttonsRef.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            WorkoutNavigationButtons(
                exercises = exercises,
                currentExerciseIndex = currentExerciseIndex,
                onPreviousClick = { viewModel.navigateToPreviousExercise() },
                onNextClick = { viewModel.navigateToNextExercise() },
                modifier = Modifier
                    .constrainAs(buttonsRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHeader(
    currentExercise: ExerciseWithSeries?,
    onFinishClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentExercise?.let { ex ->
                    Text(
                        text = ex.exerciseName?.let {
                            Translations.translateAndFormat(
                                it,
                                Translations.exerciseTranslations
                            )
                        } ?: stringResource(R.string.exercise),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f) // Constrain text width
                            .padding(end = 8.dp) // Space between text and info button
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = stringResource(id = R.string.info),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onInfoClick() }
                    )
                } ?: CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(8.dp)) // Add space before the Save button
            Button(
                onClick = onFinishClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(end = 8.dp) // Additional padding to shift left
            ) {
                Text(stringResource(R.string.save))
            }
        },
        modifier = modifier
    )
}

@Composable
fun WorkoutNavigationButtons(
    exercises: List<ExerciseWithSeries>,
    currentExerciseIndex: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousClick,
            enabled = currentExerciseIndex > 0,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        ExerciseProgressIndicator(
            totalExercises = exercises.size,
            currentExerciseIndex = currentExerciseIndex,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .height(24.dp)
        )

        IconButton(
            onClick = onNextClick,
            enabled = currentExerciseIndex < exercises.size - 1,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = stringResource(R.string.next),
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseInfo(currentExercise: ExerciseWithSeries?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        currentExercise?.let { exercise ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Músculo principal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = Translations.translateAndFormat(
                                "Primary Muscle",
                                Translations.uiStrings
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        ChipComponent(
                            text = Translations.translateAndFormat(
                                exercise.primaryMuscleName ?: "No especificado",
                                Translations.muscleTranslations
                            ),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }

                    // Músculos secundarios
                    if (exercise.secondaryMuscleNames.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = Translations.translateAndFormat(
                                    "Secondary Muscles",
                                    Translations.uiStrings
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                exercise.secondaryMuscleNames.forEach { muscle ->
                                    ChipComponent(
                                        text = Translations.translateAndFormat(
                                            muscle,
                                            Translations.muscleTranslations
                                        ),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val language = Locale.getDefault().language
                        val instructionText = currentExercise?.instructions?.get(language)
                            ?: currentExercise?.instructions?.get("es")
                            ?: ""

                        Text(
                            text = Translations.translateAndFormat(
                                "Intrucciones",
                                Translations.uiStrings
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Text(
                            text = instructionText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                }
            }
        } ?: item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Selecciona un ejercicio",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}


@Composable
private fun ChipComponent(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}