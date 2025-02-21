package com.aronid.weighttrackertft.ui.components.searchBar

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.ui.components.exerciseCard.ExerciseCard
import com.aronid.weighttrackertft.ui.components.tags.MyTag

@Composable
fun MySearchBar(
    exercises: List<ExerciseModel>,
    onExerciseSelected: (ExerciseModel) -> Unit,
    selectedExerciseIds: List<String>,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<SearchBarViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val filteredExercises by viewModel.getFilteredExercises(exercises)
        .collectAsState(initial = exercises)
    val selectedMuscle by viewModel.selectedMuscle.collectAsState()

    Log.d("MySearchBar", "selectedMuscle: $selectedMuscle")
    Log.d("MySearchBar", "selectedMuscle: $selectedExerciseIds")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),
            placeholder = {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search...")
                }
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.clearAllFilters()
                        }
                    )
                }
            },
            maxLines = 1,
            singleLine = true
        )

        MyFilter(
            muscleList = viewModel.muscleList,
            selectedMuscle = selectedMuscle,
            onMuscleSelected = { muscle -> viewModel.toggleMuscleFilter(muscle) },
            onClearFilter = { viewModel.toggleMuscleFilter(selectedMuscle!!) },
            modifier = Modifier.fillMaxWidth()
        )

        // Lista de resultados
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredExercises) { exercise ->
                ExerciseCard(
                    name = exercise.name,
                    primaryMuscle = exercise.primaryMuscle?.id ?: "",
                    secondaryMuscles = exercise.secondaryMuscle.map { it.id },
                    imageUrl = R.drawable.background,
                    isSelected = selectedExerciseIds.contains(exercise.id),
                    onCardClick = { onExerciseSelected(exercise) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MyFilter(
    muscleList: List<String>,
    selectedMuscle: String?,
    onMuscleSelected: (String) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Lista de músculos para filtrar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(muscleList.distinct()) { muscle ->
                val isSelected = muscle == selectedMuscle
                MyTag(
                    text = muscle,
                    backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    onClick = { onMuscleSelected(muscle) }
                )
            }
        }

        // Indicador de filtro activo
        selectedMuscle?.let { muscle ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClearFilter() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtro activo: $muscle",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_x),
                    contentDescription = "Quitar filtro",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
