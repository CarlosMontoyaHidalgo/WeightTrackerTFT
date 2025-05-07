package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.cards.CustomElevatedCard.CustomElevatedCard
import com.aronid.weighttrackertft.ui.components.charts.barList.MuscleBarList
import com.aronid.weighttrackertft.ui.components.charts.radarCharts.MuscleRadarChart
import com.aronid.weighttrackertft.ui.components.charts.verticalCharts.MuscleVerticalBarChart
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.konfetti.KonfettiComponent
import com.aronid.weighttrackertft.utils.Translations
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun WorkoutSummaryScreen(
    innerPadding: PaddingValues,
    workoutId: String?,
    viewModel: WorkoutSummaryViewModel,
    navHostController: NavHostController
) {
    val calories by viewModel.calories.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val triggerKonfetti = remember { mutableStateOf(false) }
    val allMuscles by viewModel.allMuscles.collectAsState(initial = emptyList())
    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val exercises by viewModel.exercises.collectAsState()
    Log.d("WorkoutSummaryScreen", "$exercises")

    LaunchedEffect(workoutId) {
        workoutId?.let {
            viewModel.loadWorkoutById(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            KonfettiComponent(
                modifier = Modifier.fillMaxSize(),
                isActive = triggerKonfetti.value,
                durationMs = 2000L,
                onFinish = { triggerKonfetti.value = false }
            )

            val tabs = listOf(
                "Datos",
                "Ejercicios",
                "Rutina"
            )

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTabIndex) {
            0 -> DataSection(
                innerPadding = innerPadding,
                calories = calories,
                volume = volume,
                buttonConfigs = buttonConfigs,
                saveState = saveState,
                navHostController = navHostController
            )

            1 -> MuscleSection(
                innerPadding = innerPadding,
                workoutId = workoutId,
                viewModel = viewModel,
                allMuscles = allMuscles,
                navHostController = navHostController
            )

            2 -> RoutineSection(
                innerPadding = innerPadding,
                workoutId = workoutId,
                exercises = exercises,
                viewModel = viewModel
            )
        }
    }

}

@Composable
fun DataSection(
    innerPadding: PaddingValues,
    calories: Int?,
    volume: Int?,
    buttonConfigs: ButtonConfigs,
    saveState: String?,
    navHostController: NavHostController
) {
    val context = LocalContext.current


    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            CustomElevatedCard(
                title = stringResource(R.string.calories_burned),
                result = calories,
                unitLabel = stringResource(R.string.kcal_unit),
                contentColor = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomElevatedCard(
                iconResource = Icons.Filled.FitnessCenter,
                title = stringResource(R.string.volume),
                result = volume,
                unitLabel = stringResource(R.string.kg_unit),
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        formButton = {
            NewCustomButton(
                text = stringResource(R.string.close),
                onClick = { navHostController.navigate(NavigationRoutes.Home.route) },
                buttonType = ButtonType.FILLED,
                containerColor = MaterialTheme.colorScheme.primary,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )
        }
    )

}

@Composable
fun MuscleSection(
    innerPadding: PaddingValues,
    workoutId: String?,
    viewModel: WorkoutSummaryViewModel,
    allMuscles: List<Pair<String, Float>>,
    navHostController: NavHostController
) {
    val musclesWorkedLabel =
        Translations.uiStrings["description_label"]?.get("es") ?: "Músculos trabajados"
    val primaryMuscleLabel =
        Translations.uiStrings["primary_muscle_label"]?.get("es") ?: "Primarios"
    val secondaryMusclesLabel =
        Translations.uiStrings["secondary_muscles_label"]?.get("es") ?: "Secundarios"

    val translatedMuscles = allMuscles.map { (muscleId, value) ->
        Pair(Translations.translateAndFormat(muscleId, Translations.muscleTranslations), value)
    }

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (translatedMuscles.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { 3 })
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = musclesWorkedLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Leyenda para primarios y secundarios
                        Legends(
                            legendsConfig = LegendsConfig(
                                legendLabelList = listOf(
                                    LegendLabel(Color.Green, primaryMuscleLabel),
                                    LegendLabel(Color.Blue, secondaryMusclesLabel)
                                ),
                                gridColumnCount = 2,
                                textSize = 14.sp,
                                colorBoxSize = 20.dp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> MuscleBarList(translatedMuscles)
                                1 -> MuscleRadarChart(translatedMuscles)
                                2 -> MuscleVerticalBarChart(translatedMuscles)
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_data_available),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        formButton = {}
    )
}

@Composable
fun RoutineSection(
    innerPadding: PaddingValues,
    workoutId: String?,
    exercises: List<ExerciseWithSeries>,
    viewModel: WorkoutSummaryViewModel
) {
    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = true,
        formContent = {
            if (exercises.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.exercises_performed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                exercises.forEach { exercise ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Traducir el nombre del ejercicio
                            val translatedExerciseName = exercise.exerciseName?.let {
                                Translations.translateAndFormat(
                                    it,
                                    Translations.exerciseTranslations
                                )
                            } ?: stringResource(R.string.unknown_exercise)
                            Text(
                                text = translatedExerciseName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val validSeries = exercise.series.filter { series ->
                                !series.reps.isNullOrBlank() && !series.weight.isNullOrBlank()
                            }
                            if (validSeries.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                validSeries.forEach { series ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),

                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(
                                                R.string.set_number,
                                                series.setNumber
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.reps, series.reps ?: ""),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.weight_data,
                                                series.weight ?: ""
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.no_valid_series),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_exercises_available),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        formButton = { }
    )
}


