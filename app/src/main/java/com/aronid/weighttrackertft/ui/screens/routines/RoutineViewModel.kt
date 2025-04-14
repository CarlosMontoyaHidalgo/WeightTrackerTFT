package com.aronid.weighttrackertft.ui.screens.routines

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RoutineFilterType {
    ALL, CUSTOM_ONLY, PREDEFINED_ONLY, FAVORITES
}

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val customRepository: RoutineCustomRepository,
    private val predefinedRepository: RoutinePredefinedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ButtonState())
    val state: StateFlow<ButtonState> = _state

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _allCustomRoutines = MutableStateFlow<List<RoutineModel>>(emptyList())
    private val _allPredefinedRoutines = MutableStateFlow<List<RoutineModel>>(emptyList())
    private val _selectedRoutines = mutableStateMapOf<String, Boolean>()
    private val _filterType = MutableStateFlow(RoutineFilterType.ALL)

    // NEW: Added flows for UI state management in RoutineSearchBar
    private val _showCheckbox = MutableStateFlow(false)
    val showCheckbox: StateFlow<Boolean> = _showCheckbox.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    val filterType: StateFlow<RoutineFilterType> = _filterType.asStateFlow()
    val selectedRoutines: SnapshotStateMap<String, Boolean> = _selectedRoutines

    val customRoutines: StateFlow<List<RoutineModel>> = combine(
        _allCustomRoutines,
        _searchText,
        _filterType
    ) { routines, text, filter ->
        when (filter) {
            RoutineFilterType.ALL -> filterRoutines(routines, text)
            RoutineFilterType.CUSTOM_ONLY -> filterRoutines(routines, text)
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val predefinedRoutines: StateFlow<List<RoutineModel>> = combine(
        _allPredefinedRoutines,
        _searchText,
        _filterType
    ) { routines, text, filter ->
        when (filter) {
            RoutineFilterType.ALL -> filterRoutines(routines, text)
            RoutineFilterType.PREDEFINED_ONLY -> filterRoutines(routines, text)
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRoutines: StateFlow<List<RoutineModel>> = combine(
        _allCustomRoutines,
        _allPredefinedRoutines,
        _searchText,
        _filterType
    ) { custom, predefined, text, filter ->
        if (filter == RoutineFilterType.FAVORITES || filter == RoutineFilterType.ALL) {
            filterRoutines((custom + predefined).filter { it.isFavorite }, text)
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadRoutines()
    }

//    private fun loadRoutines() {
//        viewModelScope.launch {
//            try {
//                _allCustomRoutines.value = customRepository.getUserRoutines()
//                _allPredefinedRoutines.value = predefinedRepository.getPredefinedRoutines()
//                Log.d("RoutineViewModel", "Routines loaded: ${_allCustomRoutines.value.size} custom, ${_allPredefinedRoutines.value.size} predefined")
//            } catch (e: Exception) {
//                Log.e("RoutineViewModel", "Error loading routines", e)
//                _allCustomRoutines.value = emptyList()
//                _allPredefinedRoutines.value = emptyList()
//            }
//        }
//    }

    private fun filterRoutines(routines: List<RoutineModel>, query: String): List<RoutineModel> {
        return if (query.isEmpty()) routines
        else routines.filter { it.name.contains(query, true) }
    }

    fun setFilterType(type: RoutineFilterType) {
        _filterType.value = type
    }

    fun toggleSelection(routineId: String, selected: Boolean) {
        if (selected) _selectedRoutines[routineId] = true
        else _selectedRoutines.remove(routineId)
    }

    fun deleteSelectedRoutines(onResult: (Boolean) -> Unit) {
        val idsToDelete = _selectedRoutines.keys.toList()
        viewModelScope.launch {
            try {
                customRepository.deleteRoutines(idsToDelete)
                _selectedRoutines.clear()
                loadRoutines()
                onResult(true)
            } catch (e: Exception) {
                Log.e("RoutineViewModel", "Error deleting routines: ${e.message}")
                onResult(false)
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun clearSearchQuery() {
        _searchText.value = ""
    }

    fun toggleFavorite(routineId: String, isPredefined: Boolean) {
        viewModelScope.launch {
            try {
                customRepository.toggleFavorite(routineId, isPredefined)
                loadRoutines() // Recargar datos después de cambiar favorito
            } catch (e: Exception) {
                Log.e("RoutineViewModel", "Error al actualizar favorito", e)
            }
        }
    }

    fun toggleFavoriteOptimistic(routineId: String, isPredefined: Boolean) {
        viewModelScope.launch {
            try {
                if (isPredefined) {
                    _allPredefinedRoutines.update { list ->
                        list.map {
                            if (it.id == routineId) it.copy(isFavorite = !it.isFavorite)
                            else it
                        }
                    }
                } else {
                    _allCustomRoutines.update { list ->
                        list.map {
                            if (it.id == routineId) it.copy(isFavorite = !it.isFavorite)
                            else it
                        }
                    }
                }

                // Persistir en Firestore
                customRepository.toggleFavorite(routineId, isPredefined)

            } catch (e: Exception) {
                // Revertir cambios si falla
                loadRoutines()
            }
        }
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            try {
                // Obtener rutinas y favoritos
                val custom = customRepository.getUserRoutines()
                val predefined = predefinedRepository.getPredefinedRoutines()
                val favorites = customRepository.getUserFavorites()
                val favoriteIds = favorites.map { it.routineId }.toSet()

                // Actualizar estado de favoritos
                _allCustomRoutines.value = custom.map { it.copy(isFavorite = it.id in favoriteIds) }
                _allPredefinedRoutines.value =
                    predefined.map { it.copy(isFavorite = it.id in favoriteIds) }

            } catch (e: Exception) {
                Log.e("RoutineViewModel", "Error cargando rutinas", e)
            }
        }
    }

    // NEW: Methods to manage UI state
    fun toggleCheckbox() {
        _showCheckbox.value = !_showCheckbox.value
        if (!_showCheckbox.value) {
            _selectedRoutines.clear()
        }
    }

    fun showDeleteDialog(show: Boolean) {
        _showDeleteDialog.value = show
    }

    private fun updateButtonConfigs(isEmpty: Boolean) {
        _state.update {
            val (text, layout, state) = getDefaultButtonConfig(true)
            val border = getDefaultBorderConfig()
            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = isEmpty,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }
}