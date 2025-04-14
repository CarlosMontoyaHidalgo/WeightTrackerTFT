package com.aronid.weighttrackertft.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.WeeklyWorkoutCalendar
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    calendarViewModel: CalendarViewModel
) {
    val workouts by viewModel.weeklyWorkouts.collectAsState()
    val name by viewModel.userName.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedWorkouts by rememberSaveable { mutableStateOf<List<WorkoutModel>>(emptyList()) }

    Scaffold(
        topBar = {
            Text(
                text = stringResource(R.string.welcome, name),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            )
        },
        bottomBar = {
            BottomNavigationBar(navHostController = navHostController)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(paddingValues)
        ) {
            ElevatedCard(
                onClick = { /* Navegar al calendario grande */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                WeeklyWorkoutCalendar(
                    workouts = workouts,
                    onDayClick = { date, dayWorkouts ->
                        selectedDate = date
                        selectedWorkouts = dayWorkouts
                        showDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Entrenamientos favoritos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (favorites.isEmpty()) {
                Text(
                    text = "No tienes rutinas favoritas aún",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
                Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                    Text(text = "Ver todas las rutinas")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(favorites) { favorite ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    navHostController.navigate(
                                        NavigationRoutes.RoutineDetails.createRoute(
                                            favorite.routineId,
                                            favorite.isPredefined
                                        )
                                    )
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = favorite.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = if (favorite.isPredefined) "Predefinida" else "Personalizada",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                Text(text = "Ver todas las rutinas")
            }

//            Button(onClick = { navHostController.navigate(NavigationRoutes.Exercises.route) }) {
//                Text(text = "Ver ejercicios")
//            }

            Button(onClick = { navHostController.navigate(NavigationRoutes.Example.route) }) {
                Text(text = "Ver ejemplo")
            }

            if (showDialog && selectedDate != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = stringResource(
                                id = R.string.workouts_of,
                                selectedDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            )
                        )
                    },
                    text = {
                        if (selectedWorkouts.isEmpty()) {
                            Text(stringResource(R.string.no_workouts_for_day))
                        } else if (selectedWorkouts.size == 1) {
                            val workout = selectedWorkouts.first()
                            Text(
                                text = stringResource(
                                    id = R.string.single_workout_details,
                                    workout.calories.toString(),
                                    workout.volume.toString(),
                                    workout.workoutType,
                                    workout.intensity.toString(),
                                    workout.date?.toDate()?.toString() ?: stringResource(R.string.not_available)
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = selectedWorkouts.mapIndexed { index, workout ->
                                    val workoutIndex = index + 1
                                    val dateString = workout.date?.toDate()?.toString() ?: stringResource(id = R.string.not_available)
                                    stringResource(
                                        id = R.string.workout_details,
                                        workoutIndex,
                                        workout.calories.toString(),
                                        workout.volume.toString(),
                                        workout.workoutType,
                                        workout.intensity.toString(),
                                        dateString
                                    )
                                }.joinToString("\n"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.close))
                        }
                    }
                )
            }
        }
    }
}