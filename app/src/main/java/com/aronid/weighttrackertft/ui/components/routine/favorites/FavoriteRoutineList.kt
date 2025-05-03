package com.aronid.weighttrackertft.ui.components.routine.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.routine.favorite.FavoriteRoutine
import com.aronid.weighttrackertft.navigation.NavigationRoutes

@Composable
fun FavoriteRoutineList(
    modifier: Modifier = Modifier,
    favoriteRoutines: List<FavoriteRoutine> = emptyList(),
    navHostController: NavHostController,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Entrenamientos favoritos",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    if (favoriteRoutines.isEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "No favorite routines yet",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                Text(text = "Ver todas las rutinas")
            }
        }
    } else {
        RoutineList(
            favorites = favoriteRoutines,
            modifier = modifier,
            navHostController = navHostController
        )
    }
}

@Composable
fun RoutineList(
    favorites: List<FavoriteRoutine>,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        items(favorites) { favorite ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navHostController.navigate(
                            NavigationRoutes.RoutineDetails.createRoute(
                                favorite.routineId,
                                favorite.isPredefined
                            )
                        )
                    },

                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = favorite.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (favorite.isPredefined) "Predefinida" else "Personalizada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
