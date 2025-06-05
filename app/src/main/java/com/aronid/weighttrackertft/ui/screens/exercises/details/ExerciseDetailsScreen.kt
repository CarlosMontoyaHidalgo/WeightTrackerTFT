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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.MyTopNavigationBar
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.exercise.details.ExerciseDetailsContent
import com.aronid.weighttrackertft.utils.Translations

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
    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        Scaffold(
            topBar = {
                MyTopNavigationBar(
                    title = Translations.translateAndFormat(
                        exercise?.name ?: stringResource(R.string.exercise),
                        Translations.exerciseTranslations
                    ),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.Black,
                )
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