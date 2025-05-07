package com.aronid.weighttrackertft.ui.screens.routines.editRoutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditRoutineViewModel @Inject constructor(
    private val routineRepository: RoutineCustomRepository
) : ViewModel() {

    private val _routine = MutableStateFlow<RoutineModel?>(null)
    val routine: StateFlow<RoutineModel?> = _routine.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val exercises: StateFlow<List<ExerciseModel>> = _exercises.asStateFlow()

    private val _allExercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val allExercises: StateFlow<List<ExerciseModel>> = _allExercises.asStateFlow()

    private val _targetMuscles = MutableStateFlow<List<String>>(emptyList())
    val targetMuscles: StateFlow<List<String>> = _targetMuscles.asStateFlow()

    private val _availableMuscles = MutableStateFlow<List<String>>(
        listOf("Pecho", "Tríceps", "Espalda", "Bíceps", "Piernas", "Hombros", "Abdomen")
    )
    val availableMuscles: StateFlow<List<String>> = _availableMuscles.asStateFlow()

    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Initial : State()
    }

    private val _state = MutableStateFlow<State>(State.Initial)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadAllExercises()
    }

    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            try {
                val loadedRoutine = routineRepository.getRoutineById(routineId)
                _routine.value = loadedRoutine
                _exercises.value = routineRepository.getExercisesForRoutine(routineId)
                _targetMuscles.value = loadedRoutine?.targetMuscles ?: emptyList()
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Error al cargar rutina")
            }
        }
    }

    fun updateRoutine(name: String, goal: String, description: String, targetMuscles: List<String>) {
        _state.value = State.Loading
        val updatedRoutine = _routine.value?.copy(
            name = name,
            goal = goal,
            description = description,
            targetMuscles = targetMuscles
        )
        updatedRoutine?.let {
            viewModelScope.launch {
                try {
                    routineRepository.updateRoutine(it, _exercises.value.map { ex -> ex.id })
                    _state.value = State.Success
                } catch (e: Exception) {
                    _state.value = State.Error(e.message ?: "Error al actualizar rutina")
                }
            }
        }
    }

    fun addExercise(exercise: ExerciseModel) {
        if (!_exercises.value.any { it.id == exercise.id }) {
            _exercises.value = _exercises.value + exercise
        }
    }

    fun removeExercise(exerciseId: String) {
        _exercises.value = _exercises.value.filter { it.id != exerciseId }
    }

    private fun loadAllExercises() {
        viewModelScope.launch {
            try {
                _allExercises.value = routineRepository.getAllExercises()
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Error al cargar ejercicios")
            }
        }
    }

    fun updateTargetMuscles(muscles: List<String>) {
        _targetMuscles.value = muscles
    }
}