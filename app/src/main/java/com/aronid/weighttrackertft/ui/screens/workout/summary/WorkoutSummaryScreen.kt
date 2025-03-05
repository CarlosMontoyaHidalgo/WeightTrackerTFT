package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.konfetti.KonfettiComponent
import com.aronid.weighttrackertft.ui.screens.workout.WorkoutViewModel
@Composable
fun WorkoutSummaryScreen(
    innerPadding: PaddingValues,
    workoutId: String?, // Nuevo parámetro
    viewModel: WorkoutSummaryViewModel,
    navHostController: NavHostController
) {

    Log.d("WorkoutSummaryScreen", "WorkoutId: $workoutId")
    val calories by viewModel.calories.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val triggerKonfetti = remember { mutableStateOf(false) }
    Log.d("WorkoutSummaryScreen", "Calories: $calories, Volume: $volume, SaveState: $saveState")


    // Cargar los datos cuando workoutId esté disponible
    LaunchedEffect(workoutId) {
        workoutId?.let { viewModel.loadWorkoutById(it) }
        Log.d("WorkoutSummaryScreen", "$workoutId")
    }

    Log.d("WorkoutSummaryScreen", "Recomposing - Calories: $calories, Volume: $volume, SaveState: $saveState")

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
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (calories != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.LocalFireDepartment, "Calories", tint = Color.Red, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text("Calorías quemadas:", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                        }
                        Text(
                            text = "$calories kcal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Red
                        )
                    } else {
                        Text("Cargando calorías...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (volume != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.FitnessCenter, "Volume", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text("Volumen del entrenamiento:", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                        }
                        val animatedVolume by animateIntAsState(
                            targetValue = volume ?: 0,
                            animationSpec = tween(durationMillis = 1000),
                            label = "Volume Animation"
                        )
                        Text(
                            text = "$animatedVolume kg",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text("Cargando volumen...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        formButton = {
            Button(onClick = { navHostController.navigate(NavigationRoutes.Home.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar")
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun WorkoutSummaryScreenPreview() {
//    val navController = rememberNavController()
//    val viewModel: WorkoutViewModel1 = remember { WorkoutViewModel1() }
//    WorkoutSummaryScreen(
//        innerPadding = PaddingValues(16.dp),
//        viewModel = viewModel,
//        navHostController = navController
//    )
//}