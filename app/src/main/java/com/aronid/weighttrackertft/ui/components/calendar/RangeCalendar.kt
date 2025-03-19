package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.cards.CustomWorkoutCard.WorkoutCard
import com.aronid.weighttrackertft.ui.components.searchBar.workouts.WorkoutTopBar

//@Composable
//fun WorkoutList(innerPadding: PaddingValues, navHostController: NavHostController) {
//    val workoutListViewModel: WorkoutListViewModel = hiltViewModel()
//    val workouts = workoutListViewModel.workouts.collectAsLazyPagingItems()
//    var showCheckbox by remember { mutableStateOf(false) }
//    var showAlertDialog by remember { mutableStateOf(false) }
//    var showFilterDialog by remember { mutableStateOf(false) }
//    val selectedWorkouts = remember { mutableStateMapOf<String, Boolean>() }
//
//    LaunchedEffect(workouts) {
//        workouts.itemSnapshotList.forEach { workout ->
//            workout?.let {
//                if (!selectedWorkouts.containsKey(it.id)) {
//                    selectedWorkouts[it.id] = false
//                }
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            WorkoutTopBar(
//                selectedWorkouts = selectedWorkouts,
//                onEditClick = { showCheckbox = !showCheckbox },
//                onDeleteClick = { showAlertDialog = true },
//                onFilterClick = { showFilterDialog = true },
//                onClearFilterClick = { workoutListViewModel.clearDateFilter() },
//                showCheckbox = showCheckbox
//            )
//        },
//        modifier = Modifier.padding(innerPadding)
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize() // Ocupa todo el espacio disponible
//        ) {
//            // Lista que ocupa toda la altura
//            LazyColumn(
//                modifier = Modifier.fillMaxSize()
//            ) {
//                if (workouts.itemCount == 0) {
//                    item {
//                        Text(
//                            text = "No hay workouts disponibles",
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.fillMaxWidth().padding(16.dp)
//                        )
//                    }
//                } else {
//                    items(workouts.itemCount) { index ->
//                        workouts[index]?.let { workout ->
//                            WorkoutCard(
//                                workout = workout,
//                                showCheckbox = showCheckbox,
//                                isChecked = selectedWorkouts[workout.id] ?: false,
//                                onCheckedChange = { checked ->
//                                    selectedWorkouts[workout.id] = checked
//                                    workoutListViewModel.toggleSelection(workout.id, checked)
//                                },
//                                navHostController = navHostController
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Botón de "Volver atrás" superpuesto
//            Button(
//                onClick = { navHostController.popBackStack() },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onPrimary
//                ),
//                modifier = Modifier
//                    .align(Alignment.BottomCenter) // Posicionado en la parte inferior
//                    .padding(16.dp) // Espacio desde el borde
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_go_back),
//                    contentDescription = "Volver atrás",
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }
//        }
//    }
//}


//            ConstraintLayout(Modifier.fillMaxWidth()) {
//                val (titleRef, editRef, deleteRef, filterRef, clearFilterRef) = createRefs()
//
//                Text(
//                    text = "Workouts",
//                    textAlign = TextAlign.Start, // Alineado a la izquierda por defecto
//                    modifier = Modifier
//                        .constrainAs(titleRef) {
//                            start.linkTo(parent.start, margin = 16.dp)
//                            top.linkTo(parent.top)
//                            bottom.linkTo(parent.bottom)
//                            end.linkTo(deleteRef.start, margin = 8.dp) // Limita el espacio hacia los botones
//                        }
//                        .padding(end = 8.dp)
//                )
//
//                if (showCheckbox) {
//                    Button(
//                        onClick = { showAlertDialog = true },
//                        enabled = selectedWorkouts.values.any { it },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary,
//                            contentColor = MaterialTheme.colorScheme.onPrimary,
//                            disabledContainerColor = Color(0xFFFFCDD2),
//                            disabledContentColor = Color.Gray
//                        ),
//                        modifier = Modifier.constrainAs(deleteRef) {
//                            end.linkTo(editRef.start, margin = 4.dp) // Reducido de default a 4dp
//                            top.linkTo(parent.top)
//                            bottom.linkTo(parent.bottom)
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_trash),
//                            contentDescription = "Eliminar",
//                            tint = if (selectedWorkouts.values.any { it }) Color.Red else Color.Gray
//                        )
//                    }
//                }
//
//                Button(
//                    onClick = {
//                        showCheckbox = !showCheckbox
//                        Log.d("WorkoutList", "Checkbox state toggled: $showCheckbox")
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    ),
//                    modifier = Modifier.constrainAs(editRef) {
//                        end.linkTo(filterRef.start, margin = 4.dp) // Reducido de default a 4dp
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    }
//                ) {
//                    Icon(
//                        imageVector = if (showCheckbox) Icons.Default.Done else Icons.Default.Edit,
//                        contentDescription = "Edit",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                Button(
//                    onClick = { showFilterDialog = true },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    ),
//                    modifier = Modifier.constrainAs(filterRef) {
//                        end.linkTo(clearFilterRef.start, margin = 4.dp) // Reducido de default a 4dp
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.CalendarToday,
//                        contentDescription = "Filtrar por fecha",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                Button(
//                    onClick = {
//                        workoutListViewModel.clearDateFilter()
//                        Log.d("WorkoutList", "Date filter cleared")
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    ),
//                    modifier = Modifier.constrainAs(clearFilterRef) {
//                        end.linkTo(parent.end, margin = 16.dp)
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Clear,
//                        contentDescription = "Borrar filtro",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                if (showAlertDialog) {
//                    CustomAlertDialog(
//                        showDialog = showAlertDialog,
//                        onDismiss = { showAlertDialog = false },
//                        onConfirm = {
//                            val toDelete = selectedWorkouts.filter { it.value }.keys.toList()
//                            Log.d("WorkoutList", "Confirming deletion of workouts: $toDelete")
//                            workoutListViewModel.deleteSelectedWorkouts()
//                            showAlertDialog = false
//                            showCheckbox = false
//                            selectedWorkouts.clear()
//                            Log.d("WorkoutList", "Refreshing workouts after delete")
//                            workouts.refresh()
//                        },
//                        title = "¿Estás seguro de eliminar estos workouts?",
//                        text = "No podrás recuperarlos",
//                        confirmButtonText = stringResource(R.string.alert_positive),
//                        dismissButtonText = stringResource(R.string.alert_negative)
//                    )
//                }
//
//                if (showFilterDialog) {
//                    SimpleDateFilterDialog(
//                        onDateRangeSelected = { startDate, endDate ->
//                            workoutListViewModel.setDateFilter(startDate, endDate)
//                        },
//                        onDismiss = { showFilterDialog = false }
//                    )
//                }
//            }