package com.aronid.weighttrackertft.ui.components.searchBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class)
class SearchBarViewModel : ViewModel(
) {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    val selectedMuscle: StateFlow<String?> = _selectedMuscle.asStateFlow()

    val muscleList = listOf("Quadriceps", "Hamstrings", "Glutes", "Chest", "Triceps", "Shoulders", "Back", "Biceps", "Lower Back")

    fun getFilteredExercises(exercises: List<ExerciseModel>) = combine(
        _searchText,
        _selectedMuscle
    ) { text, muscle ->
        exercises.filter { exercise ->
            val matchesText = text.isEmpty() || exercise.matchesSearchQuery(text)
            val primaryMuscleName = exercise.primaryMuscle?.id ?: ""
            val secondaryMuscleNames = exercise.secondaryMuscle.map { it.id }

            val matchesMuscle = muscle == null ||
                    primaryMuscleName.equals(muscle, ignoreCase = true) ||
                    secondaryMuscleNames.any { it.equals(muscle, ignoreCase = true) }

            matchesText && matchesMuscle
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        exercises
    )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun toggleMuscleFilter(muscle: String) {
        _selectedMuscle.value = if (_selectedMuscle.value == muscle) null else muscle
    }

    fun clearAllFilters() {
        _searchText.value = ""
        _selectedMuscle.value = null
    }
}