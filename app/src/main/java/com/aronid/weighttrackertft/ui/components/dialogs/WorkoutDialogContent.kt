package com.aronid.weighttrackertft.ui.components.dialogs

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.utils.Translations
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WorkoutDialogContent(workouts: List<WorkoutModel>, navHostController: NavHostController) {
    if (workouts.isEmpty()) {
        Text(stringResource(R.string.no_workouts_for_day))
    } else {
        LazyColumn {
            item {
                workouts.forEachIndexed { index, workout ->
                    WorkoutCardSummary(
                        workout = workout,
                        index = if (workouts.size > 1) index + 1 else null,
                        navHostController = navHostController
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WorkoutCardSummary(workout: WorkoutModel, index: Int? = null, navHostController: NavHostController) {
    val locale = Locale("es", "ES")
    val formatter = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy 'a las' HH:mm", locale)
    val dateString = workout.date?.toDate()?.let {
        formatter.format(it).replaceFirstChar { char -> char.uppercase(locale) }
    } ?: stringResource(R.string.not_available)

    Log.d("WorkoutCardSummary", "workout: $workout")
    Card(
        modifier = Modifier.fillMaxWidth().clickable{
            navHostController.navigate(NavigationRoutes.WorkoutSummary.createRoute(workout.id))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (index != null) {
                Text(
                    text = "Entrenamiento $index",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text("Calorías: ${workout.calories} kcal", style = MaterialTheme.typography.bodyMedium)
            Text("Volumen: ${workout.volume} kg", style = MaterialTheme.typography.bodyMedium)
            Text("Intensidad: ${String.format("%.2f", workout.intensity)}", style = MaterialTheme.typography.bodyMedium)
            Text("Tipo: ${workout.workoutType}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha: $dateString", style = MaterialTheme.typography.bodyMedium)
            val translatedMuscles = workout.primaryMuscleIds.joinToString(", ") { muscle ->
                Translations.translateAndFormat(muscle, Translations.muscleTranslations)
            }

            Text(
                text = "Músculos principales: $translatedMuscles",
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}
