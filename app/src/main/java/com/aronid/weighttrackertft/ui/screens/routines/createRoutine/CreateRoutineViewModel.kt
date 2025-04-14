package com.aronid.weighttrackertft.ui.screens.routines.createRoutine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoutineViewModel @Inject constructor(
    private val routineCustomRepository: RoutineCustomRepository,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _availableMuscles = MutableStateFlow<List<String>>(listOf(
        "Pecho",
        "Tríceps",
        "Espalda",
        "Bíceps",
        "Piernas",
        "Hombros",
        "Abdomen"
    ))
    val availableMuscles: StateFlow<List<String>> = _availableMuscles.asStateFlow()
    private val _customRoutines = MutableStateFlow<List<RoutineModel>>(emptyList())
    val customRoutines: StateFlow<List<RoutineModel>> = _customRoutines.asStateFlow()

    private val _availableExercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val availableExercises: StateFlow<List<ExerciseModel>> = _availableExercises.asStateFlow()

    sealed class State {
        object Loading : State()
        data class Success(val routineId: String) : State()
        data class Error(val message: String) : State()
        object Initial : State()
    }

    private val _state = MutableStateFlow<State>(State.Initial)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _buttonState = MutableStateFlow(ButtonState())
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    init {
        loadAvailableExercises()
    }

    fun createRoutine(name: String, goal: String, description: String, exerciseIds: List<String>, targetMuscles: List<String>) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
                val exerciseRefs = exerciseIds.map { routineCustomRepository.getExerciseReference(it) }
                val routine = RoutineModel(
                    name = name,
                    goal = goal,
                    description = description,
                    userId = userId,
                    exercises = exerciseRefs,
                    targetMuscles = targetMuscles
                )

                val routineId = routineCustomRepository.createRoutine(routine)
                _state.value = State.Success(routineId)
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun loadAvailableExercises() {
        viewModelScope.launch {
            try {
                val exercises = routineCustomRepository.getAllExercises() // Método para obtener ejercicios
                _availableExercises.value = exercises
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading exercises: ${e.message}")
            }
        }
    }

    fun checkFormValidity(name: String, goal: String, description: String, selectedExerciseIds: List<String>, targetMuscles: List<String>) {
        val isValid = name.isNotBlank() && goal.isNotBlank() && description.isNotBlank() && selectedExerciseIds.isNotEmpty() && targetMuscles.isNotEmpty()
        updateButtonConfigs(isValid)
    }

    private fun updateButtonConfigs(isValid: Boolean) {
        _buttonState.update {
            val (text, layout, state) = getDefaultButtonConfig(isValid)
            val border = getDefaultBorderConfig()
            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = isValid,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }

}

