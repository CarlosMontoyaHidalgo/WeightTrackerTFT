package com.aronid.weighttrackertft.ui.components.routine.routineDetails

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.exercise.exerciseCard.ExerciseCard
import com.aronid.weighttrackertft.ui.screens.routines.details.RoutineDetailsViewModel

@Composable
fun RoutineDetailsContent(
    routine: RoutineModel?,
    exercises: List<ExerciseModel>,
    navHostController: NavHostController,
    innerPadding: PaddingValues,
    isPredefined: Boolean,
    viewModel: RoutineDetailsViewModel
) {

    val isFavorite by viewModel.isFavorite.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (headerRef, titleRef, favoriteRef, cardsRef, buttonRef) = createRefs()

// Ícono de favoritos en la parte superior derecha
        IconButton(
            onClick = {
                routine?.id?.let {
                    Log.d("Favorites", "Clic en favorito para rutina: $it")
                    viewModel.toggleFavorite(it, isPredefined)
                }

            },
            modifier = Modifier.constrainAs(favoriteRef) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                painter = painterResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border),
                contentDescription = "favorite",
                tint = Color(0xFFFFD700)
            )
        }

        // Encabezado con el nombre y objetivo, centrado horizontalmente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(headerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            routine?.let { rt ->
                Text(text = rt.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = rt.goal, style = MaterialTheme.typography.bodyLarge)
            } ?: run {
                CircularProgressIndicator()
            }
        }

        routine?.let { rt ->
            Button(
                onClick = {
                    navHostController.navigate(
                        NavigationRoutes.Workout.createRoute(
                            rt.id,
                            isPredefined
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttonRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(stringResource(R.string.start_routine))
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
                            navHostController.navigate(
                                NavigationRoutes.ExerciseDetails.createRoute(
                                    exercise.id
                                )
                            )
                        },
                        isSelected = false,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}