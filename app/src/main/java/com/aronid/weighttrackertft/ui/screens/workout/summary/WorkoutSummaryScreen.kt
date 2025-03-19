package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.cards.CustomElevatedCard.CustomElevatedCard
import com.aronid.weighttrackertft.ui.components.charts.donutCharts.SimpleDonutChart
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.konfetti.KonfettiComponent
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun WorkoutSummaryScreen(
    innerPadding: PaddingValues,
    workoutId: String?,
    viewModel: WorkoutSummaryViewModel,
    navHostController: NavHostController
) {
    Log.d("WorkoutSummaryScreen", "WorkoutId: $workoutId")
    val calories by viewModel.calories.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val triggerKonfetti = remember { mutableStateOf(false) }
    val primaryMuscles by viewModel.primaryMuscles.collectAsState(initial = emptyList())
    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs
    val isLoading by viewModel.isLoading.collectAsState(initial = true)

    LaunchedEffect(workoutId) {
        workoutId?.let {
            viewModel.loadWorkoutById(it)
            Log.d("WorkoutSummaryScreen", "Loading workout with ID: $it")
        }
    }

    Log.d("WorkoutSummaryScreen", "Recomposing - Calories: $calories, Volume: $volume, SaveState: $saveState, IsLoading: $isLoading")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )
            }
        } else {

            KonfettiComponent(
                modifier = Modifier.fillMaxSize(),
                isActive = triggerKonfetti.value,
                durationMs = 2000L,
                onFinish = { triggerKonfetti.value = false }
            )

            FormScreen(
                modifier = Modifier,
                innerPadding = innerPadding,
                isContentScrolleable = false,
                formContent = {
                    Text(text = saveState.toString())
                    CustomElevatedCard(
                        title = "Calorias quemadas",
                        result = calories,
                        unitLabel = "kcal",
                        contentColor = Color.Red
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomElevatedCard(
                        iconResource = Icons.Filled.FitnessCenter,
                        title = "Volumen",
                        result = volume,
                        unitLabel = "kg",
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (primaryMuscles.isNotEmpty()) {
                            Log.d("WorkoutSummaryScreen", "Primary Muscles: $primaryMuscles")
                            SimpleDonutChart(content = primaryMuscles)
                        } else {
                            Text("No hay datos disponibles")
                        }
                    }
                },
                formButton = {
                    NewCustomButton(
                        text = "Cerrar",
                        onClick = { navHostController.navigate(NavigationRoutes.Home.route) },
                        buttonType = ButtonType.FILLED,
                        containerColor = Color.Black,
                        textConfig = buttonConfigs.textConfig,
                        layoutConfig = buttonConfigs.layoutConfig,
                        stateConfig = buttonConfigs.stateConfig,
                        borderConfig = buttonConfigs.borderConfig
                    )
                }
            )
        }
    }
}