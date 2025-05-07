package com.aronid.weighttrackertft.ui.components.routine.routineDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.exercise.exerciseCard.ExerciseCard
import com.aronid.weighttrackertft.ui.screens.routines.details.RoutineDetailsViewModel
import com.aronid.weighttrackertft.utils.button.ButtonType

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
    val state by viewModel.buttonState.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (headerRef, cardsRef, buttonRef) = createRefs()

        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(headerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Back Button
            IconButton(onClick = { navHostController.popBackStack() }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Volver",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Title and Goal
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                routine?.let { rt ->
                    Text(
                        text = rt.name,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = rt.goal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } ?: run {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

            // Favorite Button
            IconButton(
                onClick = {
                    routine?.id?.let {
                        viewModel.toggleFavorite(it, isPredefined)
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isFavorite) R.drawable.ic_star
                        else R.drawable.ic_star_border
                    ),
                    contentDescription = "favorite",
                    tint = Color(0xFFFFD700)
                )
            }
        }

        // Exercise List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(cardsRef) {
                    top.linkTo(headerRef.bottom, margin = 16.dp)
                    bottom.linkTo(buttonRef.top, margin = 16.dp)
                    height = Dimension.fillToConstraints
                },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (exercises.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(exercises) { exercise ->
                    ExerciseCard(
                        name = exercise.name,
                        primaryMuscle = exercise.primaryMuscle?.id ?: "",
                        secondaryMuscles = exercise.secondaryMuscle.map { it.id },
                        imageUrl = R.drawable.background,
                        onCardClick = {
                            navHostController.navigate(
                                NavigationRoutes.ExerciseDetails.createRoute(exercise.id)
                            )
                        },
                        isSelected = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        NewCustomButton(
            text = stringResource(id = R.string.start_routine),
            onClick = {
                routine?.id?.let {
                    navHostController.navigate(
                        NavigationRoutes.Workout.createRoute(it, isPredefined)
                    )
                }
            },
            buttonType = ButtonType.FILLED,
            containerColor = MaterialTheme.colorScheme.primary,
            textConfig = buttonConfigs.textConfig,
            layoutConfig = buttonConfigs.layoutConfig,
            stateConfig = buttonConfigs.stateConfig,
            borderConfig = buttonConfigs.borderConfig,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(buttonRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )


    }
}