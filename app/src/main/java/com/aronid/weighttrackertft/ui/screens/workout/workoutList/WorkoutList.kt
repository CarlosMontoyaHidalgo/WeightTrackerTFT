package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.SimpleDateFilterDialog
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.cards.CustomWorkoutCard.WorkoutCard
import com.aronid.weighttrackertft.ui.components.searchBar.workouts.WorkoutTopBar

@Composable
fun WorkoutList(innerPadding: PaddingValues, navHostController: NavHostController) {
    val workoutListViewModel: WorkoutListViewModel = hiltViewModel()
    val workouts = workoutListViewModel.workouts.collectAsLazyPagingItems()
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    var showCheckbox by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val selectedWorkouts = remember { mutableStateMapOf<String, Boolean>() }
    val context = LocalContext.current

    val workoutsDeletedMessage = stringResource(id = R.string.workouts_deleted)
    val deleteErrorMessage = stringResource(id = R.string.delete_error)

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
                                Toast.makeText(
                                    context,
                                    workoutsDeletedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, deleteErrorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        showAlertDialog = false
                        showCheckbox = false
                        selectedWorkouts.clear()
                        workouts.refresh()
                    },
                    title = stringResource(id = R.string.delete_workouts_confirmation_title),
                    text = stringResource(id = R.string.delete_workouts_confirmation_message),
                    confirmButtonText = stringResource(R.string.alert_positive),
                    dismissButtonText = stringResource(R.string.alert_negative)
                )
            }

            if (showFilterDialog) {
//                SimpleDateFilterDialog(
//                    onDateRangeSelected = { startDate, endDate ->
//                        workoutListViewModel.setDateFilter(startDate, endDate)
//                    },
//                    onDismiss = { showFilterDialog = false }
//                )

                WorkoutRangeCalendar(
                    viewModel = calendarViewModel,
                    onDateRangeSelected = { startDate, endDate ->
                        workoutListViewModel.setDateFilter(startDate, endDate)
                    },
                    onDismiss = { showFilterDialog = false }
                )
            }
        },
        modifier = Modifier.padding(innerPadding)
            .navigationBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize() // Ocupa todo el espacio disponible
        ) {
            // Lista que ocupa toda la altura
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (workouts.itemCount == 0) {
                    item {
                        Text(
                            text = stringResource(R.string.no_workouts_available),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
                                navHostController = navHostController,
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
                    .padding(bottom = 8.dp)
                    .shadow(4.dp, CircleShape),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_go_back),
                    contentDescription = stringResource(R.string.go_back),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}