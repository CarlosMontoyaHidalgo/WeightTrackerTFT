package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
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
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("") }
    val selectedWorkoutsExport = workoutListViewModel.selectedWorkouts
    val selectedWorkouts = remember { mutableStateMapOf<String, Boolean>() }
    val context = LocalContext.current

    val workoutsDeletedMessage = stringResource(id = R.string.workouts_deleted)
    val deleteErrorMessage = stringResource(id = R.string.delete_error)
    val exportSuccessMessage = stringResource(id = R.string.export_success) // Add to strings.xml
    val exportErrorMessage = stringResource(id = R.string.export_error) // Add to strings.xml

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument()
    ) { uri ->
        if (uri != null) {
            val selectedIds =
                workoutListViewModel.selectedWorkouts.filter { it.value }.keys.toList()
            when (exportFormat) {
                "csv" -> workoutListViewModel.exportSelectedWorkoutsAsCsv(
                    context,
                    uri,
                    selectedIds
                ) { success ->
                    Toast.makeText(
                        context,
                        if (success) "CSV exportado correctamente" else "Error al exportar",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                "pdf" -> workoutListViewModel.exportSelectedWorkoutsAsPdf(
                    context,
                    uri,
                    selectedIds
                ) { success ->
                    Toast.makeText(
                        context,
                        if (success) "PDF exportado correctamente" else "Error al exportar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

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
                onDeleteClick = {
                    showAlertDialog = true

                },
                onFilterClick = { showFilterDialog = true },
                onClearFilterClick = { workoutListViewModel.clearDateFilter() },
                showCheckbox = showCheckbox,
                onDownloadClick = {
                    showExportDialog = true
                }

            )

            if (showExportDialog) {
                CustomAlertDialog(
                    showDialog = showExportDialog,
                    onDismiss = { showExportDialog = false },
                    onConfirm = {},
                    title = stringResource(R.string.export_dialog_title),
                    text = stringResource(R.string.export_dialog_message),
                    confirmButtonText = "",
                    dismissButtonText = "",
                    customContent = {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    exportFormat = "csv"
                                    exportLauncher.launch("workouts_${System.currentTimeMillis()}.csv")
                                    showExportDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.export_csv))
                            }

                            Button(
                                onClick = {
                                    exportFormat = "pdf"
                                    exportLauncher.launch("workouts_${System.currentTimeMillis()}.pdf")
                                    showExportDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.export_pdf))
                            }

                            OutlinedButton(
                                onClick = { showExportDialog = false },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }
                )
            }


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
                WorkoutRangeCalendar(
                    viewModel = calendarViewModel,
                    onDateRangeSelected = { startDate, endDate ->
                        workoutListViewModel.setDateFilter(startDate, endDate)
                    },
                    onDismiss = { showFilterDialog = false }
                )
            }
        },
        modifier = Modifier
            .padding(innerPadding)
            .navigationBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
        ) {
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
                                onCardClick = {
                                    navHostController.navigate(
                                        NavigationRoutes.WorkoutSummary.createRoute(
                                            workout.id
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
            BackButton(navHostController)
        }
    }
}