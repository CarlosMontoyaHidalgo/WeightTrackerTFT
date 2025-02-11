package com.aronid.weighttrackertft.ui.components.searchBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
class SearchBarViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    val selectedMuscle = _selectedMuscle.asStateFlow()

    private val _exercises = MutableStateFlow(exerciseList)
    val exercises = searchText
        .debounce(300L)
        .onEach { _isSearching.update { true } }
        .combine(_exercises) { text, exercises ->
            if (text.isBlank()) {
                exercises
            } else {
                exercises.filter { it.matchesSearchQuery(text) }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _exercises.value)


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun clearSearchText() {
        _searchText.value = ""
    }

    fun onMuscleSelected(muscle: String) {
        _selectedMuscle.value = muscle
    }

    fun clearSelectedMuscle() {
        _selectedMuscle.value = null
    }

}

data class Exercise(
    val name: String,
    val muscles: List<String>,
    val image: Int
) {

    //TODO: View if the name or description is empty
    fun matchesSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
            muscles.joinToString(" "),
            "$name ${muscles.joinToString(" ")}",
            "$name${muscles.joinToString(" ")}",
            "${name.first()} ${muscles.joinToString("").first()}",
            /*"$description $name",
            "$description$name",
            "${description.first()} ${name.first()}",*/

        )

        return matchingCombinations.any { it.contains(query, ignoreCase = true) }
    }
}

private val exerciseList = listOf(
    Exercise("Squat", listOf("Quadriceps", "Hamstrings", "Glutes"), R.drawable.ic_account_default),
    Exercise(
        "Deadlift",
        listOf("Hamstrings", "Glutes", "Lower Back"),
        R.drawable.ic_account_default
    ),
    Exercise("Bench Press", listOf("Chest", "Triceps", "Shoulders"), R.drawable.ic_account_default),
    Exercise("Pull-Up", listOf("Back", "Biceps"), R.drawable.ic_account_default),
    Exercise("Shoulder Press", listOf("Shoulders", "Triceps"), R.drawable.ic_account_default)
)

//private val muscleList =
//    listOf("Quadriceps", "Hamstrings", "Glutes", "Chest", "Triceps", "Back", "Biceps", "Shoulders")

