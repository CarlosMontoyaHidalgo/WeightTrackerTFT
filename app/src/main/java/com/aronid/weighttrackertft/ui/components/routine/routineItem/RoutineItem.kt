package com.aronid.weighttrackertft.ui.components.routine.routineItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.screens.routines.RoutineViewModel

@Composable
fun RoutineItem(
    routine: RoutineModel,
    navHostController: NavHostController,
    isPredefined: Boolean,
    isChecked: Boolean = false,
    showCheckbox: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val viewModel: RoutineViewModel = hiltViewModel()

    val isChecked by remember { derivedStateOf {
        viewModel.selectedRoutines.containsKey(routine.id)
    }}
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                val route = NavigationRoutes.RoutineDetails.createRoute(routine.id, isPredefined)
                println("Route: $route")
                navHostController.navigate(route)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showCheckbox && !isPredefined) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked -> viewModel.toggleSelection(routine.id, checked) },
                    modifier = Modifier
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = routine.name)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = routine.goal)
            }

            Row {
                // Botón para iniciar entrenamiento
                if (!showCheckbox){
                    IconButton(
                        onClick = {
                            navHostController.navigate(
                                NavigationRoutes.Workout.createRoute(routine.id, isPredefined)
                            )
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Start workout"
                        )
                    }
                }

                // Botón de edición solo para rutinas personalizadas
                if (!isPredefined) {
                    IconButton(
                        onClick = {
                            navHostController.navigate(NavigationRoutes.EditRoutine.createRoute(routine.id))
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit routine"
                        )
                    }
                }
            }
        }
    }
}