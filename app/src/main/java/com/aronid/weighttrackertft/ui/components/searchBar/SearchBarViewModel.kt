package com.aronid.weighttrackertft.ui.components.searchBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class)
class SearchBarViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    val selectedMuscle = _selectedMuscle.asStateFlow()

    private val _exercises = MutableStateFlow(exerciseList)
        }
    }


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

        _searchText.value = ""
        _selectedMuscle.value = null
    }

}

data class Exercise(
    val name: String,
    val muscles: List<String>,
    val image: Int
) {

    fun matchesSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
            muscles.joinToString(" "),
            "$name ${muscles.joinToString(" ")}",
            "$name${muscles.joinToString(" ")}",
            "${name.first()} ${muscles.joinToString("").first()}",

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
