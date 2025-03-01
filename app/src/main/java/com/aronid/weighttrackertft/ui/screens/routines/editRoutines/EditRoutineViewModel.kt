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

    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            val loadedRoutine = routineRepository.getRoutineById(routineId)
            _routine.value = loadedRoutine
            _exercises.value = routineRepository.getExercisesForRoutine(routineId)
        }
    }

    fun updateRoutine(name: String, goal: String, description: String, exerciseIds: List<String>) {
        val updatedRoutine = _routine.value?.copy(
            name = name,
            goal = goal,
            description = description
        )
        updatedRoutine?.let {
            viewModelScope.launch {
                try {
                    routineRepository.updateRoutine(it, exerciseIds)
                } catch (e: Exception) {
                    // Aquí podrías exponer el error a través de un StateFlow para mostrarlo en la UI
                    println("Error al actualizar rutina: ${e.message}")
                }
            }
        }
    }

    fun removeExercise(exerciseId: String) {
        _exercises.value = _exercises.value.filter { it.id != exerciseId }
    }

    fun getAllExercises() {
        viewModelScope.launch {
            _allExercises.value = routineRepository.getAllExercises()
        }
    }

    fun addExercises(newExercises: List<ExerciseModel>) {
        _exercises.value += newExercises
    }
}