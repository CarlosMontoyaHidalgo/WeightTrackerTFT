package com.aronid.weighttrackertft.ui.screens.exercises.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.exercise.details.ExerciseDetailsContent

@Composable
fun ExerciseDetailsScreen(
    innerPadding: PaddingValues,
    exerciseId: String?,
    viewModel: ExerciseDetailsViewModel,
    navHostController: NavHostController
) {

    val exercise by viewModel.exercise.collectAsState()
    val primaryMuscleName by viewModel.primaryMuscleName.collectAsState()
    val secondaryMuscleNames by viewModel.secondaryMuscleNames.collectAsState()

    LaunchedEffect(exerciseId) {
        exerciseId?.let { id -> viewModel.loadExerciseDetails(exerciseId = id) }
    }
    Column(modifier = Modifier.padding(innerPadding)) {
        Scaffold(
            topBar = {
                /*MyTopNavigationBar(
                    title = "${exercise?.name}",
                    onBackClick = { navHostController.popBackStack() },
                    backgroundColor = Color.Transparent,
                    contentColor = Color.Black,
                )*/
            },
        ) { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ExerciseDetailsContent(
                    exercise = exercise,
                    primaryMuscleName = primaryMuscleName,
                    secondaryMuscleNames = secondaryMuscleNames,
                    innerPadding = innerPadding
                )
                BackButton(navHostController)
            }
        }
    }
}