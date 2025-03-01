package com.aronid.weighttrackertft.ui.screens.exercises

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.FloatingActionButton.MyFloatingActionButton
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.components.searchBar.muscle.MuscleSearchBar
import kotlinx.coroutines.launch

@Composable
fun ExerciseScreen(
    innerPadding: PaddingValues,
    viewModel: ExerciseViewModel,
    navHostController: NavHostController
) {

    val exercises by viewModel.exercises.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navHostController = navHostController)
        },
        floatingActionButton = {
            MyFloatingActionButton(
                onClick = {
                    navHostController.navigate(NavigationRoutes.CreateRoutine.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MuscleSearchBar(
                exercises,
                { exercise ->
                    navHostController.navigate(NavigationRoutes.ExerciseDetails.createRoute(exercise.id))
                },
                selectedExerciseIds = emptyList(),
                Modifier
            )
        }
    }
}