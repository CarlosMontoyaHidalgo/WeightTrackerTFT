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
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.WeeklyWorkoutCalendar
import com.aronid.weighttrackertft.ui.components.charts.donutCharts.SimpleDonutChart
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
                text = "Bienvenido, ${name.value}!",
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
            Button(onClick = { navHostController.navigate(NavigationRoutes.PhysicalData.route) }) {
                Text(text = "Go to questionnaire")
            }

            Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                Text(text = "see routines")
            }
            Button(onClick = { navHostController.navigate(NavigationRoutes.EditRoutine.route) }) {
                Text(text = "editar routine")
            }

            Button(onClick = { navHostController.navigate(NavigationRoutes.WorkoutList.route) }) {
                Text(text = "Lista de rutinas")
            }

//            MyCalendar()

//            Button(onClick = {navHostController.navigate(NavigationRoutes.CreateRoutine.route)}) {
//                Text(text = "Create a routine")
//            }
//            Button(onClick = {navHostController.navigate(NavigationRoutes.Exercises.route)}) {
//                Text(text = "Ver ejercicios")
//            }

            if (showDialog && selectedDate != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            "Entrenamientos del ${
                                selectedDate!!.format(
                                    DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                    )
                                )
                            }"
                        )
                    },
                    text = {
                        if (selectedWorkouts.isEmpty()) {
                            Text("No hay entrenamientos para este día.")
                        } else if (selectedWorkouts.size == 1) {
                            // Caso 1: Un solo entrenamiento
                            val workout = selectedWorkouts.first()
                            Text(
                                text = """
                                    Entrenamiento:
                                    - Calorías: ${workout.calories} kcal
                                    - Volumen: ${workout.volume} kg
                                    - Tipo: ${workout.workoutType}
                                    - Intensidad: ${workout.intensity}%
                                    - Fecha: ${
                                    workout.date?.toDate()?.toString() ?: "No disponible"
                                }
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            // Caso 2: Múltiples entrenamientos
                            Text(
                                text = selectedWorkouts.joinToString("\n") { workout ->
                                    "Entrenamiento ${selectedWorkouts.indexOf(workout) + 1}:\n" +
                                            "- Calorías: ${workout.calories} kcal\n" +
                                            "- Volumen: ${workout.volume} kg\n" +
                                            "- Tipo: ${workout.workoutType}\n" +
                                            "- Intensidad: ${workout.intensity}%\n" +
                                            "- Fecha: ${
                                                workout.date?.toDate()
                                                    ?.toString() ?: "No disponible"
                                            }"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cerrar")
                        }
                    })
            }

        }
    }

}
