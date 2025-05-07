package com.aronid.weighttrackertft.ui.components.exercise.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel

@Composable
fun ExerciseDetailsContent(
    exercise: ExerciseModel?,
    primaryMuscleName: String?,
    secondaryMuscleNames: List<String>,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
    ) {
        item {
            exercise?.let { ex ->
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Exercise",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descripción: ${ex.description}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                primaryMuscleName?.let { name ->
                    Text(
                        text = "Músculo principal: $name",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (secondaryMuscleNames.isNotEmpty()) {
                    Text(
                        text = "Músculos secundarios: ${secondaryMuscleNames.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
