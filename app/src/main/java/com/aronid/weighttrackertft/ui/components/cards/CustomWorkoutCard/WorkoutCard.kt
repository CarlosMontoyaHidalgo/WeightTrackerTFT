package com.aronid.weighttrackertft.ui.components.cards.CustomWorkoutCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.ui.components.tags.MyTag
import com.google.firebase.Timestamp

@Composable
fun WorkoutCard(
    modifier: Modifier = Modifier,
    workout: WorkoutModel,
    onDeleteClick: () -> Unit = {},
    showDeleteButton: Boolean = false,
    isChecked: Boolean = false,
    showCheckbox: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    navHostController: NavHostController? = null,
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = workout.workoutType.ifEmpty { "Entrenamiento sin nombre" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )


                Text(
                    text = "Fecha: ${workout.date?.toDate()?.toString() ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyMedium
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Primarios: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(workout.primaryMuscleIds) { muscle ->
                            MyTag(text = muscle.ifEmpty { "Ninguno" })
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Secundarios: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(workout.secondaryMuscleIds) { muscle ->
                            MyTag(text = muscle.ifEmpty { "Ninguno" })
                        }
                    }
                }
            }

            if (showCheckbox) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onCheckedChange(it) },
                    modifier = Modifier
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutCardPreviewWithDelete() {
    val sampleWorkout = WorkoutModel(
        id = "123",
        workoutType = "Fuerza",
        date = Timestamp.now(),
        primaryMuscleIds = listOf("chest", "triceps"),
        secondaryMuscleIds = listOf("shoulders")
    )

    MaterialTheme {
        WorkoutCard(
            workout = sampleWorkout,
            onDeleteClick = { /* Simular eliminación */ },
            showCheckbox = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutCardPreviewWithoutDelete() {
    val sampleWorkout = WorkoutModel(
        id = "123",
        workoutType = "Fuerza",
        date = Timestamp.now(),
        primaryMuscleIds = listOf("chest", "triceps"),
        secondaryMuscleIds = listOf("shoulders")
    )

    MaterialTheme {
        WorkoutCard(workout = sampleWorkout)
    }
}
//@Preview
//@Composable
//fun WorkoutCardPreview() {
//    WorkoutCard({})
//}