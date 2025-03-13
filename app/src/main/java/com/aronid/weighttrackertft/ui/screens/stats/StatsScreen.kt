package com.aronid.weighttrackertft.ui.screens.stats


import android.R.attr.entries
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutCalendar
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.patrykandpatrick.vico.compose.Chart
//import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
//import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
//import com.patrykandpatrick.vico.compose.chart.column.columnChart
//import com.patrykandpatrick.vico.core.entry.ChartEntryModel
//import com.patrykandpatrick.vico.core.entry.entries
//
//@Composable
//fun SimpleBarChart() {
//    // Datos de ejemplo (volumen semanal en kg)
//    val chartEntries = entries {
//        listOf(45f, 52f, 48f, 60f, 55f).forEach { entry(it) }
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Volumen Semanal", style = MaterialTheme.typography.headlineSmall)
//
//        Chart(
//            chart = columnChart(
//                spacing = 8.dp,
//                colors = listOf(MaterialTheme.colorScheme.primary)
//            ),
//            chartModel = ChartEntryModel(chartEntries),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .padding(top = 16.dp),
//
//            bottomAxis = rememberBottomAxis(
//                title = "Semanas",
//                valueFormatter = { value, _ ->
//                    listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5")
//                        .getOrNull(value.toInt()) ?: ""
//                }
//            ),
//
//            startAxis = rememberStartAxis(
//                title = "Kg",
//                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                guidelineColor = Color.Transparent
//            )
//        )
//    }
//}
//https://www.patrykandpatrick.com/vico/guide/stable/getting-started


@Composable
fun StatsScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    calendarViewModel: CalendarViewModel
) {
    val workouts by calendarViewModel.workouts.collectAsState()
    Log.d("StatsScreen", "Workouts: $workouts")
    val accountCreationDate by calendarViewModel.accountCreationDate.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedWorkouts by rememberSaveable { mutableStateOf<List<WorkoutModel>>(emptyList()) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController = navHostController) },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(paddingValues)) {
            WorkoutCalendar(
                workouts = workouts,
                onDayClick = { date, dayWorkouts ->
                    selectedDate = date
                    selectedWorkouts = dayWorkouts
                    showDialog = true
                    println("Clicked day: $date with ${dayWorkouts.size} workouts")
                },
                viewModel = calendarViewModel
            )



            if (showDialog && selectedDate != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Entrenamientos del ${selectedDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}") },
                    text = {
                        if (selectedWorkouts.isEmpty()) {
                            Text("No hay entrenamientos para este día.")
                        } else if (selectedWorkouts.size == 1) {
                            val workout = selectedWorkouts.first()
                            Text(
                                text = """
                                    Entrenamiento:
                                    - Calorías: ${workout.calories} kcal
                                    - Volumen: ${workout.volume} kg
                                    - Tipo: ${workout.workoutType}
                                    - Intensidad: ${workout.intensity}%
                                    - Fecha: ${workout.date?.toDate()?.toString() ?: "No disponible"}
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = selectedWorkouts.joinToString("\n") { workout ->
                                    "Entrenamiento ${selectedWorkouts.indexOf(workout) + 1}:\n" +
                                            "- Calorías: ${workout.calories} kcal\n" +
                                            "- Volumen: ${workout.volume} kg\n" +
                                            "- Tipo: ${workout.workoutType}\n" +
                                            "- Intensidad: ${workout.intensity}%\n" +
                                            "- Fecha: ${workout.date?.toDate()?.toString() ?: "No disponible"}"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    }
}