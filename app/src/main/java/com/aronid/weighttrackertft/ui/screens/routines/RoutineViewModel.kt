package com.aronid.weighttrackertft.ui.screens.routines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.questionnaire.UserQuestionnaireState
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routinePredefinedRepository: RoutinePredefinedRepository,
    private val routineCustomRepository: RoutineCustomRepository,

    ) : ViewModel() {

    private val _state = MutableStateFlow(ButtonState())
    val state: StateFlow<ButtonState> = _state

    private val _predefinedRoutines = MutableStateFlow<List<RoutineModel>>(emptyList())
    val predefinedRoutines: StateFlow<List<RoutineModel>> = _predefinedRoutines.asStateFlow()

    private val _customRoutines = MutableStateFlow<List<RoutineModel>>(emptyList())
    val customRoutines: StateFlow<List<RoutineModel>> = _customRoutines.asStateFlow()

    private val _selectedRoutine = MutableStateFlow<RoutineModel?>(null)
    val selectedRoutine: StateFlow<RoutineModel?> = _selectedRoutine.asStateFlow()


    init {
        loadPredefinedRoutines()
        loadCustomRoutines()
        updateButtonConfigs(isEmpty = _customRoutines.value.isEmpty())
    }

    private fun loadPredefinedRoutines() {
        viewModelScope.launch {
            try {
                val routines = routinePredefinedRepository.getPredefinedRoutines()
                _predefinedRoutines.value = routines
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading routines: ${e.message}")
            }
        }
    }

    private fun loadCustomRoutines() {
        viewModelScope.launch {
            try {
                val routines = routineCustomRepository.getUserRoutines()
                Log.i("RoutineViewModel", "customRoutines: $routines") // 🔍 Depuración
                _customRoutines.value = routines
                updateButtonConfigs(isEmpty = routines.isEmpty())
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading routines: ${e.message}")
            }
        }
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