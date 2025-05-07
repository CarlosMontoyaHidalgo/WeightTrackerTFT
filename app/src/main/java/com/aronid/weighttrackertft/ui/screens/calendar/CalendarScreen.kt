package com.aronid.weighttrackertft.ui.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutCalendar
import com.aronid.weighttrackertft.ui.components.dialogs.WorkoutDialogContent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    innerPadding: PaddingValues,
    calendarViewModel: CalendarViewModel,
    navHostController: NavHostController,
) {
    val workouts by calendarViewModel.workouts.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedWorkouts by rememberSaveable { mutableStateOf<List<WorkoutModel>>(emptyList()) }
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(innerPadding)) {
                Text(
                    text = stringResource(R.string.your_trainings),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {

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
                        WorkoutDialogContent(workouts = selectedWorkouts, navHostController)
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.close))
                        }
                    }
                )
            }
            BackButton(navHostController)
        }
    }
}