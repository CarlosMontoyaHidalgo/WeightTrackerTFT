package com.aronid.weighttrackertft.ui.screens.routines.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.routine.routineDetails.RoutineDetailsContent

@Composable
fun RoutineDetailsScreen(
    innerPadding: PaddingValues,
    routineId: String?,
    isPredefined: Boolean,
    navHostController: NavHostController,
    viewModel: RoutineDetailsViewModel
) {
    val routine by viewModel.routine.collectAsState()
    val exercises by viewModel.exercises.collectAsState()

    Column(modifier = Modifier.padding(innerPadding)) {
        LaunchedEffect(routineId) {
            routineId?.let { id -> viewModel.loadRoutineDetails(routineId = id, isPredefined = isPredefined) }
        }

        RoutineDetailsContent(routine = routine, exercises = exercises, navHostController = navHostController, innerPadding, isPredefined)

    }

}



