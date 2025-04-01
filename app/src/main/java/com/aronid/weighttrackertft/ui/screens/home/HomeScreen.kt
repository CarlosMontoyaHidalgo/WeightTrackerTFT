package com.aronid.weighttrackertft.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.alertDialog.BottomSheetDialog
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.WeeklyWorkoutCalendar
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    viewModel: HomeViewModel,
    calendarViewModel: CalendarViewModel
) {
    val workouts = viewModel.weeklyWorkouts.collectAsState()
    val name = viewModel.userName.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedWorkouts by rememberSaveable { mutableStateOf<List<WorkoutModel>>(emptyList()) }

    Scaffold(
        topBar = {
            Text(
                text = stringResource(R.string.welcome, name.value),
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
                onClick = { /* A la derecha tendra una flecha que te llevara al calendario grande */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                WeeklyWorkoutCalendar(
                    workouts = workouts.value,
                    onDayClick = { date, dayWorkouts ->
                        selectedDate = date
                        selectedWorkouts = dayWorkouts
                        showDialog = true
                    }
                )
            }
//            SimpleDonutChart(listOf("back", "chest", "legs", "arms"))
//            PreviewDonutChart()
//            SimpleDonutChart()

            Text(
                text = stringResource(id = R.string.home),
                color = MaterialTheme.colorScheme.onBackground
            )

//            BottomSheetDialog()
//            Button(onClick = { navHostController.navigate(NavigationRoutes.PhysicalData.route) }) {
//                Text(text = "Go to questionnaire")
//            }
//
            Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                Text(text = "see routines")
            }
//            Button(onClick = { navHostController.navigate(NavigationRoutes.EditRoutine.route) }) {
//                Text(text = "editar routine")
//            }
//
//            Button(onClick = { navHostController.navigate(NavigationRoutes.WorkoutList.route) }) {
//                Text(text = "Lista de rutinas")
//            }

//            val viewModel: CalendarViewModel = hiltViewModel()
//            WorkoutRangeCalendar(viewModel)

//            MyCalendar()

//            Button(onClick = {navHostController.navigate(NavigationRoutes.CreateRoutine.route)}) {
//                Text(text = "Create a routine")
//            }
            Button(onClick = {navHostController.navigate(NavigationRoutes.Exercises.route)}) {
                Text(text = "Ver ejercicios")
            }

            Text(text = "Entrenamientos favoritos")
            Button(onClick = {navHostController.navigate(NavigationRoutes.FavoriteExercises.route)}) {
                Text(text = "Ver favoritos")
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
                            // Caso 1: Un solo entrenamiento
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
                    })
            }

        }
    }

}
