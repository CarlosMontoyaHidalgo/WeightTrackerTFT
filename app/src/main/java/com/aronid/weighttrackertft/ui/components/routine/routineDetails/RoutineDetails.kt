package com.aronid.weighttrackertft.ui.components.routine.routineDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.exercise.exerciseCard.ExerciseCard

@Composable
fun RoutineDetailsContent(
    routine: RoutineModel?,
    exercises: List<ExerciseModel>,
    navHostController: NavHostController,
    innerPadding: PaddingValues,
    isPredefined: Boolean
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (headerRef, cardsRef, buttonRef) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(headerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            routine?.let { rt ->
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = rt.name, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = rt.goal ?: "Sin objetivo",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                CircularProgressIndicator()
            }
        }

        routine?.let { rt ->
            Button(
                onClick = {
                    navHostController.navigate(NavigationRoutes.Workout.createRoute(rt.id, isPredefined))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttonRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(text = "Empezar rutina")
            }
        }

        if (exercises.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(cardsRef) {
                        top.linkTo(headerRef.bottom, margin = 8.dp)
                        bottom.linkTo(buttonRef.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cardsRef) {
                        top.linkTo(headerRef.bottom, margin = 8.dp)
                        bottom.linkTo(buttonRef.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height =
                            Dimension.fillToConstraints
                    }
            ) {
                items(exercises) { exercise ->
                    ExerciseCard(
                        name = exercise.name,
                        primaryMuscle = exercise.primaryMuscle?.id ?: "",
                        secondaryMuscles = exercise.secondaryMuscle.map { it.id },
                        imageUrl = R.drawable.background,
                        onCardClick = {
                            navHostController.navigate(NavigationRoutes.ExerciseDetails.createRoute(exercise.id))
                        },
                        isSelected = false,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}