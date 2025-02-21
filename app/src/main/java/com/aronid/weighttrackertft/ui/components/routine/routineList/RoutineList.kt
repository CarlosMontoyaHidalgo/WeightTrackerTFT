package com.aronid.weighttrackertft.ui.components.routine.routineList

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.ui.components.routine.routineItem.RoutineItem

@Composable
fun RoutineList(routines: List<RoutineModel>, navHostController: NavHostController) {
    LazyColumn {
        items(routines) { routine ->
            RoutineItem(routine, navHostController)
        }
    }
}