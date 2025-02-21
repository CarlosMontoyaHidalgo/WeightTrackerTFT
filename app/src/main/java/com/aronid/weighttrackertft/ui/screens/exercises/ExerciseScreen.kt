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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.components.BottomNavigationBar.EditRoutineBottomBar
import com.aronid.weighttrackertft.ui.components.FloatingActionButton.MyFloatingActionButton
import com.aronid.weighttrackertft.ui.components.searchBar.MySearchBar
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

    val selectedExercises = remember { mutableStateListOf<ExerciseModel>() }

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
            MySearchBar(exercises, {}, selectedExerciseIds = emptyList(), Modifier)
        }
    }
}