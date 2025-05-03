package com.aronid.weighttrackertft.ui.screens.routines.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
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
class RoutineDetailsViewModel @Inject constructor(
    private val routinePredefinedRepository: RoutinePredefinedRepository,
    private val customRepository: RoutineCustomRepository,
) : ViewModel() {
    private val _routine = MutableStateFlow<RoutineModel?>(null)
    val routine: StateFlow<RoutineModel?> = _routine.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val exercises: StateFlow<List<ExerciseModel>> = _exercises.asStateFlow()

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _buttonState = MutableStateFlow(ButtonState())
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    init {
        updateButtonConfigs()
    }

    fun updateButtonConfigs() {
        _buttonState.update {
            val (text, layout, state) = getDefaultButtonConfig(true)
            val border = getDefaultBorderConfig()
            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = true,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }

    fun toggleFavorite(routineId: String, isPredefined: Boolean) {
        viewModelScope.launch {
            try {
                customRepository.toggleFavorite(routineId, isPredefined)
                val updatedFavoriteStatus = customRepository.isFavorite(routineId)
                _isFavorite.value = updatedFavoriteStatus
                Log.d("ViewModel", "Favorito actualizado a: $updatedFavoriteStatus")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al alternar favorito: ${e.message}")
            }
        }
    }

    fun loadRoutineDetails(routineId: String, isPredefined: Boolean = false) {
        viewModelScope.launch {
            try {
                println("Loading routine details for ID: $routineId, isPredefined: $isPredefined")
                val loadedRoutine = if (isPredefined) {
                    routinePredefinedRepository.getRoutineById(routineId)?.copy(id = routineId)
                } else {
                    customRepository.getRoutineById(routineId)?.copy(id = routineId)
                }
                _routine.value = loadedRoutine
                println("Routine loaded: ${loadedRoutine?.name}, exercises refs: ${loadedRoutine?.exercises?.size}")

                val loadedExercises = if (isPredefined) {
                    routinePredefinedRepository.getExercisesForRoutine(routineId)
                } else {
                    customRepository.getExercisesForRoutine(routineId)
                }
                _exercises.value = loadedExercises
                println("Exercises loaded: ${loadedExercises.size}, names: ${loadedExercises.map { it.name }}")

                _isFavorite.value = customRepository.isFavorite(routineId)
            } catch (e: Exception) {
                println("Error loading routine details: ${e.message}")
            }
        }
    }
}