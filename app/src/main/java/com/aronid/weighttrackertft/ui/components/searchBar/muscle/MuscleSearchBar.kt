package com.aronid.weighttrackertft.ui.components.searchBar.muscle

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.ui.components.exercise.exerciseCard.ExerciseCard
import com.aronid.weighttrackertft.ui.components.filter.muscleFilter.MuscleFilter
import com.aronid.weighttrackertft.utils.Translations.exerciseTranslations
import com.aronid.weighttrackertft.utils.Translations.muscleTranslations
import com.aronid.weighttrackertft.utils.Translations.translateAndFormat
import java.util.Locale

@Composable
fun MuscleSearchBar(
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

    // Get the current locale to determine the language
    val currentLocale = Locale.getDefault().language


    // Translate muscle list for the filter
    val translatedMuscleList = viewModel.muscleList.map { muscle ->
        translateAndFormat(muscle, muscleTranslations)
    }

    // Translate selected muscle for the filter (if not null)
    val translatedSelectedMuscle = selectedMuscle?.let { translateAndFormat(it, muscleTranslations) }

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
                    Text(stringResource(R.string.search_placeholder))
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

        MuscleFilter(
            muscleList = translatedMuscleList,
            selectedMuscle = translatedSelectedMuscle,
            onMuscleSelected = { muscle ->
                // Map the translated muscle back to its original ID for the ViewModel
                val originalMuscle = viewModel.muscleList.find { translateAndFormat(it, muscleTranslations) == muscle }
                originalMuscle?.let { viewModel.toggleMuscleFilter(it) }
            },
            onClearFilter = {
                translatedSelectedMuscle?.let { muscle ->
                    val originalMuscle = viewModel.muscleList.find { translateAndFormat(it, muscleTranslations) == muscle }
                    originalMuscle?.let { viewModel.toggleMuscleFilter(it) }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredExercises) { exercise ->
                ExerciseCard(
                    name = translateAndFormat(exercise.name, exerciseTranslations),
                    primaryMuscle = translateAndFormat(exercise.primaryMuscle?.id ?: "", muscleTranslations),
                    secondaryMuscles = exercise.secondaryMuscle.map { translateAndFormat(it.id, muscleTranslations) },
                    imageUrl = R.drawable.background,
                    isSelected = selectedExerciseIds.contains(exercise.id),
                    onCardClick = { onExerciseSelected(exercise) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}