package com.aronid.weighttrackertft.ui.screens.workout

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.exercise.exerciseProgressIndicator.ExerciseProgressIndicator
import com.aronid.weighttrackertft.ui.components.series.row.SeriesRow
import com.aronid.weighttrackertft.ui.components.timer.WorkoutTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Composable
fun WorkoutScreen(
    innerPadding: PaddingValues,
    routineId: String?,
    isPredefined: Boolean,
    navHostController: NavHostController,
    viewModel: WorkoutViewModel // Inyectado con Hilt
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
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize().padding()) {
        val (headerRef, seriesRef, timerRef ,buttonsRef) = createRefs()
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
                        Log.d("WorkoutScreen", "Navigating with workout ID: ${workout.id}")
                        navHostController.navigate(NavigationRoutes.WorkoutSummary.createRoute(workout.id))
                    }
                },
                title = "¿Finalizar entrenamiento?",
                text = "¿Estás seguro de que quieres finalizar el entrenamiento?",
                confirmButtonText = "Finalizar",
                dismissButtonText = "Cancelar"
            )
        }

        if (showDialog) {
            CustomAlertDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = {},
                title = currentExercise?.exerciseName ?: "",
                text = "${currentExercise?.description}",
                imageUrl = (currentExercise?.imageUrl ?: R.drawable.background).toString(),
                confirmButtonText = "",
                dismissButtonText = "Cerrar"
            )
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                        text = ex.exerciseName ?: "Ejercicio",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "info",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onInfoClick() }
                    )
                } ?: CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        navigationIcon = {
//            IconButton(onClick = onBackClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_back),
//                    contentDescription = "Volver"
//                )
//            }
        },
        actions = {
            Button(
                onClick = onFinishClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Finalizar")
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
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Atrás",
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
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Siguiente",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
