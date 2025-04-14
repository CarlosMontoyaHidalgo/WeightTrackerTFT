package com.aronid.weighttrackertft.ui.components.filter.FilterChips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.ui.screens.routines.RoutineFilterType

@Composable
fun FilterChips(
    showFilters: Boolean,
    filterType: RoutineFilterType,
    onFilterChange: (RoutineFilterType) -> Unit
) {
    if (showFilters) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = filterType == RoutineFilterType.ALL,
                onClick = { onFilterChange(RoutineFilterType.ALL) },
                label = { Text("Todas") }
            )
            FilterChip(
                selected = filterType == RoutineFilterType.CUSTOM_ONLY,
                onClick = { onFilterChange(RoutineFilterType.CUSTOM_ONLY) },
                label = { Text("Mis Rutinas") }
            )
            FilterChip(
                selected = filterType == RoutineFilterType.PREDEFINED_ONLY,
                onClick = { onFilterChange(RoutineFilterType.PREDEFINED_ONLY) },
                label = { Text("Predefinidas") }
            )
            // NEW: Added Favorites filter chip
            FilterChip(
                selected = filterType == RoutineFilterType.FAVORITES,
                onClick = { onFilterChange(RoutineFilterType.FAVORITES) },
                label = { Text("Favoritas") }
            )
        }
    }
}