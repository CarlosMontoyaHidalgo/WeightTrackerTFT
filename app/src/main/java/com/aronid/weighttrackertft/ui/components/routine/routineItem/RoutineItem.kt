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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    isPredefined: Boolean? = null,
    isChecked: Boolean = false,
    showCheckbox: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val viewModel: RoutineViewModel = hiltViewModel()
    val isFavorite by viewModel.favoriteRoutines.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                val route = NavigationRoutes.RoutineDetails.createRoute(
                    routine.id,
                    isPredefined == true
                )
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

            Column(modifier = Modifier.weight(1f)) {
                Text(text = routine.name)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = routine.goal)
            }
            if (showCheckbox && !isPredefined!!) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier
                )
            }
            if (!showCheckbox) {
                Row {
                    IconButton(onClick = {
                        viewModel.toggleFavoriteOptimistic(
                            routine.id,
                            isPredefined ?: false
                        )
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (routine.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border
                            ),
                            contentDescription = "Toggle favorite",
                            tint = Color(0xFFFFD700)
                        )
                    }

                    // Botón para iniciar entrenamiento
                    IconButton(
                        onClick = {
                            navHostController.navigate(
                                NavigationRoutes.Workout.createRoute(
                                    routine.id,
                                    isPredefined == true
                                )
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
                isPredefined?.let {
                    if (!it) {
                        IconButton(
                            onClick = {
                                navHostController.navigate(
                                    NavigationRoutes.EditRoutine.createRoute(
                                        routine.id
                                    )
                                )
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
}