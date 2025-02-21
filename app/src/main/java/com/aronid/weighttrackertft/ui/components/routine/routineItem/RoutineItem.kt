package com.aronid.weighttrackertft.ui.components.routine.routineItem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes

@Composable
fun RoutineItem(routine: RoutineModel, navHostController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navHostController.navigate(NavigationRoutes.RoutineDetails.createRoute(routine.id))
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = routine.name)
            Text(text = routine.goal)
        }
    }
}