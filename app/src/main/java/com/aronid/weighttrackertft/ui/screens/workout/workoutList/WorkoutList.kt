package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.calendar.SimpleDateFilterDialog
import com.aronid.weighttrackertft.ui.components.cards.CustomWorkoutCard.WorkoutCard
import com.aronid.weighttrackertft.ui.components.searchBar.workouts.WorkoutTopBar

@Composable
fun WorkoutList(innerPadding: PaddingValues, navHostController: NavHostController) {
    val workoutListViewModel: WorkoutListViewModel = hiltViewModel()
    val workouts = workoutListViewModel.workouts.collectAsLazyPagingItems()
    var showCheckbox by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val selectedWorkouts = remember { mutableStateMapOf<String, Boolean>() }
    val context = LocalContext.current

    LaunchedEffect(workouts) {
        workouts.itemSnapshotList.forEach { workout ->
            workout?.let {
                if (!selectedWorkouts.containsKey(it.id)) {
                    selectedWorkouts[it.id] = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            WorkoutTopBar(
                selectedWorkouts = selectedWorkouts,
                onEditClick = { showCheckbox = !showCheckbox },
                onDeleteClick = { showAlertDialog = true },
                onFilterClick = { showFilterDialog = true },
                onClearFilterClick = { workoutListViewModel.clearDateFilter() },
                showCheckbox = showCheckbox
            )

                            if (showAlertDialog) {
                    CustomAlertDialog(
                        showDialog = showAlertDialog,
                        onDismiss = { showAlertDialog = false },
                        onConfirm = {
                            val toDelete = selectedWorkouts.filter { it.value }.keys.toList()
                            workoutListViewModel.deleteSelectedWorkouts { success ->
                                if (success) {
                                    Toast.makeText(context, "¡Entrenamientos eliminados!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                }
                            }
                            showAlertDialog = false
                            showCheckbox = false
                            selectedWorkouts.clear()
                            workouts.refresh()
                        },
                        title = "¿Estás seguro de eliminar estos workouts?",
                        text = "No podrás recuperarlos",
                        confirmButtonText = stringResource(R.string.alert_positive),
                        dismissButtonText = stringResource(R.string.alert_negative)
                    )
                }

                if (showFilterDialog) {
                    SimpleDateFilterDialog(
                        onDateRangeSelected = { startDate, endDate ->
                            workoutListViewModel.setDateFilter(startDate, endDate)
                        },
                        onDismiss = { showFilterDialog = false }
                    )
                }
        },
        modifier = Modifier.padding(innerPadding)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize() // Ocupa todo el espacio disponible
        ) {
            // Lista que ocupa toda la altura
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (workouts.itemCount == 0) {
                    item {
                        Text(
                            text = "No hay workouts disponibles",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                } else {
                    items(workouts.itemCount) { index ->
                        workouts[index]?.let { workout ->
                            WorkoutCard(
                                workout = workout,
                                showCheckbox = showCheckbox,
                                isChecked = selectedWorkouts[workout.id] ?: false,
                                onCheckedChange = { checked ->
                                    selectedWorkouts[workout.id] = checked
                                    workoutListViewModel.toggleSelection(workout.id, checked)
                                },
                                navHostController = navHostController
                            )
                        }
                    }
                }
            }

            // Botón de "Volver atrás" superpuesto
            Button(
                onClick = { navHostController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .shadow(4.dp, CircleShape),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_go_back),
                    contentDescription = "Volver atrás",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}