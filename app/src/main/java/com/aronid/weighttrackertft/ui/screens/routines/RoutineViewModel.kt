package com.aronid.weighttrackertft.ui.screens.routines

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

    val customRoutines: StateFlow<List<RoutineModel>> = combine(_allCustomRoutines, _searchText) { routines, text ->
        filterRoutines(routines, text)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val predefinedRoutines: StateFlow<List<RoutineModel>> = combine(_allPredefinedRoutines, _searchText) { routines, text ->
        filterRoutines(routines, text)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            try {
                _allCustomRoutines.value = customRepository.getUserRoutines()
                _allPredefinedRoutines.value = predefinedRepository.getPredefinedRoutines()
            } catch (e: Exception) {
                println("Error loading routines: ${e.message}")
                _allCustomRoutines.value = emptyList()
                _allPredefinedRoutines.value = emptyList()
            }
        }
    }

    private fun filterRoutines(routines: List<RoutineModel>, query: String): List<RoutineModel> {
        return if (query.isEmpty()) {
            routines // Mostrar todas las rutinas si no hay búsqueda
        } else {
            routines.filter { it.matchesSearchQuery(query) }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun clearSearchQuery() {
        _searchText.value = ""
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