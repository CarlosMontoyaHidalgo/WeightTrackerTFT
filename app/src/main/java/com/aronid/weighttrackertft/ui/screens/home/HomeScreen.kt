package com.aronid.weighttrackertft.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton
import com.aronid.weighttrackertft.ui.components.calendar.WeeklyWorkoutCalendar
import com.aronid.weighttrackertft.ui.components.dialogs.WorkoutDialogContent
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.components.routine.favorites.FavoriteRoutineList
import com.aronid.weighttrackertft.ui.components.stepCounter.StepCounterManager
import com.aronid.weighttrackertft.ui.components.stepCounter.StepCounterSensor
import com.aronid.weighttrackertft.utils.common.RequestActivityRecognitionPermission
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    viewModel: HomeViewModel,
) {

    RequestActivityRecognitionPermission()

    val workouts by viewModel.weeklyWorkouts.collectAsState()
    val name by viewModel.userName.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current
    var stepCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }
    var stepsToday by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        val stepManager = StepCounterManager(context)

        val sensor = StepCounterSensor(context) { currentSteps ->
            stepsToday = stepManager.getStepsForToday(currentSteps)
        }
        sensor.start()

    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedWorkouts by rememberSaveable { mutableStateOf<List<WorkoutModel>>(emptyList()) }
    val workoutCount by viewModel.workoutCount.collectAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome, name),
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                        contentDescription = "Pasos"
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                    Text("Pasos de hoy: $stepsToday")
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navHostController = navHostController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navHostController.navigate(NavigationRoutes.ChatbotScreen.route) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bot_avatar_no_background),
                    contentDescription = stringResource(R.string.open_chatbot),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(paddingValues)
        ) {
            ElevatedCard(
                onClick = {
                    navHostController.navigate(NavigationRoutes.Calendar.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 24.dp),
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

            MyElevatedButton(
                onClick = { navHostController.navigate(NavigationRoutes.Routines.route) },
                text = stringResource(R.string.view_all_routines),
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyElevatedButton(
                onClick = { navHostController.navigate(NavigationRoutes.WorkoutList.route) },
                text = if (workoutCount == 0) {
                    stringResource(R.string.no_workouts)
                } else {
                    stringResource(R.string.workout_count, workoutCount)
                },
                modifier = Modifier,
                enabled = workoutCount > 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            FavoriteRoutineList(
                modifier = Modifier,
                favoriteRoutines = favorites,
                navHostController = navHostController
            )
            Spacer(modifier = Modifier.height(16.dp))




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
                        WorkoutDialogContent(workouts = selectedWorkouts, navHostController)
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

