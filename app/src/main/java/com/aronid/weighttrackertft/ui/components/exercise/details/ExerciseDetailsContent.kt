package com.aronid.weighttrackertft.ui.components.exercise.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.utils.Translations
import com.aronid.weighttrackertft.utils.getExerciseId
import com.aronid.weighttrackertft.utils.getExerciseImageResource
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseDetailsContent(
    exercise: ExerciseModel?,
    primaryMuscleName: String?,
    secondaryMuscleNames: List<String>,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            if (exercise == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.loading_exercise),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Exercise Name
                        /*Text(
                            text = Translations.translateAndFormat(
                                exercise.name ?: stringResource(R.string.exercise),
                                Translations.exerciseTranslations
                            ),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
*/
                        // Image
                        Image(

                            painter = painterResource(
                                getExerciseImageResource(
                                    getExerciseId(
                                        exercise.name
                                    )
                                )
                            ),
                            contentDescription = stringResource(
                                R.string.exercise_image_description,
                                exercise.name ?: stringResource(R.string.exercise)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )


                        // Primary Muscle
                        primaryMuscleName?.takeIf { it.isNotBlank() }?.let { name ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.primary_muscle_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                MuscleChip(
                                    text = Translations.translateAndFormat(
                                        name,
                                        Translations.muscleTranslations
                                    ),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                        }

                        // Secondary Muscles
                        if (secondaryMuscleNames.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.secondary_muscles_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    secondaryMuscleNames.forEach { muscle ->
                                        MuscleChip(
                                            text = Translations.translateAndFormat(
                                                muscle,
                                                Translations.muscleTranslations
                                            ),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        val language = Locale.getDefault().language
                        val instructionText = exercise.instructions?.get(language)
                            ?: exercise.instructions?.get("es")
                            ?: ""
                        // Description
                        Text(
                            text = instructionText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}